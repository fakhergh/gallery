package com.benboubaker.gallery.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.RelativeLayout;

/**
 * Created by fekhe on 06/11/2017.
 */

public class SwipeableLayout extends RelativeLayout{

    private boolean isLocked;

    private OnLayoutSwipeListener swipeListener;
    private OnLayoutCloseListener closeListener;

    private int previousPositionRawX;
    private int previousPositionRawY;

    private int baseLayoutPositionX;
    private int baseLayoutPositionY;

    private Direction direction = Direction.NONE;
    private Direction swipeDirection = Direction.NONE;

    private ObjectAnimator animator;

    private VelocityTracker velocityTracker;
    private float lastVelocityX;
    private float lastVelocityY;

    public boolean isLocked() {
        return isLocked;
    }

    public enum Direction{
        NONE, UP_DOWN, LEFT_RIGHT
    }

    public interface OnLayoutCloseListener{
        void onLayoutClosed();
    }

    public interface OnLayoutSwipeListener{
        void onSwiping(float x, float y);
    }

    public SwipeableLayout(Context context) {
        super(context);
    }

    public SwipeableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isLocked){return false;}

        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN){
            previousPositionRawX = rawX;
            previousPositionRawY = rawY;
        }else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE){
            int diffX  = rawX - previousPositionRawX;
            int diffY  = rawY - previousPositionRawY;

            if (direction == Direction.UP_DOWN){
                if (Math.abs(diffX) + 25 < Math.abs(diffY)){
                    return true;
                }
            }else if (direction == Direction.LEFT_RIGHT){
                if (Math.abs(diffY) + 25 < Math.abs(diffX)){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isLocked){return false;}

        if (velocityTracker == null) {velocityTracker = VelocityTracker.obtain();}

        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN){
            velocityTracker.clear();
            velocityTracker.addMovement(ev);

            lastVelocityX = 0;
            lastVelocityY = 0;

            previousPositionRawX = rawX;
            previousPositionRawY = rawY;

            baseLayoutPositionX = (int) getX();
            baseLayoutPositionY = (int) getY();
            return true;
        }else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE){
            velocityTracker.addMovement(ev);
            velocityTracker.computeCurrentVelocity(1000);

            lastVelocityX = velocityTracker.getXVelocity();
            lastVelocityY  = velocityTracker.getYVelocity();

            int diffX  = rawX - previousPositionRawX;
            int diffY  = rawY - previousPositionRawY;

            if (swipeDirection == Direction.NONE){
                if (Math.abs(diffX) + 25 < Math.abs(diffY)){
                    swipeDirection  = Direction.UP_DOWN;
                } else if (Math.abs(diffY) + 25 < Math.abs(diffX)){
                    swipeDirection  = Direction.LEFT_RIGHT;
                }else {
                    swipeDirection = Direction.NONE;
                }
            }

            if (direction  == Direction.UP_DOWN && swipeDirection == Direction.UP_DOWN){
                setY(baseLayoutPositionY + diffY);
                requestLayout();
                if (swipeListener != null){
                    swipeListener.onSwiping(getX(), getY());
                }
                return true;
            }else if (direction == Direction.LEFT_RIGHT && swipeDirection == Direction.LEFT_RIGHT){
                setX(baseLayoutPositionX + diffX);
                requestLayout();
                if (swipeListener != null){
                    swipeListener.onSwiping(getX(), getY());
                }
                return true;
            }

        }else if (ev.getActionMasked() == MotionEvent.ACTION_UP){
            String property = null;
            int origin = 0;
            if (direction == Direction.UP_DOWN){
                property = "y";
                origin = (int) getY();
                if (Math.abs(getY()) > getHeight() / 2){
                    if (closeListener != null){
                        closeListener.onLayoutClosed();
                        return true;
                    }
                }else {
                    if (Math.abs(lastVelocityY) > 500){
                        if (closeListener != null){
                            closeListener.onLayoutClosed();
                            return true;
                        }
                    }
                }
                resetView(property, origin,0);
            }else if (direction == Direction.LEFT_RIGHT){
                property = "x";
                origin = (int) getX();
                if (Math.abs(getX()) > getWidth() / 2){
                    if (closeListener != null){
                        closeListener.onLayoutClosed();
                        return true;
                    }
                }else {
                    if (Math.abs(lastVelocityX) > 500){
                        if (closeListener != null){
                            closeListener.onLayoutClosed();
                            return true;
                        }
                    }
                }
                resetView(property, origin,0);
            }
            swipeDirection = Direction.NONE;
            return true;
        }
        return false;
    }

    private void resetView(String property, int origin, int destination) {
        if (animator != null && animator.isRunning()){animator.cancel();}
        animator = ObjectAnimator.ofFloat(this,property,origin,destination);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                requestLayout();
                if (swipeListener != null){
                    swipeListener.onSwiping(getX(), getY());
                }
            }
        });
        animator.start();
    }

    public void setSwipeDirection(Direction direction){
        this.direction = direction;
    }

    public void lock(){
        this.isLocked = true;
    }

    public void unLock(){
        this.isLocked = false;
    }

    public void setOnLayoutCloseListener(OnLayoutCloseListener closeListener){
        this.closeListener = closeListener;
    }

    public void setOnLayoutSwipeListener(OnLayoutSwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }
}