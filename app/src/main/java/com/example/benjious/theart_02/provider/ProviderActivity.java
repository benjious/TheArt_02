package com.example.benjious.theart_02.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.benjious.theart_02.R;
import com.example.benjious.theart_02.aidl.Book;
import com.example.benjious.theart_02.model.User;

/**
 * Created by benjious on 2016/11/1.
 */

public class ProviderActivity extends AppCompatActivity {
    public static final String TAG="ProviderActivity";
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
//        Uri uri = Uri.parse("content://com.example.benjious.theart_02.provider");
//        getContentResolver().query(uri, null, null, null, null);
//        getContentResolver().query(uri, null, null, null, null);
//        getContentResolver().query(uri, null, null, null, null);

        Uri bookUri = Uri.parse("content://com.example.benjious.theart_02.provider/book");
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id",6);
        contentValues.put("name","程序设计的艺术");
        getContentResolver().insert(bookUri, contentValues);
        Cursor bookCursor1 = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor1.moveToNext()) {
            Book book = new Book();
            book.bookId = bookCursor1.getInt(0);
            book.bookName = bookCursor1.getString(1);
            Log.d(TAG, "xyz  onCreate: query book : "+book.toString());
        }
        bookCursor1.close();


        Uri userUri1 = Uri.parse("content://com.example.benjious.theart_02.provider/user");
        Cursor userCursor = getContentResolver().query(userUri1, new String[]{"_id", "name", "sex"}, null, null, null);
        while (userCursor.moveToNext()) {
            User user = new User();
            user.userId = userCursor.getInt(0);
            user.userName = userCursor.getString(1);
            user.isMale=userCursor.getInt(2)==1;
            Log.d(TAG, "xyz  onCreate: query user : "+user.toString());

        }
        userCursor.close();



    }
}
