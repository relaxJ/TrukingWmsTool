package com.truking.wms.tool.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.truking.wms.tool.R;


public class Toolbar extends android.support.v7.widget.Toolbar {
    private TextView mTitleTextView;
    private Context mContext;
    private int mTitleTextColor = 0xff333333;
    private float titleSize = 0;

    public Toolbar(Context context) {
        super(context);
        init(null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mContext = getContext();
        mTitleTextView = new TextView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.CENTER;
        mTitleTextView.setLayoutParams(lp);
        mTitleTextView.setGravity(Gravity.CENTER);
        mTitleTextView.setTextSize(17);
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs,
                    R.styleable.Toolbar);
            int c = a.getColor(R.styleable.Toolbar_titleTextColor, mTitleTextColor);
            mTitleTextView.setTextColor(a.getColor(R.styleable.Toolbar_titleTextColor, mTitleTextColor));
            mTitleTextView.setText(a.getString(R.styleable.Toolbar_title));
        } else {
            mTitleTextView.setTextColor(mTitleTextColor);
        }
        if (titleSize != 0) {
            mTitleTextView.setTextSize(titleSize);
        }
        addView(mTitleTextView);

    }

    public void setBack(final Activity activity) {
        setNavigationIcon(R.drawable.back);
        setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public void setTitleSize(float size) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextSize(size);
        }
        titleSize = size;
    }

    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     */
    public void setTitleTextAppearance(Context context, @StyleRes int resId) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    /**
     * Sets the text color of the title, if present.
     *
     * @param color The new text color in 0xAARRGGBB format
     */

    public void setTitleTextColor(@ColorInt int color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }
}
