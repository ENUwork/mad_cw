package com.example.mad_cw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

// ___________________________
/* (This) Class is a Data Model that holds Data Objects for the loaded
    Adverts from the Firestore.
 */

public class Adverts implements Parcelable {

    // Class (Adverts) Object Instances:
    private String ad_title, image_link, ad_price;
    private List<String> images;

    // Class (Adverts) Constructor(s):

    // [Empty]:
    public Adverts () {
    }

    // [5 Args]:
    public Adverts (String ad_title, String image_link, String price, List<String> images) {
        this.ad_title = ad_title;
        this.image_link = image_link;
        this.ad_price = price;
        this.images = images;
    }

    // [Parcel]:
    public Adverts(Parcel source) {
        this.ad_title = source.readString();
        this.image_link = source.readString();
        this.ad_price = source.readString();
        this.images = new ArrayList<>();
        source.readList(this.images, Adverts.class.getClassLoader());
    }

    // ________
    // Class (Adverts) Getters & Setters:

    // Getters:

    public String getAd_title() {

        return ad_title;
    }

    public String getImage_link() {

        return image_link;
    }

    public String getAd_price() {

        return ad_price;
    }

    public List<String> getImages() {

        return images;
    }

    // Setters:

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public void setAd_price(String price) {

        this.ad_price = price;
    }

    public void setImages(List<String> images) {

        this.images = images;
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
        dest.writeString(this.ad_price);
        dest.writeList(this.images);
    }

}
