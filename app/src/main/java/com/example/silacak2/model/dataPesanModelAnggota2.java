package com.example.silacak2.model;

public class dataPesanModelAnggota2 {
    public String MId_admin, MNama_admin, MFoto;

    public dataPesanModelAnggota2(String MId_admin, String MNama_admin, String MFoto) {
        this.MId_admin = MId_admin;
        this.MNama_admin = MNama_admin;
        this.MFoto = MFoto;
    }

    public String getMId_admin() {
        return MId_admin;
    }

    public String getMNama_admin() {
        return MNama_admin;
    }

    public String getMFoto(){
        return MFoto;
    }
}
