package com.abdo_mashael.travelmantics.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserListItem implements Parcelable {
    private String id,tvTitle,tvDescription,tvPrice,tvImageUrl;

    public UserListItem() {
    }

    public UserListItem(String tvTitle, String tvDescription, String tvPrice, String tvImageUrl) {
        this.tvTitle = tvTitle;
        this.tvDescription = tvDescription;
        this.tvPrice = tvPrice;
        this.tvImageUrl = tvImageUrl;
    }

    protected UserListItem(Parcel in) {
        id = in.readString();
        tvTitle = in.readString();
        tvDescription = in.readString();
        tvPrice = in.readString();
        tvImageUrl = in.readString();
    }

    public static final Creator<UserListItem> CREATOR = new Creator<UserListItem>() {
        @Override
        public UserListItem createFromParcel(Parcel in) {
            return new UserListItem(in);
        }

        @Override
        public UserListItem[] newArray(int size) {
            return new UserListItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle = tvTitle;
    }

    public String getTvDescription() {
        return tvDescription;
    }

    public void setTvDescription(String tvDescription) {
        this.tvDescription = tvDescription;
    }

    public String getTvPrice() {
        return tvPrice;
    }

    public void setTvPrice(String tvPrice) {
        this.tvPrice = tvPrice;
    }

    public String getTvImageUrl() {
        return tvImageUrl;
    }

    public void setTvImageUrl(String tvImageUrl) {
        this.tvImageUrl = tvImageUrl;
    }


    @Override
    public String toString() {
        return "UserListItem{" +
                "id='" + id + '\'' +
                ", tvTitle='" + tvTitle + '\'' +
                ", tvDescription='" + tvDescription + '\'' +
                ", tvPrice='" + tvPrice + '\'' +
                ", tvImageUrl='" + tvImageUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(tvTitle);
        parcel.writeString(tvDescription);
        parcel.writeString(tvPrice);
        parcel.writeString(tvImageUrl);
    }
}

