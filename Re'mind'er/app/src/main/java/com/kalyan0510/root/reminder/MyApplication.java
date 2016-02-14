package com.kalyan0510.root.reminder;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by root on 13/2/16.
 */
public class MyApplication extends Application {
    private static final String APP_ID = "1i9zwapMoXmMkxByhj0XRGX8plvfWmGpf0VvxfFf";
    private static final String CLIENT_KEY = "mmLshv8ixM0uYKloYj1Q7SWcfIP9PRequw0aMzZv";
    private static Integer x=15;

    public static void setX(Integer x) {
        MyApplication.x = x;
    }

    public static int getX() {
        return x.intValue();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, APP_ID, CLIENT_KEY);
    }
}
