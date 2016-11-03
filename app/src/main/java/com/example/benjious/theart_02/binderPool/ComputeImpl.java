package com.example.benjious.theart_02.binderPool;

import android.os.RemoteException;

import com.example.benjious.theart_02.aidl.ICompute;

/**
 * Created by benjious on 2016/11/3.
 */

public class ComputeImpl  extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
