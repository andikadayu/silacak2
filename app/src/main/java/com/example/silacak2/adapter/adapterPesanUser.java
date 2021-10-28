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
import com.example.silacak2.model.dataPesanModelUser;
import com.example.silacak2.userPesanTampil;

import java.util.ArrayList;

public class adapterPesanUser extends RecyclerView.Adapter<adapterPesanUser.Holder> {
    private ArrayList<dataPesanModelUser> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public adapterPesanUser(Activity activity, ArrayList<dataPesanModelUser> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesan_user, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataPesanModelUser model = dataModel.get(position);
        holder.tvnama_userPesan.setText(model.getMNama_user());
        holder.tvjml.setText(model.getMJml());
        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvnama_userPesan, tvjml;
        dataPesanModelUser model;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_userPesan = v.findViewById(R.id.namaUserPesan);
            tvjml = v.findViewById(R.id.jml);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kirimData = new Intent(activity, userPesanTampil.class);
                    kirimData.putExtra("id_user", model.getMId_user());
                    kirimData.putExtra("nama_user", model.getMNama_user());
                    activity.startActivity(kirimData);
                }
            });
        }
    }
}
