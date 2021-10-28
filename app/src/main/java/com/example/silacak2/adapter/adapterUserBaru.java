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
import com.example.silacak2.ConfrimUser;
import com.example.silacak2.R;
import com.example.silacak2.model.dataUserBaruModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class adapterUserBaru extends RecyclerView.Adapter<adapterUserBaru.Holder> {

    private ArrayList<dataUserBaruModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ProgressDialog progressDialog;

    public adapterUserBaru(Activity activity, ArrayList<dataUserBaruModel> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterUserBaru.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_baru, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull adapterUserBaru.Holder holder, int position) {
        dataUserBaruModel model = dataModel.get(position);
        holder.tvnama_user_baru.setText(model.getMNama_userBaru());
        holder.tvemail_user_baru.setText(model.getMEmail_userBaru());
        holder.tvstatus_user_baru.setText(model.getMStatus_userBaru());

        viewBinderHelper.bind(holder.swipeRevealLayout, model.MId_userBaru);

        holder.layoutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kirimData = new Intent(activity, ConfrimUser.class);
                kirimData.putExtra("id_user", model.MId_userBaru);
                kirimData.putExtra("nama_user", model.MNama_userBaru);
                kirimData.putExtra("email_user", model.MEmail_userBaru);
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
                                holder.hapusUserBaru();
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
        TextView tvstatus_user_baru, tvnama_user_baru, tvemail_user_baru;
        dataUserBaruModel model;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutConfirm;
        private LinearLayout layoutDelete;

        public Holder(@NonNull View v) {
            super(v);
            tvnama_user_baru = v.findViewById(R.id.namaUserBaru);
            tvemail_user_baru = v.findViewById(R.id.emailUserBaru);
            tvstatus_user_baru = v.findViewById(R.id.statusUserBaru);

            swipeRevealLayout = v.findViewById(R.id.swiperlayoutUserBaru);
            layoutConfirm = v.findViewById(R.id.layouteditUserBaru);
            layoutDelete = v.findViewById(R.id.layoutdeleteUserBaru);
        }

        private void hapusUserBaru() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post("https://silacak.pt-ckit.com/get_deleteuser.php")
                    .addBodyParameter("id_user", model.MId_userBaru)
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
