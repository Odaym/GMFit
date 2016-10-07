package com.mcsaatchi.gmfit.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class UserTest implements Parcelable{
    private String name;
    private String category;
    private String dateTaken;

    public UserTest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.category);
        dest.writeString(this.dateTaken);
    }

    protected UserTest(Parcel in) {
        this.name = in.readString();
        this.category = in.readString();
        this.dateTaken = in.readString();
    }

    public static final Creator<UserTest> CREATOR = new Creator<UserTest>() {
        @Override
        public UserTest createFromParcel(Parcel source) {
            return new UserTest(source);
        }

        @Override
        public UserTest[] newArray(int size) {
            return new UserTest[size];
        }
    };
}
