//package com.ylz.ehui.ui.widget;
//
//import android.content.Context;
//import android.support.v4.widget.NestedScrollView;
//import android.util.AttributeSet;
//
//import FrameLayout;
//import com.module.autolayout.utils.AutoLayoutHelper;
//
///**
// * Created by yons on 2018/4/25.
// */
//
//public class MyNestedScrollView extends NestedScrollView {
//    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);
//
//
//    public MyNestedScrollView(Context context) {
//        super(context);
//    }
//
//    public MyNestedScrollView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
//    {
//        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        if (!isInEditMode())
//        {
//            mHelper.adjustChildren();
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//}
