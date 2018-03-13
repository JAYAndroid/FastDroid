package com.ylz.ehui.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.ylz.ehui.module_utils.R;

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

    public static int getActionBarSize(Activity activity) {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a =  activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}
