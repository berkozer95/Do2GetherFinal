package com.berkozer.do2getherfinal.Utils;

import android.util.Log;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryBuilder {
    public static final String URLHead = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final String placeId = "placeid=";
    public static final String encode = "UTF-8";
    public static final String key = "&key=";
    public static final String apiKey = "YOUR_API_KEY";


    public static final String directionsURLHead = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String origin = "origin=";
    public static final String destination = "&destination=";
    public static final String mode = "&mode=";

    public static final String PhotoURLHead = "https://maps.googleapis.com/maps/api/place/photo?";
    public static final String width = "&maxWidth=";
    public static final String photoReference = "&photoreference=";


    public static String createURL(String selectedPlaceId) {
        String urlResult;
        if ((!selectedPlaceId.equals(""))) {
            try {
                final String encodedId = URLEncoder.encode(selectedPlaceId, encode);
                urlResult = URLHead + placeId + encodedId + key + apiKey;
                Log.e("Link ---->", urlResult);
                return urlResult;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
        return null;
    }

    public static String createDirectionsURL(Double placeLat, Double placeLng, Double userLat, Double userLng, String travelMode) {
        String urlResult;
        if (placeLat != null && placeLng != null && userLat != null && userLng != null && !mode.equals("")) {
            try {
                final String encodedMode = URLEncoder.encode(travelMode, encode);
                urlResult = directionsURLHead + origin + userLat + "," + userLng + destination + placeLat + "," +
                        placeLng + mode + encodedMode + key + apiKey;
                Log.e("Link ---->", urlResult);
                return urlResult;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
        return null;
    }

    static String createPhotoURL(String placePhotoReference, String maxWidth) {
        String urlResult;
        if (!placePhotoReference.equals("")) {
            try {
                final String encodedReference = URLEncoder.encode(placePhotoReference, encode);
                urlResult = PhotoURLHead + width + maxWidth + photoReference + encodedReference + key + apiKey;
                Log.e("Link ---->", urlResult);
                return urlResult;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
        return null;
    }
}
