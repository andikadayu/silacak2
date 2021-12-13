package com.example.silacak2;

public class viewAdapterUser{
    public String nama_user, email;

    public viewAdapterUser(String nama_user, String email) {
        this.nama_user = nama_user;
        this.email = email;
    }

    public String getNama_user() {
        return nama_user;
    }

    public String getEmail() {
        return email;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
