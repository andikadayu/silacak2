package com.example.silacak2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silacak2.R;
import com.example.silacak2.model.dataTampilPesanModelAnggota;

import java.util.ArrayList;

public class adapterTampilPesanAnggota extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    private final Context context;
    ArrayList<dataTampilPesanModelAnggota> list;

    public adapterTampilPesanAnggota(Context context, ArrayList<dataTampilPesanModelAnggota> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new adapterTampilPesanAnggota.MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_tampilpesan_anggota, parent, false));
        }
        return new adapterTampilPesanAnggota.MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_tampilpesanout_anggota, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list.get(position).messageType == MESSAGE_TYPE_IN) {
            ((adapterTampilPesanAnggota.MessageInViewHolder) holder).bind(position);
        } else {
            ((adapterTampilPesanAnggota.MessageOutViewHolder) holder).bind(position);
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

        TextView messageTV, dateTV, tgl;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_textAnggota);
            dateTV = itemView.findViewById(R.id.date_textAnggota);
            tgl = itemView.findViewById(R.id.dateTAnggota);
        }

        void bind(int position) {
            dataTampilPesanModelAnggota dataTampilPesanModel = list.get(position);
            messageTV.setText(dataTampilPesanModel.MPesan);
            dateTV.setText(dataTampilPesanModel.MJam);
            tgl.setText(dataTampilPesanModel.MTanggal);
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, dateTV, tgl;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_textAnggota);
            dateTV = itemView.findViewById(R.id.date_textAnggota);
            tgl = itemView.findViewById(R.id.dateTAnggota);
        }

        void bind(int position) {
            dataTampilPesanModelAnggota messageModel = list.get(position);
            messageTV.setText(messageModel.MPesan);
            dateTV.setText(messageModel.MJam);
            tgl.setText(messageModel.MTanggal);
        }
    }
}
