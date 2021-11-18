package com.example.silacak2.model;

public class dataPesanModelAnggota {
    public String MId_user, MId_anggotaPesan, MNama_anggotaPesan, MEmail_anggotaPesan, MFoto;

    public dataPesanModelAnggota(String MId_user, String MId_anggotaPesan, String MNama_anggotaPesan, String MEmail_anggotaPesan, String MFoto) {
        this.MId_user = MId_user;
        this.MId_anggotaPesan = MId_anggotaPesan;
        this.MNama_anggotaPesan = MNama_anggotaPesan;
        this.MEmail_anggotaPesan = MEmail_anggotaPesan;
        this.MFoto = MFoto;
    }

    public String getMId_user() {
        return MId_user;
    }

    public String getMId_anggotaPesan() {
        return MId_anggotaPesan;
    }

    public String getMNama_anggotaPesan() {
        return MNama_anggotaPesan;
    }

    public String getMFoto(){
        return MFoto;
    }
}
