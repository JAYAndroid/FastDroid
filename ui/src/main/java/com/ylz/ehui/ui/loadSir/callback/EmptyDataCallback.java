package com.ylz.ehui.ui.loadSir.callback;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.utils.StringUtils;

/**
 * Author: yms
 * Time: 2018/7/20 17:00
 * Describe:  没有数据缺省页
 */
public class EmptyDataCallback extends Callback {
    //填充布局
    @Override
    protected int onCreateView() {
        return R.layout.fastdroid_common_load_call_back;
    }

    @Override
    public void onAttach(Context context, View view, String msg) {
        if(StringUtils.isEmpty(msg)){
            msg = "暂无数据";
        }

        view.<ImageView>findViewById(R.id.iv_load_bg).setBackgroundResource(R.drawable.fast_droid_load_empty_data);
        view.<TextView>findViewById(R.id.tv_load_tip).setText(msg);
    }
}

