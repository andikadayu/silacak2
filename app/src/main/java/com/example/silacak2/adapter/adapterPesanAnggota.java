package com.example.silacak2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.silacak2.R;
import com.example.silacak2.anggotaPesanTampil;
import com.example.silacak2.model.dataPesanModelAnggota;

import java.util.ArrayList;

public class adapterPesanAnggota extends RecyclerView.Adapter<adapterPesanAnggota.Holder> {
    private ArrayList<dataPesanModelAnggota> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public adapterPesanAnggota(Activity activity, ArrayList<dataPesanModelAnggota> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterPesanAnggota.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesan_anggota, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataPesanModelAnggota model = dataModel.get(position);
        holder.tvnama_anggotaPesan.setText(model.getMNama_anggotaPesan());
        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvnama_anggotaPesan;
        dataPesanModelAnggota model;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_anggotaPesan = v.findViewById(R.id.namaAnggotaPesanAnggota);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kirimData = new Intent(activity, anggotaPesanTampil.class);
                    kirimData.putExtra("id_user", model.getMId_user());
                    kirimData.putExtra("nama_user", model.getMNama_anggotaPesan());
                    activity.startActivity(kirimData);
                }
            });
        }
    }
}
