package com.example.silacak2.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.silacak2.info_biodata_anggota;
import com.example.silacak2.model.dataAnggotaModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class adapterAnggota extends RecyclerView.Adapter<adapterAnggota.Holder> {
    private ArrayList<dataAnggotaModel> dataModel;
    private Activity activity;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ProgressDialog progressDialog;
    private ImageView imgFoto;

    public adapterAnggota(Activity activity, ArrayList<dataAnggotaModel> dataModel) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public adapterAnggota.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anggota, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    private void setImageProfile(String foto){
        if (!foto.equalsIgnoreCase("null")){
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            imgFoto.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        dataAnggotaModel model = dataModel.get(position);
        holder.tvnama_anggota.setText(model.getMNama_anggota());
        holder.tvemail_anggota.setText(model.getMEmail_anggota());
        holder.tvstatus_anggota.setText(model.getMStatus_anggota());

        setImageProfile(model.getMFoto_anggota());

        viewBinderHelper.bind(holder.swipeRevealLayout, model.MId_user_anggota);

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Data Akan Dihapus, Lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.hapusAnggota();
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

        holder.layoutBlokir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Buka Blokir Akun")
                        .setMessage("Buka Akun Ini ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.blokirAkun();
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

        holder.layoutBiodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kirimData = new Intent(activity, info_biodata_anggota.class);
                kirimData.putExtra("id_user", model.getMId_user_anggota());
                activity.startActivity(kirimData);
            }
        });


        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvstatus_anggota, tvnama_anggota, tvemail_anggota;
        dataAnggotaModel model;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutBlokir;
        private LinearLayout layoutDelete;
        private LinearLayout layoutBiodata;

        public Holder(@NonNull View v) {
            super(v);
            tvstatus_anggota = v.findViewById(R.id.statusAnggota);
            tvnama_anggota = v.findViewById(R.id.namaAnggota);
            tvemail_anggota = v.findViewById(R.id.emailAnggota);

            swipeRevealLayout = v.findViewById(R.id.swiperlayoutAnggota);
            layoutBlokir = v.findViewById(R.id.layoutBlokirAnggota);
            layoutDelete = v.findViewById(R.id.layoutdeleteAnggota);
            layoutBiodata = v.findViewById(R.id.layoutBiodataAnggota);
            imgFoto = v.findViewById(R.id.listViewPhotoAnggota);
        }

        private void hapusAnggota() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post("https://silacak.pt-ckit.com/get_deleteanggota.php")
                    .addBodyParameter("id_user", model.MId_user_anggota)
                    .addBodyParameter("action", "hapusAnggota")
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

        private void blokirAkun() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            AndroidNetworking.post("https://silacak.pt-ckit.com/bukaBlokirUserBaru.php")
                    .addBodyParameter("id_user", model.MId_user_anggota)
                    .addBodyParameter("action", "konfirmasi")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("pesan").equals("sukses")) {
                                    Toast.makeText(activity, "Berhasil Dibuka", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(activity, "Data Dibuka", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(activity, "Data Gagal Diubah" + e, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(activity, "Failed " + anError, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
