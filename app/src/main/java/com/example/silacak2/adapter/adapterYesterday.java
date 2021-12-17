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
import com.example.silacak2.model.YesterdayModel;

import java.util.ArrayList;

public class adapterYesterday extends RecyclerView.Adapter<adapterYesterday.Holder>{

    Activity activity;
    ArrayList<YesterdayModel> dataModel;


    public adapterYesterday(Activity activity, ArrayList<YesterdayModel> dataModel) {
        this.activity = activity;
        this.dataModel = dataModel;
    }

    private Bitmap getImagePhoto(@NonNull String foto){
        byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_yesterday, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        YesterdayModel dataModels = dataModel.get(position);

        Bitmap bmp = getImagePhoto(dataModels.getImageYes());

        holder.imgHadir.setImageBitmap(bmp);
        holder.tvNameHadir.setText(dataModels.getNameYes());
        holder.tvNRPHadir.setText(dataModels.getNrpYes());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }


    public class Holder extends RecyclerView.ViewHolder{

        ImageView imgHadir;
        TextView tvNameHadir,tvNRPHadir;

        public Holder(@NonNull View view) {
            super(view);

            imgHadir = view.findViewById(R.id.imgHadir);
            tvNameHadir = view.findViewById(R.id.tvNameHadir);
            tvNRPHadir = view.findViewById(R.id.tvNRPHadir);

        }
    }
}
