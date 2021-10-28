package com.example.silacak2.model;

public class dataUserModel {
    public String MNama_user;
    public String MEmail_user;
    public String MId_user;
    public String MStatus_user;

    public dataUserModel(String MNama_user, String MEmail_user, String MId_user, String MStatus_user) {
        this.MNama_user = MNama_user;
        this.MEmail_user = MEmail_user;
        this.MId_user = MId_user;
        this.MStatus_user = MStatus_user;
    }

    public String getMNama_user() {
        return MNama_user;
    }

    public void setMNama_user(String MNama_user) {
        this.MNama_user = MNama_user;
    }

    public String getMEmail_user() {
        return MEmail_user;
    }

    public void setMEmail_user(String MEmail_user) {
        this.MEmail_user = MEmail_user;
    }

    public String getMId_user() {
        return MId_user;
    }

    public void setMId_user(String MId_user) {
        this.MId_user = MId_user;
    }

    public String getMStatus_user() {
        return MStatus_user;
    }


}
