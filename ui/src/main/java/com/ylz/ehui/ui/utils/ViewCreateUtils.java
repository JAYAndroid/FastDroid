package com.ylz.ehui.ui.utils;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylz.ehui.utils.Utils;

/**
 * Created by yons on 2018/3/20.
 */

public class ViewCreateUtils {
    public static View createTextView(int textColor) {
        return createTextView("", textColor, 0);
    }

    public static View createTextView(String content, int textColor) {
        return createTextView(content, textColor, 0);
    }

    public static View createTextView(int textResId, int textColor) {
        Resources resources = Utils.getApp().getResources();
        return createTextView(resources.getString(textResId), textColor, 0);
    }

    public static View createTextView(String content, int textColor, int rule) {
        Resources resources = Utils.getApp().getResources();
        TextView middlerView = new TextView(Utils.getApp());
        middlerView.setText(content);
        middlerView.setMaxLines(1);
        middlerView.setEllipsize(TextUtils.TruncateAt.END);
        middlerView.setTextColor(resources.getColor(textColor));
        middlerView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        if (rule > 0) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(rule);
            middlerView.setLayoutParams(lp);
        }

        return middlerView;
    }

    public static View createViewById(@LayoutRes int layoutId) {
        View customView = LayoutInflater.from(Utils.getApp()).inflate(layoutId, null, false);
        return customView;
    }

}
