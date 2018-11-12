package com.ylz.ehui.utils;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ylz.ehui.module_utils.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author : HJQ
 * github : https://github.com/getActivity/ToastUtils
 * time   : 2018/09/01
 * desc   : Toast工具类
 */
public final class ToastUtils {
    private static int TOAST_STYLE_DEFAULT = 1;
    private static int TOAST_STYLE_HINT = 2;
    private static int TOAST_STYLE_WARN = 3;

    private static int TOAST_STYLE_CURRENT = TOAST_STYLE_DEFAULT;
    private static IToastStyle sCurrentStyle;

    private static Toast sToast;

    /**
     * 初始化ToastUtils，建议在Application中初始化
     *
     * @param application 应用的上下文
     */
    public static void init(Application application) {
        // 检查默认样式是否为空，如果是就创建一个默认样式
        if (sCurrentStyle == null) {
            sCurrentStyle = new ToastBlackStyle();
        }

        // 判断有没有通知栏权限
        if (isNotificationEnabled(application)) {
            sToast = new XToast(application);
        } else {
            sToast = new SupportToast(application);
        }


        sToast.setGravity(sCurrentStyle.getGravity(), sCurrentStyle.getXOffset(), sCurrentStyle.getYOffset());
    }

    /**
     * 显示一个对象的吐司
     *
     * @param object 对象
     */
    public static void show(Object object) {
        show(object != null ? object.toString() : "null");
    }

    /**
     * 显示一个吐司
     *
     * @param id 如果传入的是正确的string id就显示对应字符串
     *           如果不是则显示一个整数的string
     */
    public static void show(int id) {

        checkToastState();

        try {
            if (TOAST_STYLE_CURRENT != TOAST_STYLE_DEFAULT) {
                sCurrentStyle = new ToastBlackStyle();
                initStyle(sCurrentStyle);
            }
            // 如果这是一个资源id
            show(sToast.getView().getContext().getResources().getText(id));
        } catch (Resources.NotFoundException ignored) {
            // 如果这是一个int类型
            show(String.valueOf(id));
        }

        TOAST_STYLE_CURRENT = TOAST_STYLE_DEFAULT;
    }

    public static void showHint(CharSequence text) {
        if (TOAST_STYLE_CURRENT != TOAST_STYLE_HINT) {
            sCurrentStyle = new ToastHintStyle();
            initStyle(sCurrentStyle);
        }
        show(text);
        TOAST_STYLE_CURRENT = TOAST_STYLE_HINT;
    }

    public static void showWarn(CharSequence text) {
        if (TOAST_STYLE_CURRENT != TOAST_STYLE_WARN) {
            sCurrentStyle = new ToastWarnStyle();
            initStyle(sCurrentStyle);
        }
        show(text);
        TOAST_STYLE_CURRENT = TOAST_STYLE_WARN;
    }


    /**
     * 显示一个吐司
     *
     * @param text 需要显示的文本
     */
    public static void show(CharSequence text) {

        checkToastState();

        if (text == null || text.equals("")) return;

        if (TOAST_STYLE_CURRENT != TOAST_STYLE_DEFAULT) {
            sCurrentStyle = new ToastBlackStyle();
            initStyle(sCurrentStyle);
        }
        // 如果显示的文字超过了10个就显示长吐司，否则显示短吐司
        if (text.length() > 20) {
            sToast.setDuration(Toast.LENGTH_LONG);
        } else {
            sToast.setDuration(Toast.LENGTH_SHORT);
        }

        sToast.setText(text);
        sToast.show();
        TOAST_STYLE_CURRENT = TOAST_STYLE_DEFAULT;
    }

    /**
     * 取消吐司的显示
     */
    public void cancel() {
        checkToastState();
        sToast.cancel();
    }

    /**
     * 获取当前Toast对象
     */
    public static Toast getToast() {
        return sToast;
    }

    /**
     * 给当前Toast设置新的布局，具体实现可看{@link XToast#setView(View)}
     */
    public static void setView(Context context, int layoutId) {
        if (context != context.getApplicationContext()) {
            context = context.getApplicationContext();
        }
        setView(View.inflate(context, layoutId, null));
    }

    public static void setView(View view) {

        checkToastState();

        if (view == null) {
            throw new IllegalArgumentException("Views cannot be empty");
        }

        // 如果吐司已经创建，就重新初始化吐司
        if (sToast != null) {
            //取消原有吐司的显示
            sToast.cancel();
            sToast.setView(view);
        }
    }

    /**
     * 统一全局的Toast样式，建议在{@link Application#onCreate()}中初始化
     *
     * @param style 样式实现类，框架已经实现三种不同的样式
     *              黑色样式：{@link ToastBlackStyle}
     */
    public static void initStyle(IToastStyle style) {
        ToastUtils.sCurrentStyle = style;
        // 如果吐司已经创建，就重新初始化吐司
        if (sToast != null) {
            //取消原有吐司的显示
            sToast.cancel();
            sToast.setView(createTextView(sToast.getView().getContext().getApplicationContext()));
        }
    }

    /**
     * 检查吐司状态，如果未初始化请先调用{@link ToastUtils#init(Application)}
     */
    private static void checkToastState() {
        //吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (sToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        }
    }

    /**
     * 生成默认的 TextView 对象
     */
    private static TextView createTextView(Context context) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(sCurrentStyle.getBackgroundColor()); // 设置背景色
        drawable.setCornerRadius(dp2px(context, sCurrentStyle.getCornerRadius())); // 设置圆角

        TextView textView = new TextView(context);
        textView.setId(R.id.toast_main_text_view_id);
        textView.setTextColor(sCurrentStyle.getTextColor());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp2px(context, sCurrentStyle.getTextSize()));
        textView.setPadding(dp2px(context, sCurrentStyle.getPaddingLeft()), dp2px(context, sCurrentStyle.getPaddingTop()),
                dp2px(context, sCurrentStyle.getPaddingRight()), dp2px(context, sCurrentStyle.getPaddingBottom()));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // setBackground API版本兼容

        if (sCurrentStyle.getBackgroundDrawable() != 0) {
            textView.setBackgroundResource(sCurrentStyle.getBackgroundDrawable());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(drawable);
        } else {
            textView.setBackgroundDrawable(drawable);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setZ(sCurrentStyle.getZ()); // 设置 Z 轴阴影
        }

        if (sCurrentStyle.getMaxLines() > 0) {
            textView.setMaxLines(sCurrentStyle.getMaxLines()); // 设置最大显示行数
        }

        return textView;
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context 上下文
     * @param spValue sp值
     * @return px值
     */
    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 检查通知栏权限有没有开启
     * 参考SupportCompat包中的： NotificationManagerCompat.from(context).areNotificationsEnabled();
     */
    private static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return (Integer) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == 0;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException | ClassNotFoundException ignored) {
                return true;
            }
        } else {
            return true;
        }
    }
}