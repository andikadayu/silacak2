package com.example.silacak2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.silacak2.R;
import com.example.silacak2.adminPesanTampil;
import com.example.silacak2.model.dataPesanModel;

import java.util.ArrayList;

public class adapterPesanAdmin extends RecyclerView.Adapter<adapterPesanAdmin.Holder> {
    private ArrayList<dataPesanModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    ImageView imgprofile;

    public adapterPesanAdmin(Activity activity, ArrayList<dataPesanModel> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterPesanAdmin.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesan_admin, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    private void setImageProfile(String foto){
        if (!foto.equalsIgnoreCase("null")){
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            imgprofile.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataPesanModel model = dataModel.get(position);
        holder.tvnama_anggotaPesan.setText(model.getMNama_anggotaPesan());
        setImageProfile(model.getMFoto());

        //viewBinderHelper.bind(holder.cardView, model.MId_anggotaPesan);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent kirimData = new Intent(activity, adminPesanTampil.class);
//                activity.startActivity(kirimData);
//            }
//        });

        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvnama_anggotaPesan;

        dataPesanModel model;
        private CardView cardView;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_anggotaPesan = v.findViewById(R.id.namaAnggotaPesan);
            imgprofile = v.findViewById(R.id.pp);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kirimData = new Intent(activity, adminPesanTampil.class);
                    kirimData.putExtra("id_user", model.getMId_user());
                    kirimData.putExtra("nama_user", model.getMNama_anggotaPesan());
                    kirimData.putExtra("foto", model.getMFoto());
                    activity.startActivity(kirimData);
                }
            });
        }
    }
}
