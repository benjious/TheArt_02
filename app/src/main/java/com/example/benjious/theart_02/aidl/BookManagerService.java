package com.example.benjious.theart_02.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.Build.VERSION_CODES.N;


/**
 * 远程服务端
 * Created by benjious on 2016/10/31.
 */

public class BookManagerService extends Service {
    public static final String TAG = "BookManagerService";

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IONewBookArrivedListener> mListeners = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestory = new AtomicBoolean();



    private Binder mBinder = new com.example.benjious.theart_02.aidl.IBookManager.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IONewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            final int N = mListeners.beginBroadcast();
            mListeners.finishBroadcast();
            Log.d(TAG, "xyz  registerListener: register listener : " + N);
        }

        @Override
        public void unRegisterListener(IONewBookArrivedListener listener) throws RemoteException {
            boolean success =mListeners.unregister(listener);
            if (success) {
                final int N = mListeners.beginBroadcast();
                mListeners.finishBroadcast();
                Log.d(TAG, "xyz  unRegisterListener: unregisted succeed,目前数量: " +N);
            }else{
                Log.d(TAG, "xyz  unRegisterListener: unregisted failed,目前数量: " +N);
            }

        }


    };


    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.example.benjious.theart_02.permission.ACCESS_BOOK_SERVIEC");
        if (check== PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        new Thread(new ServiceWorker()).start();


    }

    //当有新书到时就通知注册者,同时回掉他们的onNewBookArrived()方法
    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IONewBookArrivedListener listener = mListeners.getBroadcastItem(i);
            if (listener != null) {
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListeners.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestory.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
