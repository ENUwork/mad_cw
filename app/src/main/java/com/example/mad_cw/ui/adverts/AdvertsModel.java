package com.example.mad_cw.ui.adverts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

/*
    (This) Class is a Data Model that holds Data Objects for the loaded
    AdvertsModel from the Firestore.
 */

public class AdvertsModel implements Parcelable {

    // Class (AdvertsModel) Object Instances:
    @DocumentId
    private String documentId;
    private String ad_title, ad_price;
    private List<String> images;

    // Class (AdvertsModel) Constructor(s):

    // [Empty]:
    public AdvertsModel() {
    }

    // [5 Args]:
    public AdvertsModel(String documentId, String ad_title, String image_link, String price, List<String> images) {
        this.documentId = documentId;
        this.ad_title = ad_title;
        this.ad_price = price;
        this.images = images;
    }

    // [Parcel]:
    public AdvertsModel(Parcel source) {
        this.documentId = source.readString();
        this.ad_title = source.readString();
        this.ad_price = source.readString();
        this.images = new ArrayList<>();
        source.readList(this.images, AdvertsModel.class.getClassLoader());
    }

    // ________
    // Class (AdvertsModel) Getters & Setters:

    // Getters:

    public  String getDocumentId() {
        return documentId;
    }

    public String getAd_title() {
        return ad_title;
    }

    public String getAd_price() {
        return ad_price;
    }

    public List<String> getImages() {
        return images;
    }

    // Setters:

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public void setAd_price(String price) {
        this.ad_price = price;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // ________
    // Parcelable Methods:

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
        dest.writeList(this.images);
    }

}
