package com.example.mad_cw;

public class Adverts {

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

}
