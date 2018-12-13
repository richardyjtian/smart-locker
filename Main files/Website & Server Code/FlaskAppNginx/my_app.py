#!/usr/bin/python3.6
# https://stackoverflow.com/questions/15279793/how-to-invoke-a-specific-python-version-within-a-script-py-windows
# https://askubuntu.com/questions/528009/how-to-tell-ubuntu-to-use-python-3-4-instead-of-2-7
from flask import (Flask, render_template, flash, request, url_for,
                   redirect, session, jsonify, g, )
import sys
from wtforms import Form, BooleanField, StringField, PasswordField, validators
from flask_socketio import SocketIO, emit, send

from pyutil.mysql_connection import connection
from passlib.apps import custom_app_context as pwd_context
from passlib.hash import sha256_crypt
from pymysql import escape_string as thwart  # for SQL Injection
from itsdangerous import (TimedJSONWebSignatureSerializer
                          as Serializer, BadSignature, SignatureExpired)
import gc
import os
from functools import wraps
import requests
import json
import datetime

from flask_httpauth import HTTPBasicAuth
from flask_sqlalchemy import SQLAlchemy

import random
from bokeh.models import (HoverTool, FactorRange, Plot, LinearAxis, Grid,
                          Range1d)
from bokeh.models.glyphs import VBar
from bokeh.plotting import figure
from bokeh.charts import Bar
from bokeh.embed import components
from bokeh.models.sources import ColumnDataSource

first_app = Flask(__name__)
sio = SocketIO(first_app, engineio_logger=True)  # wrap first_app with flask socket io

# https://stackoverflow.com/questions/35657821/the-session-is-unavailable-because-no-secret-key-was-set-set-the-secret-key-on/35657961
# it is just A key, and this is not a good practice. We will change it later
first_app.secret_key = b'\xc0\xa2V\xe1\xda\xfd@2B\xcc\xc0\x16U0\x88\x1c\xa6W\xe2^\xa6\x8e\xe4l'
first_app.config.update(
    TEMPLATES_AUTO_RELOAD=True
)

# use a flaks mysqlalchemy to map sql columns to python objects
first_app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:nZVLJudP4Qaz@localhost/291G19P2'
first_app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
first_app.config['SQLALCHEMY_COMMIT_ON_TEARDOWN'] = True

db = SQLAlchemy(first_app)
auth = HTTPBasicAuth()


# user model
class User(db.Model):
    __tablename__ = 'users'
    uid = db.Column('uid', db.Integer, primary_key=True)  # auto increment is set automatically
    username = db.Column('username', db.Unicode)
    password = db.Column('password', db.Unicode)
    email = db.Column('email', db.Unicode)
    settings = db.Column('settings', db.Unicode)
    tracking = db.Column('tracking', db.Unicode)
    rank = db.Column('rank', db.Integer)

    def hash_password(self, password):
        self.password = pwd_context.encrypt(password)

    def verify_password(self, password):
        return pwd_context.verify(password, self.password)

    def generate_auth_token(self, expiration=600):
        s = Serializer(first_app.secret_key, expires_in=expiration)
        return s.dumps({'uid': self.uid})

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(first_app.secret_key)
        try:
            data = s.loads(token)
        except SignatureExpired:
            return None  # valid token, but expired
        except BadSignature:
            return None  # invalid token
        user = User.query.get(data['uid'])
        return user


@auth.verify_password
def verify_password(username_or_token, password):
    # first try to authenticate by token
    # history is only saved someone wants to use your username and password to authenticate
    user = User.verify_auth_token(username_or_token)
    if not user:

        # try to authenticate with username/password
        user = User.query.filter_by(username=username_or_token).first()
        if not user:
            return False  # username is wrong, we are not gonna save it to the history
        if not user.verify_password(password):
            # denied
            history = LockHistory(username=username_or_token,
                                  time=datetime.datetime.now().strftime("%I:%M%p on %B %d, %Y"), status='denied')
            db.session.add(history)
            db.session.commit()
            return False
        else:
            # authorized
            history = LockHistory(username=username_or_token,
                                  time=datetime.datetime.now().strftime("%I:%M%p on %B %d, %Y"), status='authorized')
            db.session.add(history)
            db.session.commit()

    g.user = user
    return True


