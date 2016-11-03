package com.example.benjious.theart_02.messager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.benjious.theart_02.R;
import com.example.benjious.theart_02.util.MyConstants;

/**
 * 这里我绑定一个服务,当服务绑定成功时,假设我们拿到服务端的Messenger对象,然后利用他发送信息,
 * 处理的方法肯定是在服务端那里
 *
 * Created by benjious on 2016/10/30.
 */

public class MessengerActivity extends Activity {
    private static final String TAG = "MessengerActivity";
    private Messenger mService;

    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_SERVICE:
                    Log.d(TAG, "xyz  handleMessage: receive msg from Service: "+msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new android.os.Messenger(service);
            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLENT);
            Bundle data = new Bundle();
            data.putString("msg", "hello,this is client");
            msg.setData(data);
            msg.replyTo=mGetReplyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
