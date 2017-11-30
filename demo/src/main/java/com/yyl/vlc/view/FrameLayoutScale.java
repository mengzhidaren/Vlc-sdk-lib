package com.yyl.vlc.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * Created by yyl on 2015/11/22/022.
 */

public class FrameLayoutScale extends ViewGroup {

    private float videoScale = 9f / 16f;
    private boolean isFullVideoState;

    public FrameLayoutScale(Context context) {
        super(context);
    }

    public FrameLayoutScale(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutScale(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isFullVideoState = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        onMeasureVideo(widthMeasureSpec, heightMeasureSpec, getLayoutParams().height);
    }

    private void onMeasureVideo(int widthMeasureSpec, int heightMeasureSpec, int heightParams) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureHeight = isFullVideoState ? measureHeight : (int) (measureWidth * videoScale);
        setMeasuredDimension(measureWidth, measureHeight);
        if (isFullVideoState && heightParams != FrameLayout.LayoutParams.MATCH_PARENT) {
            getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
            if (isInEditMode()) {
                setMeasuredDimension(measureWidth, measureHeight);
            }
            return;
        } else if (!isFullVideoState && heightParams == FrameLayout.LayoutParams.MATCH_PARENT) {
            getLayoutParams().height =measureHeight;
            if (isInEditMode()) {
                setMeasuredDimension(measureWidth, measureHeight);
            }
            return;
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
                final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    public void setScaleVideo(float videoScale) {
        this.videoScale = videoScale;
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FrameLayout.LayoutParams;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // final int width = child.getMeasuredWidth();
                //   final int height = child.getMeasuredHeight();
                child.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }
}