package com.example.benjious.theart_02.binderPool;

import android.os.RemoteException;

import com.example.benjious.theart_02.aidl.ISecurityCenter;

/**
 * Created by benjious on 2016/11/3.
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {
    public static final char SECRET_CODE='^' ;
    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^=SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }


}
