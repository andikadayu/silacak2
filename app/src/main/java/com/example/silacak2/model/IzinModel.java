package com.example.silacak2.model;

public class IzinModel {
    String id_izin,perihal,status,date_permit;

    public IzinModel(String id_izin, String perihal, String status, String date_permit) {
        this.id_izin = id_izin;
        this.perihal = perihal;
        this.status = status;
        this.date_permit = date_permit;
    }

    public String getId_izin() {
        return id_izin;
    }

    public String getPerihal() {
        return perihal;
    }

    public String getStatus() {
        return status;
    }

    public String getDate_permit() {
        return date_permit;
    }
}
