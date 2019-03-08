package com.xhc.seekbartest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 自定义游标滑动选择数值
 *
 * @author lyp
 */
@SuppressLint("AppCompatCustomView")
public class CursorView extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    public static final int POPUP_FIXED = 1;
    public static final int POPUP_FOLLOW = 0;

    private PopupWindow mPopup;
    private TextView mPopupTextView;

    private int mPopupWidth;
    private int mPopupStyle;

    private int mYLocationOffset;
    private float leftText = 0;
    private float rightText = 0;
    private float progressText = 0;
    private float step;

    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;
    private OnSeekBarProgressChangeListener mProgressChangeListener;

    /**
     * 自定义实现获取改变数值的接口
     */
    public interface OnSeekBarProgressChangeListener {
        /**
         * 获取改变数值
         *
         * @param cursorView
         * @param progress
         * @return
         */
        String onGetValueChanged(CursorView cursorView, float progress);
    }

    public CursorView(Context context) {
        super(context);
        init(context, null);
    }

    public CursorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setOnSeekBarChangeListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CursorView);

        mPopupWidth = (int) a.getDimension(R.styleable.CursorView_popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mYLocationOffset = (int) a.getDimension(R.styleable.CursorView_yOffset, 0);
        mPopupStyle = a.getInt(R.styleable.CursorView_popupStyle, POPUP_FOLLOW);

        a.recycle();
    }

    public void setPopupStyle(int style) {
        mPopupStyle = style;
    }

    public int getPopupStyle() {
        return mPopupStyle;
    }

    private void initHintPopup() {
        String popupText = null;

        if (mProgressChangeListener != null) {
            popupText = mProgressChangeListener.onGetValueChanged(this,
                    culProcess(leftText));
        }

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View undoView = inflater.inflate(R.layout.seek_bar_popup, null);
        mPopupTextView = undoView.findViewById(R.id.text);
        mPopupTextView.setText(popupText != null ? popupText
                : String.valueOf(culProcess(leftText)));

        if (mPopup == null) {
            mPopup = new PopupWindow(undoView, mPopupWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT, false);
        } else {
            mPopup.dismiss();
            mPopup = new PopupWindow(undoView, mPopupWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT, false);
        }
    }


    public void setLeftText(float str) {
        this.leftText = str;
    }

    public void setRightText(float str) {
        this.rightText = str;
    }

    public void setProgressText(float str) {
        this.progressText = str;
    }

    private void showPopup() {

        if (mPopupStyle == POPUP_FOLLOW) {
            mPopup.showAtLocation(this, Gravity.LEFT | Gravity.BOTTOM,
                    (int) (this.getX() + (int) getXPosition(this)),
                    (int) (this.getY() + mYLocationOffset + this.getHeight()));
        }
        if (mPopupStyle == POPUP_FIXED) {
            mPopup.showAtLocation(this, Gravity.CENTER | Gravity.BOTTOM, 0,
                    (int) (this.getY() + mYLocationOffset + this.getHeight()));
        }
    }

    public void initShow() {
        initHintPopup();

        this.setMax((int) (rightText - leftText) * 10);
        this.setProgress((int) ((progressText - leftText) * 10));
        mPopup.showAtLocation(this, Gravity.START | Gravity.BOTTOM,
                (int) (this.getX() + (int) getXPosition(this)),
                (int) (CursorView.this.getY() + 2 * mYLocationOffset +
                        CursorView.this.getHeight()));
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (mInternalListener == null) {
            mInternalListener = l;
            super.setOnSeekBarChangeListener(l);
        } else {
            mExternalListener = l;
        }
    }

    public void setOnProgressChangeListener(OnSeekBarProgressChangeListener l) {
        mProgressChangeListener = l;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        String popupText = null;
        if (mProgressChangeListener != null) {
            popupText = mProgressChangeListener
                    .onGetValueChanged(this, culProcess(leftText));
        }

        if (mExternalListener != null) {
            mExternalListener.onProgressChanged(seekBar, progress, b);
        }

        step = culProcess(leftText);
        mPopupTextView.setText(popupText != null ? popupText : String.valueOf(step));

        if (mPopupStyle == POPUP_FOLLOW) {
            mPopup.update((int) (this.getX() + (int) getXPosition(seekBar)),
                    (int) (this.getY() + 2 * mYLocationOffset + this.getHeight()),
                    -1, -1);
        }
    }

    public float culProcess(float left) {
        return (left * 10 + getProgress()) / 10f;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mExternalListener != null) {
            mExternalListener.onStartTrackingTouch(seekBar);
        }
        showPopup();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mExternalListener != null) {
            mExternalListener.onStopTrackingTouch(seekBar);
        }
    }

    private float getXPosition(SeekBar seekBar) {
        float val = (((float) seekBar.getProgress() * (float) (seekBar.getWidth() - 2 *
                seekBar.getThumbOffset())) / seekBar.getMax());
        float offset = seekBar.getThumbOffset() * 2;
        int textWidth = mPopupWidth;
        float textCenter = (textWidth / 2.0f);
        float newX = val + offset - textCenter;
        return newX;
    }

    private void hidePopup() {
        if (mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }
}
