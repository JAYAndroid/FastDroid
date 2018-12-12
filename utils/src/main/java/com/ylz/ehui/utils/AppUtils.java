package com.ylz.ehui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.ylz.ehui.module_utils.R;

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
        try {
            PackageManager packageManager = Utils.getApp().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    Utils.getApp().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return Utils.getApp().getResources().getString(labelRes);
        } catch (Exception e) {
            return "";
        }
    }
}