# lock model
class Locks(db.Model):
    __tablename__ = 'locks'
    lid = db.Column('lid', db.Integer, primary_key=True)  # auto-increment automatically
    username = db.Column('username', db.Unicode)
    streamURL = db.Column('streamURL', db.Unicode)
    # lock id is unique


# lock history model
class LockHistory(db.Model):
    __tablename__ = 'lock_history'
    hid = db.Column('hid', db.Integer, primary_key=True)  # auto-increment automatically
    username = db.Column('username', db.Unicode)
    time = db.Column('time', db.Unicode)
    status = db.Column('status', db.Unicode)  # status is either "authorized" or "denied"


api_username = "iwant2use8pi"
api_password = "291piapiapiapia"

# api links ----------------------------------------------------------------
cameraControl = {
    'left': 'https://nubblier-octopus-5424.dataplicity.io/cameraControl/api/v1.0/tasks/2',
    'right': 'https://nubblier-octopus-5424.dataplicity.io/cameraControl/api/v1.0/tasks/1',
    'start streaming': 'https://nubblier-octopus-5424.dataplicity.io/cameraControl/api/v1.0/tasks/3',
    'stop streaming': 'https://nubblier-octopus-5424.dataplicity.io/cameraControl/api/v1.0/tasks/4'
}

lockControl = {
    'lock': 'https://nubblier-octopus-5424.dataplicity.io/lockControl/api/v1.0/tasks/1',
    'unlock': 'https://nubblier-octopus-5424.dataplicity.io/lockControl/api/v1.0/tasks/2',
}


# user login, registering, authentication system --------------------------------
def logout_required(f):
    @wraps(f)
    def wrap(*args, **kwargs):
        if 'logged_in' in session:
            flash("You need to logout first!")
            return redirect(url_for('dashboard'))
        else:
            return f(*args, *kwargs)

    return wrap


@first_app.route('/login/', methods=["GET", "POST"])
@logout_required
def login_page():
    # return render_template("login.html")

    error = ''
    try:
        c, conn = connection()
        if request.method == "POST":
            data = c.execute("SELECT * FROM users WHERE username = (%s)",
                             thwart(request.form['username']))

            data = c.fetchone()[2]

            # if sha256_crypt.verify(request.form['password'], data):
            if pwd_context.verify(request.form['password'], data):
                session['logged_in'] = True
                session['username'] = request.form['username']
                history = LockHistory(username=request.form['username'], time=datetime.datetime.now().strftime("%I:%M%p on %B %d, %Y"), status='authorized')
                db.session.add(history)
                db.session.commit()

                flash("You are now logged in.")
                return redirect(url_for('dashboard'))
            else:
                history = LockHistory(username=request.form['username'], time=datetime.datetime.now().strftime("%I:%M%p on %B %d, %Y"), status='denied')
                db.session.add(history)
                db.session.commit()
                error = "Invalid credentials, try again."

        gc.collect()
        return render_template("login.html", error=error)

    except Exception as e:
        # flash(e)
        error = "Invalid credentials, try again."
        return render_template("login.html", error=error)


def login_required(f):
    @wraps(f)
    def wrap(*args, **kwargs):
        if 'logged_in' in session:
            return f(*args, *kwargs)
        else:
            flash("You need to login first!")
            return redirect(url_for('login_page'))

    return wrap


@first_app.route("/logout/")
@login_required  # you have to login first before log out
def logout():
    session.clear()
    flash("You have been logged out.")
    gc.collect()
    return redirect(url_for('dashboard'))


