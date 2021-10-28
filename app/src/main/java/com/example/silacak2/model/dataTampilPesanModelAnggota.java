package com.example.silacak2.model;

public class dataTampilPesanModelAnggota {
    public int messageType;
    public String MId_pesan, MId_pengirim, MId_penerima, MPesan, MTanggal, MJam;

    public dataTampilPesanModelAnggota(String MId_pesan, String MId_pengirim, String MId_penerima, String MPesan, String MTanggal, String MJam, int messageType) {
        this.messageType = messageType;
        this.MId_pesan = MId_pesan;
        this.MId_pengirim = MId_pengirim;
        this.MId_penerima = MId_penerima;
        this.MPesan = MPesan;
        this.MTanggal = MTanggal;
        this.MJam = MJam;
    }

}
