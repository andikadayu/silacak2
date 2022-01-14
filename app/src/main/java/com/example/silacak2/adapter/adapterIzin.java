package com.example.silacak2.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silacak2.R;
import com.example.silacak2.model.IzinModel;

import java.util.ArrayList;

public class adapterIzin extends RecyclerView.Adapter<adapterIzin.Holder> {
    Activity activity;
    ArrayList<IzinModel> dataModel;

    public adapterIzin(Activity activity, ArrayList<IzinModel> dataModel) {
        this.activity = activity;
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_izin_custom, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        IzinModel dataModels = dataModel.get(position);

        holder.tvPerihal.setText(dataModels.getPerihal());
        holder.tvTgl.setText(dataModels.getDate_permit());
        holder.tvStatus.setText(dataModels.getStatus());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }


    public class Holder extends RecyclerView.ViewHolder{

        TextView tvPerihal,tvTgl,tvStatus;

        public Holder(@NonNull View v) {
            super(v);

            tvPerihal = v.findViewById(R.id.tvPerihal);
            tvTgl = v.findViewById(R.id.tvTgl);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }

}
