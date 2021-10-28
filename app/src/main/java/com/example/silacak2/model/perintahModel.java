package com.example.silacak2.model;

public class perintahModel {

    static double latitudePerintah;
    static double longitudePerintah;

    public static double getLatitudePerintah() {
        return latitudePerintah;
    }

    public static void setLatitudePerintah(double latitudePerintah) {
        perintahModel.latitudePerintah = latitudePerintah;
    }

    public static double getLongitudePerintah() {
        return longitudePerintah;
    }

    public static void setLongitudePerintah(double longitudePerintah) {
        perintahModel.longitudePerintah = longitudePerintah;
    }

    public static void clearPosPerintah() {
        perintahModel.latitudePerintah = 0;
        perintahModel.longitudePerintah = 0;
    }
}
