from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:nZVLJudP4Qaz@localhost/291G19P2'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)


class User(db.Model):
    __tablename__ = 'users'
    uid = db.Column('uid', db.Integer, primary_key=True)  # auto increment is set automatically
    username = db.Column('username', db.Unicode)
    password = db.Column('password', db.Unicode)
    email = db.Column('email', db.Unicode)
    settings = db.Column('settings', db.Unicode)
    tracking = db.Column('tracking', db.Unicode)
    rank = db.Column('rank', db.Integer)
