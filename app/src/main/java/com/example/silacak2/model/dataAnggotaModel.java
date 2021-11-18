package com.example.silacak2.model;

public class dataAnggotaModel {
    public String MId_anggota, MId_user_anggota, MNama_anggota, MEmail_anggota, MStatus_anggota,MFoto_anggota;

    public dataAnggotaModel(String MId_anggota, String MId_user_anggota, String MNama_anggota, String MEmail_anggota, String MStatus_anggota, String foto) {
        this.MId_anggota = MId_anggota;
        this.MNama_anggota = MNama_anggota;
        this.MEmail_anggota = MEmail_anggota;
        this.MStatus_anggota = MStatus_anggota;
        this.MId_user_anggota = MId_user_anggota;
        this.MFoto_anggota = foto;
    }

    public String getMId_anggota() {
        return MId_anggota;
    }

    public void setMId_anggota(String MId_anggota) {
        this.MId_anggota = MId_anggota;
    }

    public String getMId_user_anggota() {
        return MId_user_anggota;
    }

    public void setMId_user_anggota(String MId_user_anggota) {
        this.MId_user_anggota = MId_user_anggota;
    }

    public String getMNama_anggota() {
        return MNama_anggota;
    }

    public void setMNama_anggota(String MNama_anggota) {
        this.MNama_anggota = MNama_anggota;
    }

    public String getMEmail_anggota() {
        return MEmail_anggota;
    }

    public void setMEmail_anggota(String MEmail_anggota) {
        this.MEmail_anggota = MEmail_anggota;
    }

    public String getMStatus_anggota() {
        return MStatus_anggota;
    }

    public void setMStatus_anggota(String MStatus_anggota) {
        this.MStatus_anggota = MStatus_anggota;
    }

    public String getMFoto_anggota() {
        return MFoto_anggota;
    }

    public void setMFoto_anggota(String MFoto_anggota) {
        this.MFoto_anggota = MFoto_anggota;
    }
}
