package com.example.silacak2.model;

public class dataPesanModelUser {
    public String MId_user, MNama_user, MJml;

    public dataPesanModelUser(String MId_user, String MNama_user, String MJml) {
        this.MId_user = MId_user;
        this.MNama_user = MNama_user;
        this.MJml = MJml;
    }

    public String getMId_user() {
        return MId_user;
    }

    public String getMNama_user() {
        return MNama_user;
    }

    public String getMJml() {
        return MJml;
    }
}
