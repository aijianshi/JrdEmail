package com.tct.mail.browse;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;

public class MailWebView extends WebView {

    // NARROW_COLUMNS reflow can trigger the document to change size, so notify interested parties.
    // This is also a good trigger to know when to alter scroll position.
    public interface ContentSizeChangeListener {
        void onHeightChange(int h);
    }

    private int mCachedContentHeight;

    private ContentSizeChangeListener mSizeChangeListener;

    public MailWebView(Context c) {
        this(c, null);
    }

    public MailWebView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    public void setContentSizeChangeListener(ContentSizeChangeListener l) {
        mSizeChangeListener = l;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (mSizeChangeListener != null) {
            final int contentHeight = getContentHeight();
            if (contentHeight != mCachedContentHeight) {
                //TS: tao.gan 2016-3-7 EMAIL BUGFIX_1748620 MOD_S
                try {
                    mCachedContentHeight = contentHeight;
                    mSizeChangeListener.onHeightChange(contentHeight);
                }catch (Exception e){
                    LogUtils.e(LogTag.getLogTag(),"Exception happen during invalidate webview");
                    e.printStackTrace();
                }
                //TS: tao.gan 2016-3-7 EMAIL BUGFIX_1748620 MOD_E
            }
        }
    }

}
