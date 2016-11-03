// IBookManager.aidl
package com.example.benjious.theart_02.aidl;
import com.example.benjious.theart_02.aidl.Book;
import com.example.benjious.theart_02.aidl.IONewBookArrivedListener;
// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IONewBookArrivedListener listener);
    void unRegisterListener(IONewBookArrivedListener listener);

}
