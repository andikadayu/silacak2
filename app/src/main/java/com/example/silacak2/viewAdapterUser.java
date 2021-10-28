package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
