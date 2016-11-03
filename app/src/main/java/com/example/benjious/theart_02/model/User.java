package com.example.benjious.theart_02.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.benjious.theart_02.aidl.Book;

import java.io.Serializable;

/**
 * Created by benjious on 2016/10/27.
 */

public class User implements Parcelable,Serializable {
    public int userId;
    public String userName;
    public Book book;
    public boolean isMale;

    protected User(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readByte() != 0;
        book = in.readParcelable(Thread.currentThread().getContextClassLoader());

    }
    public User(){

    }

    public User(int userId, String userName, boolean isMale) {
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeByte((byte) (isMale ? 1 : 0));
        dest.writeParcelable(book,0);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return String.format("User:{userId:%s,useName:%s,isMale:%s},with child:{%s}",
                userId, userName, isMale, book);
    }
}
