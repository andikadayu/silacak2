package com.example.silacak2.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silacak2.R;
import com.example.silacak2.model.AbsensiModel;

import java.util.ArrayList;

public class adapterAbsensi extends RecyclerView.Adapter<adapterAbsensi.Holder>{

    Activity activity;
    ArrayList<AbsensiModel> dataModel;

    public adapterAbsensi(Activity activity, ArrayList<AbsensiModel> dataModel) {
        this.activity = activity;
        this.dataModel = dataModel;
    }

    private Bitmap getPhoto(@NonNull String foto){
        byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_absensi, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        AbsensiModel dataModels = dataModel.get(position);

        Bitmap bmp = getPhoto(dataModels.getImg_absen());

        holder.imgHadir.setImageBitmap(bmp);

        holder.tvNameHadir.setText(dataModels.getNama_absen());
        holder.tvHadir.setText(dataModels.getHadir());
        holder.tvTidakHadir.setText(dataModels.getTidaks());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView imgHadir;
        TextView tvNameHadir,tvHadir,tvTidakHadir;

        public Holder(@NonNull View view) {
            super(view);

            imgHadir = view.findViewById(R.id.imgHadir);
            tvNameHadir = view.findViewById(R.id.tvNameHadir);
            tvHadir = view.findViewById(R.id.tvHadir);
            tvTidakHadir = view.findViewById(R.id.tvTidakHadir);

        }
    }
}
