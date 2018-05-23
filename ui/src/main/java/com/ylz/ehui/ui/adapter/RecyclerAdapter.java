package com.ylz.ehui.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.ylz.ehui.ui.adapter.base.ItemViewDelegate;
import com.ylz.ehui.ui.adapter.base.ViewHolder;

import java.util.List;

public abstract class RecyclerAdapter<T> extends MultiItemTypeAdapter<T> {
//    protected List<T> mDatas;

    public RecyclerAdapter(final Context context, final int layoutId, List<T> datas) {
        super(context, datas);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                RecyclerAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ViewHolder holder, T t, int position);

}