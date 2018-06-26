package com.ylz.ehui.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/29
 *     desc  : 吐司相关工具类
 * </pre>
 */
public final class ToastUtils {
    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static Toast sToast;
    private static WeakReference<View> sViewWeakReference;
    private static int sLayoutId = -1;
    private static int gravity = Gravity.CENTER;
    private static int xOffset = 0;
    private static int yOffset = (int) (64 * Utils.getApp().getResources().getDisplayMetrics().density + 0.5);
    private static int bgColor = COLOR_DEFAULT;
    private static int bgResource = -1;
    private static int msgColor = COLOR_DEFAULT;
    private static TextView tvMessage;
    private static Drawable defaultBackground;

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 设置吐司位置
     *
     * @param gravity 位置
     * @param xOffset x 偏移
     * @param yOffset y 偏移
     */
    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        ToastUtils.gravity = gravity;
        ToastUtils.xOffset = xOffset;
        ToastUtils.yOffset = yOffset;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景色
     */
    public static void setBgColor(@ColorInt final int backgroundColor) {
        ToastUtils.bgColor = backgroundColor;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource 背景资源
     */
    public static void setBgResource(@DrawableRes final int bgResource) {
        ToastUtils.bgResource = bgResource;
    }

    /**
     * 设置消息颜色
     *
     * @param msgColor 颜色
     */
    public static void setMsgColor(@ColorInt final int msgColor) {
        ToastUtils.msgColor = msgColor;
    }

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(@NonNull final CharSequence text) {
        reset();
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源 Id
     */
    public static void showShort(@StringRes final int resId) {
        reset();
        show(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源 Id
     * @param args  参数
     */
    public static void showShort(@StringRes final int resId, final Object... args) {
        reset();
        show(resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 安全地显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showShort(final String format, final Object... args) {
        reset();
        show(format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    public static void showLong(@NonNull final CharSequence text) {
        reset();
        show(text, Toast.LENGTH_LONG);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     */
    public static void showLong(@StringRes final int resId) {
        reset();
        show(resId, Toast.LENGTH_LONG);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     * @param args  参数
     */
    public static void showLong(@StringRes final int resId, final Object... args) {
        reset();
        show(resId, Toast.LENGTH_LONG, args);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showLong(final String format, final Object... args) {
        reset();
        show(format, Toast.LENGTH_LONG, args);
    }

    /**
     * 安全地显示短时自定义吐司
     */
    public static View showCshustomShort(@LayoutRes final int layoutId) {
        reset();
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_SHORT);
        return view;
    }

    /**
     * 安全地显示长时自定义吐司
     */
    public static View showCustomLong(@LayoutRes final int layoutId) {
        reset();
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_LONG);
        return view;
    }

    public static void showHint(CharSequence text) {
        bgResource = com.ylz.ehui.module_utils.R.drawable.toast_bg_blue;
        msgColor = Color.parseColor("#FF196FFA");
        show(text, Toast.LENGTH_SHORT);
    }

    public static void showWarn(CharSequence text) {
        bgResource = com.ylz.ehui.module_utils.R.drawable.toast_bg_red;
        msgColor = Color.parseColor("#ef482c");
        show(text, Toast.LENGTH_SHORT);
    }


    private static void show(@StringRes final int resId, final int duration) {
        show(Utils.getApp().getResources().getText(resId).toString(), duration);
    }

    private static void show(@StringRes final int resId, final int duration, final Object... args) {
        show(String.format(Utils.getApp().getResources().getString(resId), args), duration);
    }

    private static void show(final String format, final int duration, final Object... args) {
        show(String.format(format, args), duration);
    }

    private static void show(final CharSequence text, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = Toast.makeText(Utils.getApp(), text, duration);
                defaultBackground = sToast.getView().getBackground();
                tvMessage = sToast.getView().findViewById(android.R.id.message);
                tvMessage.setGravity(gravity);
                TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance);
                tvMessage.setText(text);
                tvMessage.setTextColor(msgColor);
                sToast.setGravity(gravity, xOffset, yOffset);
                setBg();
                sToast.show();
            }
        });
    }

    private static void show(final View view, final int duration) {
//        HANDLER.post(new Runnable() {
//            @SuppressLint("ShowToast")
//            @Override
//            public void run() {
//                cancel();
//                sToast = Toast.makeText(Utils.getApp(), text, duration);
//                final TextView tvMessage = sToast.getView().findViewById(android.R.id.message);
//                if (sMsgColor != COLOR_DEFAULT) {
//                    tvMessage.setTextColor(sMsgColor);
//                }
//                if (sMsgTextSize != -1) {
//                    tvMessage.setTextSize(sMsgTextSize);
//                }
//                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
//                    sToast.setGravity(sGravity, sXOffset, sYOffset);
//                }
//                setBg(tvMessage);
//                showToast();
//            }
//        });

        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = new Toast(Utils.getApp());

                sToast.setDuration(duration);
                sToast.setView(view);
                sToast.setGravity(gravity, xOffset, yOffset);
                sToast.show();
            }
        });
    }

    private static void setBg() {
        View sToastView = sToast.getView();

        if (bgResource != -1) {
            sToastView.setBackgroundResource(bgResource);
        } else if (bgColor != COLOR_DEFAULT) {
            Drawable background = sToastView.getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN));
            } else {
                ViewCompat.setBackground(sToastView, new ColorDrawable(bgColor));
            }
        } else {
            ViewCompat.setBackground(sToastView, defaultBackground);
        }
    }

    private static View getView(@LayoutRes final int layoutId) {
        if (sLayoutId == layoutId) {
            if (sViewWeakReference != null) {
                final View toastView = sViewWeakReference.get();
                if (toastView != null) {
                    return toastView;
                }
            }
        }
        LayoutInflater inflate =
                (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflate == null) return null;
        final View toastView = inflate.inflate(layoutId, null);
        sViewWeakReference = new WeakReference<>(toastView);
        sLayoutId = layoutId;
        return toastView;
    }

    private static void reset() {
        bgResource = -1;
        msgColor = COLOR_DEFAULT;
    }

    private static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }
}
