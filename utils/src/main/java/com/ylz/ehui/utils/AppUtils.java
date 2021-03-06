package com.ylz.ehui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/********************
 * 作者：yms
 * 日期：2018/1/24  时间：16:08 
 * 邮箱：380413512@qq.com
 * 公司：易联众易惠科技有限公司
 * 注释：
 ********************/
public class AppUtils {
    private static int globalStatusBarColor = Color.parseColor("#196FFA");

    public static int getScreenWidth() {
        Resources resources = Utils.getApp().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        Resources resources = Utils.getApp().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getVersionCode() {
        try {
            PackageInfo pInfo = Utils.getApp().getPackageManager().getPackageInfo(
                    Utils.getApp().getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static String getVersionName() {
        try {
            PackageInfo pInfo = Utils.getApp().getPackageManager().getPackageInfo(
                    Utils.getApp().getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        if (Utils.getApp().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, Utils.getApp().getResources().getDisplayMetrics());
        } else {
            return SizeUtils.dp2px(72);
        }
    }

    /**
     * 我们约定 uuid一定是44位
     *
     * @return
     */
    public static String getUUid() {
        int limitLength = 44;
        StringBuilder uuidSb = new StringBuilder("Android-");
        String deviceUid = getDeviceUUid(Utils.getApp());

        if (deviceUid.length() > limitLength) {
            deviceUid = deviceUid.substring(0, limitLength);
        } else if (deviceUid.length() < limitLength) {
            deviceUid = deviceUid + String.format("%1$0" + (limitLength - deviceUid.length()) + "d", 0);
        }

        if (StringUtils.isEmpty(deviceUid)) {
            deviceUid = "";
        }

        return uuidSb.append(deviceUid).toString();
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceUUid(Context context) {
        String PREFS_FILE = "device_id.xml";
        String PREFS_DEVICE_ID = "device_id";

        UUID uuid;
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        final String id = prefs.getString(PREFS_DEVICE_ID, null);

        if (!StringUtils.isEmpty(id)) {
            uuid = UUID.fromString(id);
            return uuid.toString();
        }

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply();
        return uuid.toString();
    }

    /**
     * 检查手机是否安装了指定包名的app
     *
     * @param packageName 包名
     * @return
     */
    public static boolean isInstalledApp(String packageName) {
        //获取packageManager
        final PackageManager packageManager = Utils.getApp().getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<>();
        if (packageInfos != null) {
            //将包信息转换成包名，一一加入数组
            for (PackageInfo info : packageInfos) {
                packageNames.add(info.packageName);
            }
        }
        //返回是否存在该包名，如果存在，则证明安装，返回true。否则未安装，返回false。
        return packageNames.contains(packageName);
    }

    public static String getAppName() {
        PackageInfo packageInfo = null;
        try {
            PackageManager packageManager = Utils.getApp().getPackageManager();
            packageInfo = packageManager.getPackageInfo(
                    Utils.getApp().getPackageName(), 0);
            return String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager));
        } catch (Exception e) {
            if (packageInfo != null) {
                int labelRes = packageInfo.applicationInfo.labelRes;
                return Utils.getApp().getResources().getString(labelRes);
            }

            return "";
        }
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert     *file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public static String getSignMd5Str() {
        try {
            PackageInfo packageInfo = Utils.getApp().getPackageManager().getPackageInfo(
                    Utils.getApp().getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return EncryptUtils.encryptMD5ToString(sign.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getGlobalStatusBarColor() {
        return globalStatusBarColor;
    }

    public static void setGlobalStatusBarColor(int statusBarColor) {
        globalStatusBarColor = statusBarColor;
    }


}
