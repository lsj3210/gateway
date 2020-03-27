package com.example.gateway.utils.user;

public class ParamsContext {
    private static ThreadLocal<UserInfo> UserThread = new ThreadLocal<UserInfo>();
    public static UserInfo getUserInfo() {
        return UserThread.get();
    }
    public static void setUserInfo(UserInfo uinfo) {
        UserThread.set(uinfo);
    }
    public static void remove() {
        UserThread.remove();
    }
}
