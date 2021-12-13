package com.example.silacak2.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.silacak2.R;
import com.example.silacak2.URLServer;
import com.example.silacak2.adminListLaporan;
import com.example.silacak2.infoDetailLaporan;
import com.example.silacak2.model.dataLaporanModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class adapterLaporan extends RecyclerView.Adapter<adapterLaporan.Holder>{

    private ArrayList<dataLaporanModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ProgressDialog progressDialog;


    public adapterLaporan(ArrayList<dataLaporanModel> dataModel, Activity activity) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterLaporan.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_laporan, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataLaporanModel model = dataModel.get(position);
        holder.tvPelapor.setText(model.getNamaLaporan()+" ("+model.getStatusLaporan()+")");
        holder.tvDetail.setText(model.getDetailLaporan());
        holder.tvDate.setText(model.getTglLaporan());

        viewBinderHelper.bind(holder.swipeRevealLayout,model.getIdLaporan());

        holder.layoutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Konfirmasi Selesai")
                        .setMessage("Masalah dikonfikasi telah diselesaikan, Lanjutkan?")
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.SetDone();
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        })
                        .show();
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Yakin Menghapus Data secara permanen, Lanjutkan?")
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.DeleteLaporan();
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .show();
            }
        });

        holder.layoutDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kirimData = new Intent(activity, infoDetailLaporan.class);
                kirimData.putExtra("id_laporan",model.getIdLaporan());
                activity.startActivity(kirimData);
            }
        });

        holder.model = model;

    }


    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDone;
        private LinearLayout layoutDetail;
        private LinearLayout layoutDelete;
        dataLaporanModel model;
        TextView tvPelapor,tvDetail,tvDate;
        URLServer serv = new URLServer();

        public Holder(@NonNull View v) {
            super(v);

            tvPelapor = v.findViewById(R.id.tvNamaLaporan);
            tvDetail = v.findViewById(R.id.tvDetailLaporan);
            tvDate = v.findViewById(R.id.tvTglLaporan);

            swipeRevealLayout = v.findViewById(R.id.swiperlayoutLaporan);
            layoutDone = v.findViewById(R.id.layoutDone);
            layoutDetail = v.findViewById(R.id.layoutDetail);
            layoutDelete = v.findViewById(R.id.layoutDeleteLaporan);

        }
        public void SetDone(){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post(serv.server+"/laporan/setDone.php")
                    .addBodyParameter("id_laporan",model.idLaporan)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("status");
                                if (status) {
                                    Toast.makeText(activity, "Data Berhasil Dirubah", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    activity.startActivity(new Intent(activity, adminListLaporan.class));
                                } else {
                                    Toast.makeText(activity, "Data Gagal Dirubah", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(activity, "Data Gagal Dirubah" + e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            progressDialog.dismiss();
                        }
                    });
        }

        public void DeleteLaporan(){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post(serv.server+"/laporan/deleteLaporan.php")
                    .addBodyParameter("id_laporan",model.idLaporan)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("status");
                                if (status) {
                                    Toast.makeText(activity, "Data Berhasil Dihapus", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    activity.startActivity(new Intent(activity, adminListLaporan.class));
                                } else {
                                    Toast.makeText(activity, "Data Gagal Dihapus", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(activity, "Data Gagal Dihapus" + e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
