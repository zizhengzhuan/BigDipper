// IUserInfoAidlInterface.aidl
package com.enn.sop;

// Declare any non-default types here with import statements

interface IUserInfoAidlInterface {
    void setUserInfos(String userName, String userId, String userToken, String deviceId);

    int writeBackLocationAppProcessId();
}
