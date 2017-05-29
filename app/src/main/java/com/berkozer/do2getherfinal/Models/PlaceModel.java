package com.berkozer.do2getherfinal.Models;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlaceModel implements Serializable {
    JSONObject jsonDetails;

    public final List<String> types = new ArrayList<>();
    public final List<Review> reviews = new ArrayList<>();

    public String placeId;
    public String name;
    public String address;
    public String phoneNumber;
    public String website;
    public String iconUrl;
    public InputStream icon;
    public String vicinity;
    public String status;
    public int priceLevel;
    public double rating;
    public String photoReference;
    public int width;
    public int heigth;
    public String googleUrl;
    public String lang;
    public Double latitude;
    public Double longitude;

    public PlaceModel() {
        jsonDetails = null;
        placeId = "";
    }

    public PlaceModel(String placeId, String iconUrl, InputStream icon, String vicinity, String status, int priceLevel, int rating, String photoReference, int width, int heigth, String googleUrl, String lang, double latitude, double longitude) {
        this.placeId = placeId;
        this.iconUrl = iconUrl;
        this.icon = icon;
        this.vicinity = vicinity;
        this.status = status;
        this.priceLevel = priceLevel;
        this.rating = rating;
        this.photoReference = photoReference;
        this.width = width;
        this.heigth = heigth;
        this.googleUrl = googleUrl;
        this.lang = lang;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlaceModel addReviews(Collection<Review> reviews) {

        this.reviews.addAll(reviews);
        return this;
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public PlaceModel addTypes(Collection<String> types) {
        this.types.addAll(types);
        return this;
    }

    public List<String> getTypes() {
        return Collections.unmodifiableList(types);
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public InputStream getIcon() {
        return icon;
    }

    public void setIcon(InputStream icon) {
        this.icon = icon;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
