package com.example.silacak2.model;

import java.util.Date;

public class dataTampilPesanModel {
    public String message;
    public int messageType;
    public Date messageTime = new Date();
    public String MId_pesan, MId_pengirim, MId_penerima, MPesan, MTanggal, MJam, MStatus;


    public dataTampilPesanModel(String MId_pesan, String MId_pengirim, String MId_penerima, String MPesan, String MTanggal, String MJam, int messageType) {
//        this.message = message;
        this.messageType = messageType;
        this.MId_pesan = MId_pesan;
        this.MId_pengirim = MId_pengirim;
        this.MId_penerima = MId_penerima;
        this.MPesan = MPesan;
        this.MTanggal = MTanggal;
        this.MJam = MJam;
        //this.MStatus = MStatus;
    }

    public String getMId_pesan() {
        return MId_pesan;
    }

    public String getMId_pengirim() {
        return MId_pengirim;
    }

    public String getMId_penerima() {
        return MId_penerima;
    }

    public String getMPesan() {
        return MPesan;
    }

    public String getMTanggal() {
        return MTanggal;
    }

    public String getMJam() {
        return MJam;
    }
}
