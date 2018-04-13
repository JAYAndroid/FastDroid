package com.ylz.ehui.ui.dialog;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Base dialog fragment for all your dialogs, styleable and same design on Android 2.2+.
 *
 * @author David Vávra (david@inmite.eu)
 */
public abstract class BaseDialogFragment extends android.support.v4.app.DialogFragment {
    protected Context mContext;
    private Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        builder = new Builder(mContext, inflater, container);
        return build(builder).create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        doInit(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().setCancelable(builder.mCancelable);
        getDialog().setCanceledOnTouchOutside(builder.CanceledOnTouchOutside);

        window.setBackgroundDrawable(new ColorDrawable(0x00000000));

        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        int width = WindowManager.LayoutParams.MATCH_PARENT;

        if (builder.mHeightScale > 0) {
            height = (int) (builder.mHeightScale * displayMetrics.heightPixels);
        }

        if (builder.mWidthScale > 0) {
            width = (int) (builder.mWidthScale * displayMetrics.widthPixels);
        }

        if (builder.mGravity > 0) {
            window.setGravity(builder.mGravity);
        }

        window.setLayout(width, height);
    }

    protected abstract Builder build(Builder builder);

    protected abstract void doInit(View parent);

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    public void show(AppCompatActivity activity) {
        show(activity.getSupportFragmentManager(), getClass().getName());
    }

    /**
     * Custom dialog builder
     */
    protected static class Builder {
        private final Context mContext;
        private final ViewGroup mContainer;
        private View mCustomView;
        private LayoutInflater mLayoutInflater;
        private float mHeightScale;
        private float mWidthScale;
        private int mGravity;
        private boolean mCancelable;
        private boolean CanceledOnTouchOutside;


        public Builder(Context context, LayoutInflater inflater, ViewGroup container) {
            this.mContext = context;
            this.mLayoutInflater = inflater;
            this.mContainer = container;
            mCancelable = true;
            CanceledOnTouchOutside = true;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mCustomView = mLayoutInflater.inflate(layoutId, mContainer, false);
            return this;
        }

        public View create() {
            return mCustomView;
        }

        public Builder heightScale(float scale) {
            this.mHeightScale = scale;
            return this;
        }

        public Builder widthScale(float scale) {
            this.mWidthScale = scale;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelable) {
            this.CanceledOnTouchOutside = cancelable;
            return this;
        }
    }
}