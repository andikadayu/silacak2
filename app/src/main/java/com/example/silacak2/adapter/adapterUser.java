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
import com.example.silacak2.ConfrimAnggota;
import com.example.silacak2.R;
import com.example.silacak2.URLServer;
import com.example.silacak2.model.dataUserModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class adapterUser extends RecyclerView.Adapter<adapterUser.Holder> {
    URLServer serv;
    private ArrayList<dataUserModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ProgressDialog progressDialog;

    public adapterUser(Activity activity, ArrayList<dataUserModel> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterUser.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull adapterUser.Holder holder, int position) {
        dataUserModel model = dataModel.get(position);
        holder.tvnama_user.setText(model.getMNama_user());
        holder.tvemail_user.setText(model.getMEmail_user());
        holder.tvstatus_user.setText(model.getMStatus_user());

        viewBinderHelper.bind(holder.swipeRevealLayout, model.MId_user);


        holder.layoutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kirimData = new Intent(activity, ConfrimAnggota.class);
                kirimData.putExtra("id_user", model.getMId_user());
                kirimData.putExtra("nama_user", model.getMNama_user());
                kirimData.putExtra("email_user", model.getMEmail_user());
                activity.startActivity(kirimData);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Data Akan Dihapus, Lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.hapusUser();
                                dataModel.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
        TextView tvstatus_user, tvnama_user, tvemail_user;
        dataUserModel model;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutConfirm;
        private LinearLayout layoutDelete;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_user = v.findViewById(R.id.namaUser);
            tvemail_user = v.findViewById(R.id.emailUser);
            tvstatus_user = v.findViewById(R.id.statusUser);

            swipeRevealLayout = v.findViewById(R.id.swiperlayout);
            layoutConfirm = v.findViewById(R.id.layoutedit);
            layoutDelete = v.findViewById(R.id.layoutdelete);
        }

        private void hapusUser() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post("https://silacak.pt-ckit.com/get_deleteuser.php")
                    .addBodyParameter("id_user", model.MId_user)
                    .addBodyParameter("action", "hapusUser")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("sukses")) {
                                    Toast.makeText(activity, "Data Berhasil DiHapus", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
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
                            Toast.makeText(activity, "Failed " + anError, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
