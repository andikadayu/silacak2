package com.example.silacak2.model;

public class AbsensiModel {
    String id_anggota,nama_absen,img_absen,hadir,tidaks;

    public AbsensiModel(String id_anggota, String nama_absen, String img_absen, String hadir, String tidaks) {
        this.id_anggota = id_anggota;
        this.nama_absen = nama_absen;
        this.img_absen = img_absen;
        this.hadir = hadir;
        this.tidaks = tidaks;
    }

    public String getId_anggota() {
        return id_anggota;
    }

    public String getNama_absen() {
        return nama_absen;
    }

    public String getImg_absen() {
        return img_absen;
    }

    public String getHadir() {
        return hadir;
    }

    public String getTidaks() {
        return tidaks;
    }
}
