package com.example.benjious.theart_02.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *  由Binder的知识,我们知除了onCreate()方法是运行在主线程,其他四个,增删查改运行在Binder线程池中
 * Created by benjious on 2016/11/1.
 */

public class BookProvider extends ContentProvider {
    private static final String TAG="BookProvider";
    public static final String AUTHORITY ="com.example.benjious.theart_02.provider";
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

    public static final int BOOK_URI_CODE =0 ;
    public static final int USER_URI_CODE =1 ;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static final UriMatcher sUriMathcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMathcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMathcher.addURI(AUTHORITY,"user",USER_URI_CODE);

    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "xyz  onCreate: current Thread : "+Thread.currentThread().getName());
        mContext = getContext();
        //这里只是演示而已,真实项目中,不可以在初始化时执行像创建数据库的耗时操作
        initProvider();
        return true;

    }

    private void initProvider() {
        mDatabase = new DbOpenHelper(mContext).getWritableDatabase();
        mDatabase.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
        mDatabase.execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME);
        mDatabase.execSQL("insert into book values(3,'Android');");
        mDatabase.execSQL("insert into book values(4,'Ios');");
        mDatabase.execSQL("insert into book values(5,'Html5');");
        mDatabase.execSQL("insert into user values(1,'jake',1);");
        mDatabase.execSQL("insert into user values(2,'jasmine',0);");

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "xyz  query: current Thread : "+Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table==null) {
            throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        return mDatabase.query(table, projection, selection, selectionArgs, null, sortOrder, null);

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "xyz  getType: ");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "xyz  insert: ");
        String table = getTableName(uri);
        if (table==null) {
            throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        mDatabase.insert(table, null, values);
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "xyz  delete: ");
        String table = getTableName(uri);
        if (table==null) {
            throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        int count =mDatabase.delete(table, selection, selectionArgs);
        if (count>0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "xyz  update: ");
        String table = getTableName(uri);
        if (table==null) {
            throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        int row=mDatabase.update(table, values, selection, selectionArgs);
        if (row>0) {
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    private String getTableName(Uri uri) {
        String tableName=null;
        switch (sUriMathcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
