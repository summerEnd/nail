package com.sp.lib.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlideLayout extends FrameLayout {

    ViewDragHelper mDragHelper;
    /**
     * 滑动的初始距离
     */
    int initialX = 0;

    /**
     * 滑动的距离
     */
    int slideOffset;

    /**
     * 滑动的View
     */
    View mSlideView;

    /**
     * 打开的最大宽度
     * @param context
     */
    int MAX_WIDTH = 600;

    /**
     * 触发打开或关闭的最小滑动距离
     * @param context
     */
    int EFFECT_WIDTH = 90;

    public SlideLayout(Context context) {
        super(context);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                boolean canSlide = (child==mSlideView);
                return canSlide;
            }

            @Override
            public void onViewDragStateChanged(int state) {
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int left;
                int top = releasedChild.getTop();

                if (slideOffset >= 0) {
                    //向右滑动
                    if (slideOffset > EFFECT_WIDTH) {
                        left = MAX_WIDTH;
                    } else {
                        left = 0;
                    }
                } else {
                    //向左滑动
                    if (Math.abs(slideOffset) > EFFECT_WIDTH) {
                        left = 0;
                    } else {
                        left = MAX_WIDTH;
                    }
                }

                mDragHelper.settleCapturedViewAt(left, top);
                invalidate();
            }

            @Override
            public int getOrderedChildIndex(int index) {
                return super.getOrderedChildIndex(index);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = MAX_WIDTH;
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return super.clampViewPositionVertical(child, top, dy);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        mSlideView=getChildAt(getChildCount()-1);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        int action = event.getAction();
        if (action ==MotionEvent.ACTION_DOWN){
            initialX=mSlideView.getLeft();
        }else {
            slideOffset= mSlideView.getLeft()-initialX;
            //Log.i("tag","slideOffset:"+slideOffset+"  initialX:"+initialX);
        }

        return true;
    }
}
