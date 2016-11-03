package com.example.benjious.theart_02.activity;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.example.benjious.theart_02.util.MyUtils;


/**
 * Created by benjious on 2016/10/25.
 */

public class MyApplication extends Application {
    private static final String TAG="MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = MyUtils.getProcessName(getApplicationContext(), Process.myPid());
        Log.d(TAG, "application start, process name: " + processName);

    }
}
