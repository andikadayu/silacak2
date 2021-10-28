package com.example.silacak2.model;

public class dataPesanModel {
    public String MId_anggotaPesan, MNama_anggotaPesan, MEmail_anggotaPesan, MId_user;

    public dataPesanModel(String MId_anggotaPesan, String MId_user, String MNama_anggotaPesan, String MEmail_anggotaPesan) {
        this.MId_anggotaPesan = MId_anggotaPesan;
        this.MId_user = MId_user;
        this.MNama_anggotaPesan = MNama_anggotaPesan;
        this.MEmail_anggotaPesan = MEmail_anggotaPesan;

    }

    public String getMId_anggotaPesan() {
        return MId_anggotaPesan;
    }

    public void setMId_anggotaPesan(String MId_anggotaPesan) {
        this.MId_anggotaPesan = MId_anggotaPesan;
    }

    public String getMId_user() {
        return MId_user;
    }

    public String getMNama_anggotaPesan() {
        return MNama_anggotaPesan;
    }

    public void setMNama_anggotaPesan(String MNama_anggotaPesan) {
        this.MNama_anggotaPesan = MNama_anggotaPesan;
    }

    public String getMEmail_anggotaPesan() {
        return MEmail_anggotaPesan;
    }

    public void setMEmail_anggotaPesan(String MEmail_anggotaPesan) {
        this.MEmail_anggotaPesan = MEmail_anggotaPesan;
    }
}
