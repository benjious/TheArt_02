package com.example.benjious.theart_02.binderPool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by benjious on 2016/11/3.
 */

public class BinderPoolService extends Service {
    public static final String TAG="BinderPoolService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
