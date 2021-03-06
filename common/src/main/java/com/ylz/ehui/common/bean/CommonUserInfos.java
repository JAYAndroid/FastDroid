package com.ylz.ehui.common.bean;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

public class CommonUserInfos implements Serializable {
    private String sex;
    private String sessionId;
    private String name;
    private String phone;
    private String userId;// // 用户id
    private String userCardLinkDTOId; //
    private String cardNo;// 病人卡号
    private String medicalCardId;//当前治疗人Id

    private String groupPrefix;// 主模块前缀名称

    private CommonUserInfos() {
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public String getUserCardLinkDTOId() {
        return userCardLinkDTOId;
    }

    public void setUserCardLinkDTOId(String userCardLinkDTOId) {
        this.userCardLinkDTOId = userCardLinkDTOId;
    }

    public String getMedicalCardId() {
        return medicalCardId;
    }

    public void setMedicalCardId(String medicalCardId) {
        this.medicalCardId = medicalCardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        if (TextUtils.isEmpty(sessionId)) {
            return "";
        }
        return sessionId;
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

    public void release() {
        sessionId = "";
        userId = "";
        name = "";
        phone = "";
        cardNo = "";
        medicalCardId = "";
        sex = "";
    }
}
