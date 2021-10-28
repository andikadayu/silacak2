package com.example.silacak2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silacak2.R;
import com.example.silacak2.model.dataPesanGroubModelAdmin;

import java.util.ArrayList;

public class adapterPesanGroub extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    private final Context context;
    ArrayList<dataPesanGroubModelAdmin> list;

    public adapterPesanGroub(Context context, ArrayList<dataPesanGroubModelAdmin> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new adapterPesanGroub.MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_tampilpesan_groub, parent, false));
        }
        return new adapterPesanGroub.MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_tampilpesanout_groub, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list.get(position).messageType == MESSAGE_TYPE_IN) {
            ((adapterPesanGroub.MessageInViewHolder) holder).bind(position);
        } else {
            ((adapterPesanGroub.MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).messageType;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV, messageTV, dateTV, tgl;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.namaGroub);
            messageTV = itemView.findViewById(R.id.message_textGroub);
            dateTV = itemView.findViewById(R.id.date_textGroub);
            tgl = itemView.findViewById(R.id.dateTGroub);
        }

        void bind(int position) {
            dataPesanGroubModelAdmin dataPesanModel = list.get(position);
            nameTV.setText(dataPesanModel.MNama_groub);
            messageTV.setText(dataPesanModel.MPesan);
            dateTV.setText(dataPesanModel.MJam);
            tgl.setText(dataPesanModel.MTanggal);
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV, messageTV, dateTV, tgl;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.namaGroub);
            messageTV = itemView.findViewById(R.id.message_textGroub);
            dateTV = itemView.findViewById(R.id.date_textGroub);
            tgl = itemView.findViewById(R.id.dateTGroub);
        }

        void bind(int position) {
            dataPesanGroubModelAdmin messageModel = list.get(position);
            nameTV.setText(messageModel.MNama_groub);
            messageTV.setText(messageModel.MPesan);
            dateTV.setText(messageModel.MJam);
            tgl.setText(messageModel.MTanggal);
        }
    }
}
