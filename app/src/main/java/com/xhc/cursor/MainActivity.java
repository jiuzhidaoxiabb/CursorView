package com.xhc.cursor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 自定义游标滑动选择数值
 *
 * @author lyp
 */
public class MainActivity extends AppCompatActivity
        implements CursorView.OnSeekBarProgressChangeListener {

    private CursorView mCursorView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.tv_score);

        mCursorView = findViewById(R.id.seek_bar);
        mCursorView.setPopupStyle(CursorView.POPUP_FOLLOW);
        mCursorView.setLeftText(0.0f);
        mCursorView.setRightText(10.0f);

        mCursorView.setOnProgressChangeListener(this);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mCursorView.setProgressText(0);
        mCursorView.setOnProgressChangeListener(this);
        mCursorView.post(new Runnable() {
            @Override
            public void run() {
                mCursorView.initShow();
            }
        });
    }

    @Override
    public String onGetValueChanged(CursorView cursorView, float progress) {
        mTextView.setText("" + progress);
        return null;
    }
}
