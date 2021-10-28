package com.example.silacak2.model;

public class dataPesanGroubModelAdmin {
    public int messageType;
    public String MId_groub, MNama_groub, MId_send, MPesan, MTanggal, MJam;

    public dataPesanGroubModelAdmin(String MId_groub, String MNama_groub, String MId_send, String MPesan, String MTanggal, String MJam, int messageType) {
        this.messageType = messageType;
        this.MId_groub = MId_groub;
        this.MNama_groub = MNama_groub;
        this.MId_send = MId_send;
        this.MPesan = MPesan;
        this.MTanggal = MTanggal;
        this.MJam = MJam;
    }
}
