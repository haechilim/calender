package com.example.calender;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TableLayout;

public class CalenderView extends TableLayout {
    private float x;
    private float y;

    public CalenderView(Context context) {
        super(context);
    }

    public CalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //Log.d("wtf", event.toString());
        super.onInterceptTouchEvent(event);
        swipe(event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("wtf", event.toString());
        super.onTouchEvent(event);
        swipe(event);
        return true;
    }

    private void swipe(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            if (changeMonth(event) != 0) ((MainActivity)MainActivity.context).renderCalender(changeMonth(event), findViewById(R.id.container));
        }
    }

    private int changeMonth(MotionEvent event) {
        if(isSwipeDown(event)) return -1;
        else if(isSwipeUp(event)) return 1;
        return 0;
    }

    private boolean isSwipeDown(MotionEvent event) {
        return event.getX() <= x + 100 && event.getX() >= x - 100 && event.getY() > y + 100;
    }

    private boolean isSwipeUp(MotionEvent event) {
        return event.getX() <= x + 100 && event.getX() >= x - 100 && event.getY() < y - 100;
    }
}
