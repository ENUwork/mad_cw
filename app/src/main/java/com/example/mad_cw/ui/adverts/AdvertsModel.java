package com.example.mad_cw.ui.adverts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
    (This) Class is a Data Model that holds Data Objects for the loaded
    AdvertsModel from the Firestore.
 */

public class AdvertsModel implements Parcelable {

    // Class (AdvertsModel) Object Instances:
    @DocumentId
    private String documentId;
    private String ad_title, ad_price, ad_desc, ad_loc, ad_age, ad_owner;
    private Date post_time;
    private List<String> ad_other, images;

    // Class (AdvertsModel) Constructor(s):

    // [Empty]:
    public AdvertsModel() {
    }

    // [5 Args]:
    public AdvertsModel(String documentId, String ad_title, String ad_price, String ad_desc,
                        String ad_loc, String ad_age, String ad_owner, Timestamp post_time,
                        List<String> ad_other, List<String> images) {
        this.documentId = documentId;
        this.ad_title = ad_title;
        this.ad_price = ad_price;
        this.ad_desc = ad_desc;
        this.ad_loc = ad_loc;
        this.ad_age = ad_age;
        this.ad_owner = ad_owner;
        this.post_time = post_time.toDate();
        this.ad_other = ad_other;
        this.images = images;
    }

    // [Parcel]:
    public AdvertsModel(Parcel source) {
        this.documentId = source.readString();
        this.ad_title = source.readString();
        this.ad_price = source.readString();
        this.ad_desc = source.readString();
        this.ad_loc = source.readString();
        this.ad_age = source.readString();
        this.ad_owner = source.readString();
        this.post_time = new Date(source.readLong());
        this.ad_other = new ArrayList<>();
        source.readList(this.ad_other, AdvertsModel.class.getClassLoader());
        this.images = new ArrayList<>();
        source.readList(this.images, AdvertsModel.class.getClassLoader());
    }

    // ________
    // Class (AdvertsModel) Getters & Setters:

    // getters:
    public String getDocumentId() { return documentId; }
    public String getAd_title() { return ad_title; }
    public String getAd_price() { return ad_price; }
    public String getAd_desc() { return ad_desc; }
    public String getAd_loc() { return ad_loc; }
    public String getAd_age() { return  ad_age; }
    public String getAd_owner() { return ad_owner; }
    public Date getPost_time() { return post_time; }
    public List<String> getImages() { return images; }
    public List<String> getAd_other() { return ad_other; }

    // setters:
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setAd_title(String ad_title) { this.ad_title = ad_title; }
    public void setAd_price(String price) { this.ad_price = price; }
    public void setImages(List<String> images) { this.images = images; }
    public void setAd_desc(String ad_desc) { this.ad_desc = ad_desc; }
    public void setAd_loc(String ad_loc) { this.ad_loc = ad_loc; }
    public void setAd_age(String ad_age) { this.ad_age = ad_age; }
    public void setAd_owner(String ad_owner) { this.ad_owner = ad_owner; }
    public void setPost_time(Date post_time) { this.post_time = post_time; }
    public void setAd_other(List<String> other_info) { this.ad_other = other_info; }

    // ________________
    // parcelable methods:

    public static final Parcelable.Creator<AdvertsModel> CREATOR = new Parcelable.Creator<AdvertsModel>(){

        @Override
        public AdvertsModel createFromParcel(Parcel source) {
            return new AdvertsModel(source);
        }

        @Override
        public AdvertsModel[] newArray(int size) {
            return new AdvertsModel[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.documentId);
        dest.writeString(this.ad_title);
        dest.writeString(this.ad_price);
        dest.writeString(this.ad_desc);
        dest.writeString(this.ad_loc);
        dest.writeString(this.ad_age);
        dest.writeString(this.ad_owner);
        dest.writeLong(post_time.getTime());
        dest.writeList(this.ad_other);
        dest.writeList(this.images);
    }

}
