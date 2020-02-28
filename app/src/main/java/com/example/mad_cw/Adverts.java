package com.example.mad_cw;

import android.os.Parcel;
import android.os.Parcelable;

public class Adverts implements Parcelable {

    /* (This) Class Holds the Data Objects for the loaded Adverts
        from the Firestore.
     */

    // ________
    // Class (Adverts) Object Instances:
    String ad_title;
    String image_link;

    // ________
    // Class (Adverts) Constructor [Empty]:
    public Adverts () {
    }

    // ________
    // Class (Adverts) Constructor [5 Args]:
    public Adverts (String ad_title, String image_link) {
        this.ad_title = ad_title;
        this.image_link = image_link;
    }

    public Adverts(Parcel source) {
        this.ad_title = source.readString();
        this.image_link = source.readString();
    }

    // ________
    // Class (Adverts) Constructor [1 Args]:



    // ________
    // Class (Adverts) Getters & Setters:

    // ________
    // Getters:

    public String getAd_title() {
        return ad_title;
    }

    public String getImage_link() {
        return image_link;
    }

    // ________
    // Setters:

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    // ________
    // Parcelable Methods:


    public static final Parcelable.Creator<Adverts> CREATOR = new Parcelable.Creator<Adverts>(){

        @Override
        public Adverts createFromParcel(Parcel source) {
            return new Adverts(source);
        }

        @Override
        public Adverts[] newArray(int size) {
            return new Adverts[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ad_title);
        dest.writeString(this.image_link);
    }

}
