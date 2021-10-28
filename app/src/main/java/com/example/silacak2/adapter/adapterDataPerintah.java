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
import com.example.silacak2.adminListPerintah;
import com.example.silacak2.model.DataPerintahModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.ArrayList;

public class adapterDataPerintah extends RecyclerView.Adapter<adapterDataPerintah.Holder> {
    URLServer urlServer;
    private ArrayList<DataPerintahModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ProgressDialog progressDialog;

    public adapterDataPerintah(Activity activity, ArrayList<DataPerintahModel> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }


    @NonNull
    @Override
    public adapterDataPerintah.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_perintah, parent, false);
        Holder holder = new Holder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull adapterDataPerintah.Holder holder, int position) {
        DataPerintahModel model = dataModel.get(position);
        holder.tvstatus_peritah.setText(model.getStatus_perintah());
        holder.tv_detail.setText(model.getDetail_perintah());

        viewBinderHelper.bind(holder.swipeRevealLayout, model.getId_perintah());

        holder.layoutSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Konfirmasi Selesai")
                        .setMessage("Perintah dikonfikasi telah diselesaikan, Lanjutkan?")
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.selesaiPerintah();
                                dataModel.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .show();
            }
        });

        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvstatus_peritah, tv_detail;
        DataPerintahModel model;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutSelesai;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tv_detail = itemView.findViewById(R.id.tvdetailPerintah);
            tvstatus_peritah = itemView.findViewById(R.id.statusPerintah);

            swipeRevealLayout = itemView.findViewById(R.id.swiperlayoutPerintah);
            layoutSelesai = itemView.findViewById(R.id.layoutselesai);
        }

        private void selesaiPerintah() {
            urlServer = new URLServer();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post(urlServer.setPerintahSelesai())
                    .addBodyParameter("purpose", "selesai")
                    .addBodyParameter("id_perintah", model.id_perintah)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("status");
                                if (status) {
                                    Toast.makeText(activity, "Data Perintah Berhasil Dirubah", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    Intent i = new Intent(activity, adminListPerintah.class);
                                    activity.startActivity(i);
                                } else {
                                    Toast.makeText(activity, "Data Perintah Gagal Dirubah", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "Data Perintah Gagal Dirubah" + e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Toast.makeText(activity, "Failed " + anError, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
