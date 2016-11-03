package com.example.benjious.theart_02.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.benjious.theart_02.R;

import java.util.List;

/**
 * 这里是客户端,我的主要目的就是去绑定服务,申请一个服务端的对象,并对他操作,
 * Created by benjious on 2016/10/31.
 */

public class BookManagerActivity extends AppCompatActivity {
    public static final String TAG="BookManagerActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED=1;


    private IBinder.DeathRecipient mDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "xyz  binderDied: binder died. name : "+Thread.currentThread().getName());
            if(mRemoteBookManager==null) {
                return;
            }
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager=null;
            //这里重新绑定远程Service
        }
    };

    //当有新书时,通过Binder调用的方法运行在客户端的Binder线程池中,和客户端的主线程是不同的,
    // 那么我需要一个handler对象来处理事件,(这个handler注册在主线程,那么
    //    mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,book).sendToTarget();
    // 就会在主线程执行了,具体的方法就是 handleressager()方法
    // )
    //由于表面操作的listener是运行在客服端的Binder池内(实际的就是底层的,是运行在服务端上的啦),所有我需要一个Handler对象,
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "xyz  handleMessage: receive new book :"+msg.obj);
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    };

    private IBookManager mRemoteBookManager;

    //IONewBookArrivedListener.adil文件为了可以在远端执行
    private IONewBookArrivedListener listener=new IONewBookArrivedListener.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,book).sendToTarget();

        }
    };

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager manager = IBookManager.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient,0);
                mRemoteBookManager = manager;
                List<Book> bookList=manager.getBookList();
                Log.d(TAG, "xyz  onServiceConnected: query book list: " + bookList.getClass().getCanonicalName());
                Book newBook = new Book(3, "Android开发艺术探索");
                manager.addBook(newBook);
                Log.d(TAG, "xyz  onServiceConnected: add book: "+newBook);
                List<Book> newList = manager.getBookList();
                Log.d(TAG, "xyz  onServiceConnected: query book list,book list: "+newList.toString());
                //注册监听的动作
                manager.registerListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBookManager=null;
            Log.e(TAG, "xyz  onServiceDisconnected: binder died. name: " + Thread.currentThread().getName());
        }
    };

    public void onButton1Click(View view){
        try {
            mRemoteBookManager.getBookList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_manager);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager!=null&&mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unRegisterListener(listener);
                Log.d(TAG, "xyz  onDestroy: unregist listener: "+listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
        unbindService(mConnection);
    }
}
