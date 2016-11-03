// IONewBookArrivedListener.aidl
package com.example.benjious.theart_02.aidl;
import com.example.benjious.theart_02.aidl.Book;
// Declare any non-default types here with import statements

interface IONewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void onNewBookArrived(in Book book);
}
