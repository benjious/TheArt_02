package com.example.benjious.theart_02.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.benjious.theart_02.R;
import com.example.benjious.theart_02.manager.UseManager;
import com.example.benjious.theart_02.model.User;
import com.example.benjious.theart_02.util.MyConstants;
import com.example.benjious.theart_02.util.MyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.content.ContentValues.TAG;

/**
 * 这个类跑在私有线程，和另外两个形成多线程
 * Created by benjious on 2016/10/25.
 */

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("time", System.currentTimeMillis());
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  Log.d("SecondActivity", "  " + UseManager.sUserId);
        recoverFromFile();
    }

    private void recoverFromFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user=null;
                File cachedFile = new File(MyConstants.CACHE_FILE_PATH);
                if (cachedFile.exists()) {


                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(new FileInputStream(cachedFile));
                        //读取这个文件中的对象,反序列化
                        user = (User) objectInputStream.readObject();
                        Log.d(TAG, "recover user:" + user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        MyUtils.close(objectInputStream);
                    }
                }

            }
        }).start();

    }
}
