package com.moviestanzania.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Theater implements Parcelable {
    private String mName;
    private String mShowTimes;

    @Override
    public String toString() {
        return "Theater{" +
                "mName='" + mName + '\'' +
                ", mShowTimes='" + mShowTimes + '\'' +
                ", mPricing='" + mPricing + '\'' +
                ", mBooking='" + mBooking + '\'' +
                '}';
    }

    private String mPricing;
    private String mBooking;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getShowTimes() {
        return mShowTimes;
    }

    public void setShowTimes(String mShowTimes) {
        this.mShowTimes = mShowTimes;
    }

    public String getPricing() {
        return mPricing;
    }

    public void setPricing(String mPricing) {
        this.mPricing = mPricing;
    }

    public String getBooking() {
        return mBooking;
    }

    public void setBooking(String mBooking) {
        this.mBooking = mBooking;
    }

    protected Theater(Parcel in) {
        mName = in.readString();
        mShowTimes = in.readString();
        mPricing = in.readString();
        mBooking = in.readString();
    }

    public static final Creator<Theater> CREATOR = new Creator<Theater>() {
        @Override
        public Theater createFromParcel(Parcel in) {
            return new Theater(in);
        }

        @Override
        public Theater[] newArray(int size) {
            return new Theater[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mShowTimes);
        dest.writeString(this.mPricing);
        dest.writeString(this.mBooking);
    }

    public Theater() {

    }
}
