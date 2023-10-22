package com.app.airmaster.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.app.airmaster.R;

public class VerticalSeekBar  extends SeekBar {

    private boolean isTopToBottom = false;//显示进度方向是否从上到下，默认false（从下到上）

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress);

        void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar);

        void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;

    }

    void onStartTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
//        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0);
//        try {
//            isTopToBottom = ta.getBoolean(R.styleable.VerticalSeekBar_isTopToBottom, false);
//        } finally {
//            ta.recycle();
//        }
        isTopToBottom = false;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        //关键更改1
        if (isTopToBottom) {
            //显示进度方向 从上到下
            c.rotate(90);
            c.translate(0, -getWidth());
        } else {
            //显示进度方向 从下到上
            c.rotate(-90);
            c.translate(-getHeight(), 0);
        }
        super.onDraw(c);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                trackTouchEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                trackTouchEvent(event);
                attemptClaimDrag();
                break;

            case MotionEvent.ACTION_UP:
                trackTouchEvent(event);
                onStopTrackingTouch();
                break;

            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }

    private void trackTouchEvent(MotionEvent event) {
        //关键更改2
        int progress = getMax() - (int) (getMax() * event.getY() / getHeight());//触摸进度方向 从下到上
        if (isTopToBottom) {
            progress = (int) (getMax() * event.getY() / getHeight());//触摸进度方向 从上到下
        }
        setProgress(progress);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, progress);
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            KeyEvent newEvent = null;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_RIGHT);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_DOWN);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_UP);
                    break;
                default:
                    newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                            event.getKeyCode());
                    break;
            }
            KeyEvent.DispatcherState dispatcherState = new KeyEvent.DispatcherState();
            dispatcherState.isTracking(event);
            return newEvent.dispatch(this, dispatcherState, event);
        }
        return false;
    }

    /**
     * 设置显示进度方向
     *
     * @param isTopToBottom true 方向从上到下
     */
    public void setTopToBottom(boolean isTopToBottom) {
        this.isTopToBottom = isTopToBottom;
    }
}
