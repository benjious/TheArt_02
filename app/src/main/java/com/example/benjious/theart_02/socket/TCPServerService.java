package com.example.benjious.theart_02.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.benjious.theart_02.util.MyUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by benjious on 2016/11/2.
 */

public class TCPServerService extends Service {
    private boolean mIsServiceDestoryed = false;
    public static final String TAG="TCPServerService";
    private String[] mDefinedMessages = new String[]{
            "你好啊，哈哈",
            "请问你叫什么名字呀？",
            "今天北京天气不错啊，shy",
            "你知道吗？我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假。"
    };


    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        Log.d(TAG, "xyz  onCreate: 服务启动了");
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TcpServer implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                //监听8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("eatablish tcp server failed ,port :8688");
                e.printStackTrace();
                return;
            }

            while (!mIsServiceDestoryed) {
                //接收客户端请求
                try {
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }


        private void responseClient(Socket client) throws IOException {
            //用于接收客户端消息
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //用于发送信息给客户端
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
            out.print("欢迎来到聊天室");
            while (!mIsServiceDestoryed) {
                String str = in.readLine();
                if (str==null) {
                    break;
                }
                System.out.println("msg from client : "+str);
                int i = new Random().nextInt(mDefinedMessages.length);
                String msg = mDefinedMessages[i];
                out.println(msg);
                System.out.println("send :"+msg);

            }
            System.out.println("client quit.");
            MyUtils.close(out);
            MyUtils.close(in);
            client.close();

        }
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }
}
