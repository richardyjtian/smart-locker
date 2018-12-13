package com.g19p2.g19p2app;

import android.os.AsyncTask;

import org.json.JSONObject;

/**
 * Created by robin on 2018-03-22.
 * Client that gets replies by sending requests to a server.
 *
 * Edited by Amir 2018-03-31
 * Changed to a TCP client which operates in threads
 */

public class G19P2Client extends AsyncTask<String, Void, String> {

    Constants constants = new Constants();

    public static final String
            REQUEST_LOGIN = "0",
            REQUEST_GET_LOCKS = "1",
            REQUEST_GET_STREAM_URL = "2",
            REQUEST_GET_VISITS = "4",
            REQUEST_ADD_LOCK = "5";

    protected String doInBackground(String... args) {
        switch(args[0])
        {
            case REQUEST_LOGIN:
                return login();
            case REQUEST_GET_LOCKS:
                return locks();
            case REQUEST_GET_STREAM_URL:
                return stream(args[1]);
            case REQUEST_GET_VISITS:
                return visits();
            case REQUEST_ADD_LOCK:
                return addlock(args[1]);
        }

        return "Error";
    }

    private String login(){
        String s;
        try{
            // curl -u username:password -i https://hizhh.me/api/token/6000
            HttpRequest con = HttpRequest.get("https://hizhh.me/api/token/6000").basic(constants.userName, constants.password);
            int result = con.code();
            if(result == 200)
            {
                JSONObject jo = new JSONObject(con.body());
                constants.token = jo.getString("token");
                constants.nfc_tag = constants.token;
                s = "2";
            } else if(result == 500){
                s = "1";
            } else
                s = "0";
            con.disconnect();
        } catch(Exception e){
            s = "Exception";
        }
        return s;
    }

    private String stream(String lockID){
        String s;
        try{
            // curl -u token:password "https://hizhh.me/api/resource/streamURL?username=username&lid=lockID"
            HttpRequest con = HttpRequest.get("https://hizhh.me/api/resource/streamURL", true, "username", constants.userName, "lid", lockID)
                    .basic(constants.token, "something");
            int result = con.code();
            if(result == 200)
            {
                JSONObject url = new JSONObject(con.body());
                s = url.getString("streamURL");
            } else
                s =  "Connection Issue: " + String.valueOf(result);
            con.disconnect();
        } catch(Exception e){
            s = "Exception";
        }
        return s;
    }

    private String locks(){
        String s;
        try{
            // curl -u newnewtest:password -i -X GET https://hizhh.me/api/resource/lockList/<username>
            HttpRequest con = HttpRequest.get("https://hizhh.me/api/resource/lockList/" + constants.userName).basic(constants.token, "something");
            int result = con.code();
            if(result == 200)
                s = con.body();
            else
                s = "Connection Issue: " + String.valueOf(result);
            con.disconnect();
        } catch(Exception e){
            s = "Exception";
        }
        return s;
    }

    private String visits(){
        String s;
        try{
            // curl -u <token>:<whatever> -i -X GET https://hizhh.me/api/resource/entry_history/<username>
            HttpRequest con = HttpRequest.get("https://hizhh.me/api/resource/entry_history/" + constants.userName).basic(constants.token, "something");
            int result = con.code();
            if(result == 200)
                s = con.body();
            else
                s = "Connection Issue: " + String.valueOf(result);
            con.disconnect();
        } catch(Exception e){
            s = "Exception";
        }
        return s;
    }

    private String addlock(String URL){
        String s;
        try{
            // curl -u <token>:<whatever> -i -X POST -H "Content-Type: application/json" -d '{"username":<username>,"streamURL":<streamURL>}' https://hizhh.me/api/addLock
            JSONObject jo = new JSONObject().put("username",constants.userName).put("streamURL", URL);
            HttpRequest con = HttpRequest.post("https://hizhh.me/api/addLock").basic(constants.token, "something")
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(jo.toString());
            int result = con.code();
            if(result == 201)
                s = con.body();
            else
                s = "Connection Issue " + String.valueOf(result);
        } catch(Exception e){
            s = "Exception";
        }
        return s;
    }
}