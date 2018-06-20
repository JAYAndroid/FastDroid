package com.ylz.ehui.common.bean;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

public class CommonUserInfos implements Serializable {
    private String sex;
    private String sessionId;
    private String name;
    private String phone;
    private String patientId;// //病人id
    private String cardNo;// 病人卡号


    private CommonUserInfos() {
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
