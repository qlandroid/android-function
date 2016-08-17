package com.example.myapplication;

import android.app.Activity;
import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/14.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
    private List<Activity> mList = new LinkedList<Activity>();
    private static MyApp instance;

    public MyApp() {
    }
    public synchronized static MyApp getInstance() {
        if (null == instance) {
            instance = new MyApp();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
