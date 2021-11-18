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
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.silacak2.R;
import com.example.silacak2.anggotaPesanTampil;
import com.example.silacak2.model.dataPesanModelAnggota2;

import java.util.ArrayList;

public class adapterPesanAnggota2 extends RecyclerView.Adapter<adapterPesanAnggota2.Holder> {
    private ArrayList<dataPesanModelAnggota2> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    ImageView imgprofile;

    public adapterPesanAnggota2(Activity activity, ArrayList<dataPesanModelAnggota2> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesan_anggota2, parent, false);
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
        dataPesanModelAnggota2 model = dataModel.get(position);
        holder.tvnama_anggotaPesan.setText(model.getMNama_admin());
        setImageProfile(model.getMFoto());
        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvnama_anggotaPesan;
        dataPesanModelAnggota2 model;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_anggotaPesan = v.findViewById(R.id.namaAnggotaPesanAnggota2);
            imgprofile = v.findViewById(R.id.ppAnggota2);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kirimData = new Intent(activity, anggotaPesanTampil.class);
                    kirimData.putExtra("id_user", model.getMId_admin());
                    kirimData.putExtra("nama_user", model.getMNama_admin());
                    kirimData.putExtra("foto", model.getMFoto());
                    activity.startActivity(kirimData);
                }
            });
        }
    }
}
