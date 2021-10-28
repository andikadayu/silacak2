package com.example.silacak2;

public class Constants {
    static final int LOCATION_SERVICE_ID = 175;
    static String ID_GPS = "0";
    static String NAMA_USER = "0";
    static String ID_USER = "0";
    static String ROLE_USER = "0";

    public static void setIdGps(String idGps) {
        ID_GPS = idGps;
    }

    public static void setNamaUser(String namaUser) {
        NAMA_USER = namaUser;
    }

    public static void setRoleUser(String roleUser) {
        ROLE_USER = roleUser;
    }

    public static String getIdUser() {
        return ID_USER;
    }

    public static void setIdUser(String idUser) {
        ID_USER = idUser;
    }

    public static void clearConstant() {
        ID_GPS = "0";
        NAMA_USER = "0";
        ID_USER = "0";
        ROLE_USER = "0";
    }
}
