package com.finger.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by acer on 2015/1/21.
 */
public class HorizontalScrollViewWrapper extends HorizontalScrollView {
    private GestureDetector      mGestureDetector;
    private View.OnTouchListener mGestureListener;

    private static final String TAG = "CustomHScrollView";


    /**
     * @param context Interface to global information about an application environment.
     * @function CustomHScrollView constructor
     */
    public HorizontalScrollViewWrapper(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }


    /**
     * @param context Interface to global information about an application environment.
     * @param attrs   A collection of attributes, as found associated with a tag in an XML document.
     * @function CustomHScrollView constructor
     */
    public HorizontalScrollViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    /**
     * @param context  Interface to global information about an application environment.
     * @param attrs    A collection of attributes, as found associated with a tag in an XML document.
     * @param defStyle style of view
     * @function CustomHScrollView constructor
     */
    public HorizontalScrollViewWrapper(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    void init(Context context) {
        mGestureDetector = new GestureDetector(context, new HScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the y direction
    class HScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                return true;
            }

            return false;
        }
    }
}
