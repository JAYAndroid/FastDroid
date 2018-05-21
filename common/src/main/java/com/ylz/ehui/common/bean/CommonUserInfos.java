package com.ylz.ehui.common.bean;

import android.text.TextUtils;
import android.util.Log;

public class CommonUserInfos {
    private String sex;
    private String sessionId;
    private String name;

    private CommonUserInfos() {

    }

    public static CommonUserInfos getInstance() {
        return Singleton.instance;
    }

    private static class Singleton {
        private static CommonUserInfos instance = new CommonUserInfos();
    }

    public String getSex() {
        return checkStrNotNull(sex, "sex");
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSessionId() {
        return checkStrNotNull(sessionId, "sessionId");
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return checkStrNotNull(name, "name");
    }

    public void setName(String name) {
        this.name = name;
    }

    private String checkStrNotNull(String target, String tag) {
        if (TextUtils.isEmpty(target)) {
            Log.e(tag, "请先初始化" + tag);
            return "";
        }
        return target;
    }
}
