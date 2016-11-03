package com.example.benjious.theart_02.socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.benjious.theart_02.R;
import com.example.benjious.theart_02.util.MyUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 这个客户端,用于发送消息
 * connetTCP()方法中绑定了一个 mPrintWriter,那么当这个mPrintWriter有信息时就会传输信息给
 *  服务端
 * Created by benjious on 2016/11/2.
 */

public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.msg_container)
    TextView mMessageTextView;
    @Bind(R.id.msg)
    EditText mMessageEditText;
    @Bind(R.id.send)
    Button mSendButtton;

    private Socket mClientSocket;
    private PrintWriter mPrintWriter;

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGR_SOCKET_CONNECTED = 2;

    public static final String TAG = "TCPClientActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG: {
                    mMessageTextView.setText(mMessageTextView.getText() + (String) msg.obj);
                    break;
                }
                case MESSAGR_SOCKET_CONNECTED: {
                    mSendButtton.setEnabled(true);
                    break;
                }

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        ButterKnife.bind(this);
        mSendButtton.setOnClickListener(this);
        Intent intent = new Intent(this, TCPServerService.class);
        startService(intent);
        Log.d(TAG, "xyz  onCreate: ");
        new Thread() {
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    private void connectTCPServer() {
        Log.d(TAG, "xyz  connectTCPServer: connectTCP");
        Socket socket = null;
        //回去看TIJ,捕获到异常后,还会继续在while()循环中执行吗
        while (socket == null) {
            try {
                Log.d(TAG, "xyz  connectTCPServer: 连接中");
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                //true表示自动flush,
                mPrintWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
                //连上了就要处理,要切换到主线程中,所以要用handler
                mHandler.sendEmptyMessage(MESSAGR_SOCKET_CONNECTED);
                System.out.print("connect tcp server succeed");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                System.out.print("connect tcp server failed retry....");
            }

        }

        try {
            //接收服务器端的信息
            //既然连接到了服务器,接下来就是查看有没有消息发过来了,如果有的话就需要处理
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = reader.readLine();
                if (msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "server " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget();
                }
            }
            System.out.println("quit....");
            MyUtils.close(mPrintWriter);
            MyUtils.close(reader);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        if (v == mSendButtton) {
            final String msg = mMessageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                mPrintWriter.println(msg);
                mMessageEditText.setText("");
                String time = formatDateTime(System.currentTimeMillis());
                final String showedMsg = "self " + time + ":" + msg + "\n";
                mMessageTextView.setText(mMessageTextView.getText() + showedMsg);

            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long time) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
