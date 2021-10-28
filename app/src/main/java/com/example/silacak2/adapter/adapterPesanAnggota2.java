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
import com.example.silacak2.model.dataPesanModelAnggota2;

import java.util.ArrayList;

public class adapterPesanAnggota2 extends RecyclerView.Adapter<adapterPesanAnggota2.Holder> {
    private ArrayList<dataPesanModelAnggota2> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

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

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataPesanModelAnggota2 model = dataModel.get(position);
        holder.tvnama_anggotaPesan.setText(model.getMNama_admin());
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kirimData = new Intent(activity, anggotaPesanTampil.class);
                    kirimData.putExtra("id_user", model.getMId_admin());
                    kirimData.putExtra("nama_user", model.getMNama_admin());
                    activity.startActivity(kirimData);
                }
            });
        }
    }
}
