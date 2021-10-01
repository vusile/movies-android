package com.moviestanzania.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Movie implements Parcelable {
    private int mId;
    private String mName;
    private String mStarring;
    private String mDuration;
    private String mDirector;
    private boolean mFeatured;
    private String mTrailer;
    private String mSynopsis;
    private String mPoster;
    private String mStatus;
    private ArrayList<Theater> mTheaters;
    private String mGenres;
    private boolean mNowShowing;

    public static ArrayList<Movie> getMovies(JsonArray moviesJsonArray) {
        ArrayList<Movie> movies = new ArrayList<>();
        for(int i =0; i < moviesJsonArray.size(); i++) {
            JsonObject movieJsonObject = moviesJsonArray.get(i).getAsJsonObject();
            movies.add(getMovie(movieJsonObject));
        }
        return movies;
    }

    public static Movie getMovie(JsonObject movieJsonObject) {
        Movie movie = new Movie();
        movie.setId(movieJsonObject.get("id").getAsInt());
        movie.setName(movieJsonObject.get("name").getAsString());
        movie.setStarring(movieJsonObject.get("starring").getAsString());
        movie.setDuration(movieJsonObject.get("duration").getAsString());
        movie.setDirector(movieJsonObject.get("director").getAsString());
        movie.setFeatured(movieJsonObject.get("featured").getAsBoolean());
        movie.setTrailer(movieJsonObject.get("trailer").getAsString());
        movie.setSynopsis(movieJsonObject.get("synopsis").getAsString());
        movie.setPoster(movieJsonObject.get("poster").getAsString());
        movie.setStatus(movieJsonObject.get("status").getAsString());
        movie.setNowShowing(movieJsonObject.get("now_showing").getAsBoolean());
        movie.setGenres(movieJsonObject.get("genres").getAsString());

        if(movie.isNowShowing()) {
            JsonArray theatersJsonArray = movieJsonObject.get("theaters").getAsJsonArray();
            ArrayList<Theater> theaters = new ArrayList<>();
            for(int j = 0; j <theatersJsonArray.size(); j++) {
                JsonObject theaterJsonObject = theatersJsonArray.get(j).getAsJsonObject();
                Theater theater = new Theater();

                if(!theaterJsonObject.get("name").isJsonNull()) {
                    theater.setName(theaterJsonObject.get("name").getAsString());
                }

                if(!theaterJsonObject.get("show_times").isJsonNull()) {
                    theater.setShowTimes(theaterJsonObject.get("show_times").getAsString());
                }

                if(!theaterJsonObject.get("pricing").isJsonNull()) {
                    theater.setPricing(theaterJsonObject.get("pricing").getAsString());
                }

                if(!theaterJsonObject.get("booking_details").isJsonNull()) {
                    theater.setBooking(theaterJsonObject.get("booking_details").getAsString());
                }

                theaters.add(theater);
            }

            movie.setTheaters(theaters);
        }

        return movie;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getStarring() {
        return mStarring;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getDirector() {
        return mDirector;
    }

    public boolean ismFeatured() {
        return mFeatured;
    }

    public String getTrailer() {
        return mTrailer;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getPoster() {
        return mPoster.replace("127.0.0.1", "10.0.2.2");
    }

    public String getStatus() {
        return mStatus;
    }

    public ArrayList<Theater> getTheaters() {
        return mTheaters;
    }

    public String getGenres() {
        return mGenres;
    }

    public boolean isNowShowing() {
        return mNowShowing;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mStarring='" + mStarring + '\'' +
                ", mDuration='" + mDuration + '\'' +
                ", mDirector='" + mDirector + '\'' +
                ", mFeatured=" + mFeatured +
                ", mTrailer='" + mTrailer + '\'' +
                ", mSynopsis='" + mSynopsis + '\'' +
                ", mPoster='" + mPoster + '\'' +
                ", mStatus='" + mStatus + '\'' +
                ", mTheaters=" + mTheaters +
                ", mGenres='" + mGenres + '\'' +
                ", mNowShowing=" + mNowShowing +
                '}';
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setStarring(String mStarring) {
        this.mStarring = mStarring;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public void setDirector(String mDirector) {
        this.mDirector = mDirector;
    }

    public void setFeatured(boolean mFeatured) {
        this.mFeatured = mFeatured;
    }

    public void setTrailer(String mTrailer) {
        this.mTrailer = mTrailer;
    }

    public void setSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public void setTheaters(ArrayList<Theater> mTheaters) {
        this.mTheaters = mTheaters;
    }

    public void setGenres(String mGenres) {
        this.mGenres = mGenres;
    }

    public void setNowShowing(boolean mNowShowing) {
        this.mNowShowing = mNowShowing;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mStarring = in.readString();
        mDuration = in.readString();
        mDirector = in.readString();
        mFeatured = in.readByte() != 0;
        mTrailer = in.readString();
        mSynopsis = in.readString();
        mPoster = in.readString();
        mStatus = in.readString();
        mTheaters = in.createTypedArrayList(Theater.CREATOR);
        mGenres = in.readString();
        mNowShowing = in.readByte() != 0;
    }

    public Movie() {

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mStarring);
        dest.writeString(this.mDuration);
        dest.writeString(this.mDirector);
        dest.writeByte(this.mFeatured ? (byte) 1 : (byte) 0);
        dest.writeString(this.mTrailer);
        dest.writeString(this.mSynopsis);
        dest.writeString(this.mPoster);
        dest.writeString(this.mStatus);
        dest.writeTypedList(this.mTheaters);
        dest.writeString(this.mGenres);
        dest.writeByte(this.mNowShowing ? (byte) 1 : (byte) 0);

    }
}
