package com.sina.weibo.sdk.demo.openapi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.weibo.sdk.demo.R;

/**
 * Created by stariy on 16-10-31.
 */
public class item extends LinearLayout {
    public item(Context context) {
        super(context);
        View.inflate(context, R.layout.item, this);
    }

    public item(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.item, this);
    }
}
