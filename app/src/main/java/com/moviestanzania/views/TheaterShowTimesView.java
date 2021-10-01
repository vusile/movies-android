package com.moviestanzania.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.moviestanzania.R;

public class TheaterShowTimesView extends LinearLayout {
    private TextView mTextTheaterName;
    private TextView mTextTheaterShowTimes;
    private TextView mTextTheaterPricing;
    private MaterialButton mButtonBook;
    
    public TheaterShowTimesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TheaterShowTimesView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.theater_show_times_view, this);

        mTextTheaterName = findViewById(R.id.text_theater_name);
        mTextTheaterShowTimes = findViewById(R.id.text_theater_show_times);
        mTextTheaterPricing = findViewById(R.id.text_theater_pricing);
        mButtonBook = findViewById(R.id.button_book_movie);

        handleAttributes(attrs);
    }

    private void handleAttributes(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TheaterShowTimesView,
                    0, 0);

            try {
                String theaterName = a.getString(R.styleable.TheaterShowTimesView_theater_name);
                String showTimes = a.getString(R.styleable.TheaterShowTimesView_show_times);
                String pricing = a.getString(R.styleable.TheaterShowTimesView_pricing);

                setTheaterName(theaterName);
                setShowTimes(showTimes);
                setPricing(pricing);

            } finally {
                a.recycle();
            }
        }
    }

    public void setTheaterName(String theaterName) {
        mTextTheaterName.setText(theaterName);
    }
    
    public void setPricing(String pricing) {
        mTextTheaterPricing.setText(pricing);
    }
    
    public void setShowTimes(String showTimes) {
        mTextTheaterShowTimes.setText(showTimes);
    }

    public MaterialButton getButtonBook() {
        return mButtonBook;
    }
}
