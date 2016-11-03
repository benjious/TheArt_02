// ISecurityCenter.aidl
package com.example.benjious.theart_02.aidl;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   String encrypt(String content);
   String decrypt(String password);

}
