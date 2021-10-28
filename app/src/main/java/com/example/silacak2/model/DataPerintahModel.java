package com.example.silacak2.model;

public class DataPerintahModel {
    public String detail_perintah;
    public String id_perintah;
    public String status_perintah;

    public DataPerintahModel(String detail_perintah, String id_perintah, String status_perintah) {
        this.detail_perintah = detail_perintah;
        this.id_perintah = id_perintah;
        this.status_perintah = status_perintah;
    }

    public String getDetail_perintah() {
        return detail_perintah;
    }

    public void setDetail_perintah(String detail_perintah) {
        this.detail_perintah = detail_perintah;
    }

    public String getId_perintah() {
        return id_perintah;
    }

    public void setId_perintah(String id_perintah) {
        this.id_perintah = id_perintah;
    }

    public String getStatus_perintah() {
        return status_perintah;
    }

    public void setStatus_perintah(String status_perintah) {
        this.status_perintah = status_perintah;
    }
}
