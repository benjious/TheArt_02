package com.example.benjious.theart_02.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.benjious.theart_02.R;

import static android.content.ContentValues.TAG;

/**
 * 包名的远程线程，是全局线程，其他应用可以通过ShareUID方式可以和他跑在同一个线程中
 * Created by benjious on 2016/10/25.
 */

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ThirdActivity.this, MainActivity.class);
                intent.putExtra("time", System.currentTimeMillis());
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreate");
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


}
