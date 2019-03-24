package com.structit.snake.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SnakeOnTouchListener implements View.OnTouchListener {
    private final String LOG_TAG = SnakeOnTouchListener.class.getName();

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(LOG_TAG, "X: " + motionEvent.getX());
        Log.d(LOG_TAG, "Y: " + motionEvent.getY());

        return false;
    }
}
