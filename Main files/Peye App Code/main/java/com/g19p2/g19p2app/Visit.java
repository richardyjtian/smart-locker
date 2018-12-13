package com.g19p2.g19p2app;

/**
 * Created by robin on 2018-03-18.
 * Class describing the characteristics of a visit.
 */

public class Visit {

    private String date;
    private String time;
    private boolean authorized;

    public Visit(String date, String time, boolean authorized) {
        this.date = date;
        this.time = time;
        this.authorized = authorized;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public boolean getAuthorized() {
        return authorized;
    }
}
