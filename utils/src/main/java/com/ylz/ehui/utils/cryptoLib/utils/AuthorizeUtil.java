package com.ylz.ehui.utils.cryptoLib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ylz.ehui.utils.AppUtils;
import com.ylz.ehui.utils.SignUtils;
import com.ylz.ehui.utils.StringUtils;
import com.ylz.ehui.utils.ToastUtils;
import com.ylz.ehui.utils.Utils;
import com.ylz.ehui.utils.cryptoLib.sm3.SM3Utils;
import com.ylz.ehui.utils.cryptoLib.sm4.SM4Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: yms
 * Time: 2018/12/12 17:35
 * Describe:
 */
public class AuthorizeUtil {
    private static String authFileContent;

    public static boolean verifyAuth(String version) {
        if (TextUtils.isEmpty(authFileContent)) {
            ToastUtils.showHint("当前环境不安全，该功能暂不支持！");
            Log.i("AuthorizeUtil","当前环境不安全，该功能暂不支持！");
            return false;
        }

        Log.i("AuthorizeUtil","开始校验授权文件。。。");
        SM4Utils sm4 = new SM4Utils(SignUtils.APP_ID, SignUtils.IV);
        String sm4Key = StringUtils.rightPad(sm4.encryptData_CBC(SignUtils.APP_SECRET), 16, "0");
        sm4.setSecretKey(sm4Key);
        String data = sm4.decryptData_CBC(authFileContent);
        JSONObject authJSON = JSON.parseObject(data);// 授权文件内容
        String authCode = authJSON.getString("auth_code");

        StringBuilder sm3Sb = new StringBuilder();
        sm3Sb.append(SignUtils.APP_ID)
                .append(AppUtils.getAppName())
                .append(Utils.getApp().getPackageName())
                .append(AppUtils.getVersionName())
                .append(SignUtils.APP_SECRET);

        String appAuthCode = SM3Utils.encrypt(sm3Sb.toString());
        boolean result = authCode.equals(appAuthCode);
        if (!result) {
            Log.i("AuthorizeUtil","当前环境不安全，该功能暂不支持！");
            ToastUtils.showHint("当前环境不安全，该功能暂不支持！");
        }
        Log.i("AuthorizeUtil","授权文件校验成功。。。");
        return result;
    }

    public static void readAuthFile(final Context context) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                        context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null && applicationInfo.metaData != null) {
                    if (applicationInfo.metaData.containsKey("sm4_auth_file")) {
                        authFileContent = String.valueOf(applicationInfo.metaData.get("sm4_auth_file"));
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


}
