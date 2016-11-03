package com.example.benjious.theart_02.manualbinder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.example.benjious.theart_02.aidl.Book;

import java.util.List;

/**
 * Created by benjious on 2016/10/28.
 */

public interface IBookManager extends IInterface {
    //DESCRIPTOR是一个标识来的,生成子类对象(IBookManager的实现者 和 Binder的实现者)时要用到
    static final String DESCRIPTOR = "com.example.benjious.theart_02.manualbinder.IBookManager";

    static final int TRANSACTION_getBookList = (IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_addBook = (IBinder.FIRST_CALL_TRANSACTION + 1);

    public List<Book> getBookList() throws RemoteException;

    public void addBook(Book book) throws RemoteException;
}
