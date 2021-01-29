package com.example.calender;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TableLayout;

public class CalenderView extends TableLayout {
    private float x;
    private float y;
    private boolean isActionDown = false;
    private MainActivity mainActivity;

    public CalenderView(Context context) {
        super(context);
    }

    public CalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);
        swipe(event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        swipe(event);
        return true;
    }

    private void swipe(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(isActionDown) return;
            x = event.getX();
            y = event.getY();
            isActionDown = true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            if(isSwipeDown(event)) mainActivity.prevMonth();
            else if(isSwipeUp(event)) mainActivity.nextMonth();
            isActionDown = false;
        }
    }

    private boolean isSwipeDown(MotionEvent event) {
        return event.getX() <= x + 250 && event.getX() >= x - 250 && event.getY() > y + 100;
    }

    private boolean isSwipeUp(MotionEvent event) {
        return event.getX() <= x + 250 && event.getX() >= x - 250 && event.getY() < y - 100;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