class SignupForm(Form):
    username = StringField('Username', [validators.Length(min=4, max=20)])
    email = StringField('Email Address', [validators.Length(min=6, max=50), validators.Email("Invalid email")])
    password = PasswordField('New Password', [
        validators.DataRequired(),
        validators.EqualTo('confirm', message='Passwords must match')
    ])
    confirm = PasswordField('Repeat Password')
    accept_tos = BooleanField('I accept the <a href = "/tos/">Terms of Service</a>',
                              [validators.DataRequired()])


@first_app.route('/register/', methods=["GET", "POST"])
def register_page():
    try:
        form = SignupForm(request.form)  # create a form from html

        # user submit a form for registering
        if request.method == "POST" and form.validate():  # check if the method is post and validate the form
            username = form.username.data  # get data from the wrtform we created
            email = form.email.data
            # password = sha256_crypt.encrypt((str(form.password.data)))  # encrypt password
            password = pwd_context.encrypt((str(form.password.data)))  # encrypt password
            c, conn = connection()  # connection and cursor

            # return rows of data from sql db
            x = c.execute("SELECT * FROM users WHERE username = (%s)", (thwart(username),))

            # if length of rows is greater than 1, that means that username is already taken
            if int(x) > 0:
                flash("That username is already taken, please choose another")
                return render_template('register.html', form=form)  # we will make the html later
            else:
                c.execute("INSERT INTO users (username, password, email, tracking) VALUES (%s, %s, %s, %s)",
                          (thwart(username)
                           , thwart(password), thwart(email), thwart("/dashboard/")))  # not sure what tracking is doing
                conn.commit()  # save the changes in db

                flash("Thanks for registering!")
                c.close()
                conn.close()
                gc.collect()  # keep memory waste down.

                # session is used to track users
                session["logged_in"] = True
                session["username"] = username

                return redirect(url_for('dashboard'))

        # nothing has happened
        return render_template("register.html", form=form)
    except Exception as e:
        return render_template("500.html", error=str(e))


# Flask routing the user to different pages --------------------------------
@first_app.route('/')
def homepage():
    try:
        return render_template("dashboard.html")
    except Exception as e:
        return render_template("500.html", error=str(e))


@first_app.route('/dashboard/')
def dashboard():
    try:
        return render_template("dashboard.html")
    except Exception as e:
        return render_template("500.html", error=str(e))


@first_app.route('/statistics/')
@login_required
def statistics():
    locklistSet = []
    username = session['username']
    userLocks = Locks.query.filter_by(username=username).all()
    if userLocks is not None:
        for lock in userLocks:
            locklistSet.append({"lid": str(lock.lid), "streamURL": lock.streamURL, "username": lock.username})

    denied = 0
    authorized = 0
    historySet = []
    histories = LockHistory.query.filter_by(username=username).all()
    if histories is not None:
        for history in histories:
            historySet.append(
                {"hid": str(history.hid), "username": history.username, "time": history.time, "status": history.status})
            if history.status == "denied":
                denied += 1
            else:
                authorized += 1

    historySet = reversed(historySet)

    try:
        return render_template("statistics.html", locklistSet=locklistSet,
                               historySet=historySet, denied=denied, authorized=authorized)
    except Exception as e:
        return render_template("500.html", error=str(e))


@first_app.route('/chat/')
@login_required  # you have to login first before log out
def load_chat():
    try:
        flash("You can chat with other users on this page")
        return render_template("chat.html")
    except Exception as e:
        return render_template("500.html", error=str(e))


@first_app.route('/video/')
@login_required  # you have to login first before log out
def load_video():
    # return ":( This page is now under development..."
    return render_template("video.html")


@first_app.route('/tos/')
def load_terms():
    # return ":( This page is now under development..."
    return render_template("terms.html")


