package com.g19p2.g19p2app;

/**
 * Created by robin on 2018-04-02.
 */

public class Lock {
    private String lock_id, lock_url;

    public Lock(String lock_id, String lock_url) {
        this.lock_id = lock_id; this.lock_url = lock_url;
    }

    public String getLock_id() {
        return lock_id;
    }

    public String getLock_url() {
        return lock_url;
    }
}
