package com.example.benjious.theart_02.manualbinder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.example.benjious.theart_02.aidl.Book;

import java.util.List;

/**
 * Created by benjious on 2016/10/29.
 */


public class BookManagerImpl extends Binder implements IBookManager {
    public BookManagerImpl() {
        /**
         * Convenience method for associating a specific interface with the Binder.
         * After calling, queryLocalInterface() will be implemented for you
         * to return the given owner IInterface when the corresponding
         * descriptor is requested.
         *
         * 一个方便的方法让一个特定的接口连接到Binder,但"descriptor(指的是我们的方法:getBooklist()..)"被调用时
         * queryLocalInterface()将被实现,返回他自己
         *
         */
        this.attachInterface(this, DESCRIPTOR);
    }

    //这里的形参来自asBinder()方法,IBinder对象是什么??
    //是客服端中,利用 Binder返回的所需的对象(只是他经过Binder的转化,成了一个IBinder接口)
    public static IBookManager asInterface(IBinder obj) {
        if ((obj == null)) {
            return null;
        }
        //这个对象就是 继承IBookManager接口经过Binder转换得到的对象 的实现,
        IInterface iInterface = obj.queryLocalInterface(DESCRIPTOR);
        if ((iInterface != null) && iInterface instanceof IBookManager) {
            return (IBookManager) iInterface;
        }
        return new BookManagerImpl.Proxy(obj);

    }

    //远程代理模式
    private static class Proxy implements IBookManager {
        private IBinder mRemote;

        //看到了吗?真是的调用还是上面的IBinder对象,这个只是代理
        public Proxy(IBinder mRemote) {
            this.mRemote = mRemote;
        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }


        @Override
        public List<Book> getBookList() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            List<Book> result;
            try {
                //这里就是调用了真实对象的方法
                _data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getBookList, _data, _reply, 0);
                //反序列化数据生成返回的结果
                _reply.readException();
                result = _reply.createTypedArrayList(Book.CREATOR);

            } finally {
                _reply.recycle();
                _data.recycle();
            }
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                if ((book != null)) {
                    //各种writeXXX()方法为了序列化
                    data.writeInt(1);
                    book.writeToParcel(data, 0);
                } else {
                    data.writeInt(0);
                }
                mRemote.transact(TRANSACTION_addBook, data, reply, 0);
                reply.readException();
            }finally {
                reply.recycle();
                data.recycle();
            }

        }
    }


    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_getBookList: {
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBookList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            }

            case TRANSACTION_addBook: {
                data.enforceInterface(DESCRIPTOR);
                Book arg0;
                //这个为什么有这个判断,去看Proxy的方法
                if ((0 != data.readInt())) {
                    //这个应用了Paracable接口中的方法,反序列化过程由CREATOR来完成,
                    //createFromParcel()方法是返回序列化后的原始对象
                    //也就是利用Binder我们操作到了服务端的对象
                    arg0 = Book.CREATOR.createFromParcel(data);
                } else {
                    arg0 = null;
                }
                this.addBook(arg0);
                reply.writeNoException();
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {

    }

}