#  RESTful APIs
@first_app.route('/api/users', methods=['POST'])
def new_user():
    username = request.json.get('username')
    password = request.json.get('password')
    email = request.json.get('email')
    if username is None or password is None or email is None:
        abort(400)  # missing arguments
    if User.query.filter_by(username=username).first() is not None:
        abort(400)  # existing user
    user = User(username=username, email=email, tracking='/dashboard/')
    user.hash_password(password)
    db.session.add(user)
    db.session.commit()
    return (jsonify({'username': user.username, 'email': user.email}), 201,
            {'Location': url_for('get_user', id=user.uid, _external=True)})


@first_app.route('/api/users/<int:id>')
def get_user(id):
    user = User.query.get(id)
    if not user:
        abort(400)
    return jsonify({'username': user.username})


@first_app.route('/api/token/<int:duration>')
@auth.login_required
def get_auth_token(duration):
    token = g.user.generate_auth_token(expiration=duration)
    return jsonify({'token': token.decode('ascii'), 'duration': duration})


@first_app.route('/api/resource')
@auth.login_required
def get_resource():
    return jsonify({'data': 'Hello, %s!' % g.user.username})


@first_app.route('/api/unlock/<int:lock_id>', methods=['GET'])
@auth.login_required
def unlock(lock_id):
    return jsonify({('Lock ' + str(lock_id) + ' Status'): 'Authorized.'})


# additional APIs
@first_app.route('/api/resource/streamURL', methods=['GET'])
@auth.login_required
def get_stream_url():
    # username = request.json.get('username')
    # lid = request.json.get('lid')
    username = request.args.get('username')
    lid = request.args.get('lid')
    lid = int(lid)

    if username is None or lid is None:
        abort(400)

    userLocks = Locks.query.filter_by(username=username).all()
    if userLocks is None:
        abort(400)

    for lock in userLocks:
        if lock.lid == lid:
            streamURL = {'streamURL': lock.streamURL}
            return jsonify(streamURL)

    abort(400)


@first_app.route('/api/resource/lockList/<username>', methods=['GET'])
@auth.login_required
def get_locklist(username):
    locklistSet = []
    if username is None:
        abort(400)  # missing arguments
    lockList = Locks.query.filter_by(username=username).all()
    if lockList is None:
        abort(400)

    for lock in lockList:
        locklistSet.append({"lid": str(lock.lid), "streamURL": lock.streamURL})

    return json.dumps(locklistSet)


@first_app.route('/api/resource/entry_history/<username>', methods=['GET'])
@auth.login_required
def get_history(username):
    historySet = []
    histories = LockHistory.query.filter_by(username=username).all()
    if histories is not None:
        for history in histories:
            historySet.append(
                {"hid": str(history.hid), "username": history.username, "time": history.time, "status": history.status})

    return json.dumps(historySet)


@first_app.route('/api/addLock', methods=['POST'])
@auth.login_required
def add_lock():
    username = request.json.get('username')
    streamURL = request.json.get('streamURL')
    if username is None or streamURL is None:
        abort(400)  # missing arguments
    if Locks.query.filter_by(streamURL=streamURL).first() is not None:
        abort(400)  # existing streaming address
    lock = Locks(username=username, streamURL=streamURL)
    db.session.add(lock)
    db.session.commit()
    return jsonify({'lid': lock.lid, 'username': lock.username, 'streamURL': lock.streamURL}), 201


# error handler for 404 pages -----------------------------------------------------------
@first_app.errorhandler(404)
def page_not_found(e):
    return render_template("404.html", error=str(e))


# socket io handler
@sio.on('my_event', namespace='/chat')
def handleMessage(msg):
    print('Message: ' + msg)
    emit('my_response', msg, broadcast=True)


# communication with pi ----------------------------------------------------------------
@sio.on('pi_event', namespace='/pi')
def handlePiEvent(msg):
    if msg in cameraControl:
        r = requests.get(cameraControl[msg], auth=(api_username, api_password))
        str_json = json.dumps(r.json())
        emit('pi_response', str_json)
    else:
        emit('pi_response', msg)


# launch this app
if __name__ == "__main__":
    socketio.run(first_app, debug=False)
