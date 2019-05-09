package com.ylz.ehui.ui.safeWebview;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebView;


/**
 * Author: yms
 * Date: 2017/9/11 11:01
 * Desc:
 */
public class CustomChromeClient extends InjectedChromeClient {
    private OnWebChromeListener mOnProgressChangedListener;
    private OnReceivedTitleListener mOnReceivedTitleListener;

    public CustomChromeClient() {
    }

    public CustomChromeClient(String injectedName, Class injectedCls) {
        super(injectedName, injectedCls);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        try {
            super.onProgressChanged(view, newProgress);
            if (mOnProgressChangedListener != null) {
                mOnProgressChangedListener.onProgressChanged(view, newProgress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onReceivedTitle(WebView webView, String title) {
        super.onReceivedTitle(webView, title);
        if (mOnReceivedTitleListener != null) {
            mOnReceivedTitleListener.onReceivedTitle(title);
        }
    }

    public void setOnProgressChangedListener(OnWebChromeListener listener) {
        mOnProgressChangedListener = listener;
    }

    public void setOnReceivedTitleListener(OnReceivedTitleListener listener) {
        mOnReceivedTitleListener = listener;
    }

    public interface OnWebChromeListener {
        void onProgressChanged(WebView view, int newProgress);
    }

    public interface OnReceivedTitleListener {
        void onReceivedTitle(String title);
    }
}
