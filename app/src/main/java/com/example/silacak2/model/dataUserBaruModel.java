package com.example.silacak2.model;

public class dataUserBaruModel {
    public String MId_userBaru, MNama_userBaru, MEmail_userBaru, MStatus_userBaru;

    public dataUserBaruModel(String MId_userBaru, String MNama_userBaru, String MEmail_userBaru, String MStatus_userBaru) {
        this.MId_userBaru = MId_userBaru;
        this.MNama_userBaru = MNama_userBaru;
        this.MEmail_userBaru = MEmail_userBaru;
        this.MStatus_userBaru = MStatus_userBaru;
    }

    public String getMId_userBaru() {
        return MId_userBaru;
    }

    public void setMId_userBaru(String MId_userBaru) {
        this.MId_userBaru = MId_userBaru;
    }

    public String getMNama_userBaru() {
        return MNama_userBaru;
    }

    public void setMNama_userBaru(String MNama_userBaru) {
        this.MNama_userBaru = MNama_userBaru;
    }

    public String getMEmail_userBaru() {
        return MEmail_userBaru;
    }

    public void setMEmail_userBaru(String MEmail_userBaru) {
        this.MEmail_userBaru = MEmail_userBaru;
    }

    public String getMStatus_userBaru() {
        return MStatus_userBaru;
    }
}
