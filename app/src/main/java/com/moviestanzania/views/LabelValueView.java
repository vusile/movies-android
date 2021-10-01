package com.moviestanzania.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.moviestanzania.R;

public class LabelValueView extends LinearLayout {

    private TextView mTextLabel;
    private TextView mTextValue;

    public LabelValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setValue(String value) {
        mTextValue.setText(value);
    }

    public void setLabel(String label) {
        mTextLabel.setText(label);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.label_value_view, this);

        mTextLabel = findViewById(R.id.text_label);
        mTextValue = findViewById(R.id.text_value);

        handleAttributes(attrs);
    }

    private void handleAttributes(AttributeSet attrs) {

        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.LabelValueView,
                    0, 0);

            try {
                String mLabel = a.getString(R.styleable.LabelValueView_label);
                String mValue = a.getString(R.styleable.LabelValueView_value);

                setValue(mValue);
                setLabel(mLabel);

            } finally {
                a.recycle();
            }
        }
    }
}
