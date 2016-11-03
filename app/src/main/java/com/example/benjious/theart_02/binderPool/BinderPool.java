package com.example.benjious.theart_02.binderPool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.benjious.theart_02.aidl.IBinderPool;

import java.util.concurrent.CountDownLatch;

import static android.R.attr.start;

/**
 * 这是BinderPool的实现,1.查找合适的Binder 2.连接Service,
 *
 * Created by benjious on 2016/11/3.
 */

public class BinderPool {
    public static final String TAG="BinderPool";
    public static final int BINDER_NONE =-1 ;
    public static final int BINDER_SECURITY_CENTER =1 ;
    public static final int BINDER_COMPUTE =0 ;

    private Context mContext;
    //IBinderPool是接口,那么这个对象肯定是这个接口的实现者,代表从远端的实现者
    private IBinderPool mIBinderPool;
    private static volatile BinderPool sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;



    public BinderPool(Context context) {
        mContext = context;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance==null) {
            synchronized (BinderPool.class) {
                if (sInstance==null) {
                    sInstance = new BinderPool(context);
                }

            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent serviceIntent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(serviceIntent,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private ServiceConnection mBinderPoolConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mIBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w(TAG, "xyz binderDied: binder died.");
            mIBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mIBinderPool=null;
            connectBinderPoolService();
        }
    };

    public IBinder queryBinder(int binderCode) {
        IBinder iBinder = null;
        if (mIBinderPool!=null) {
            try {
                iBinder=mIBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return iBinder;
    }




    private static class BinderPoolImpl extends IBinderPool.Stub{
        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder iBinder = null;
            switch (binderCode) {
                case BINDER_COMPUTE:
                    iBinder=new ComputeImpl();
                    break;
                case BINDER_SECURITY_CENTER :
                    iBinder=new SecurityCenterImpl();
                    break;
                default:
                    break;
            }
            return iBinder;

        }
    }

}
