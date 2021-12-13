package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterListAnggotaCoba;
import com.example.silacak2.model.ListAnggotaModel;
import com.example.silacak2.model.perintahModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class detail_of_perintah extends AppCompatActivity {

    ListView lvlistanggota;
    Button btnOke;
    EditText editDetail;
    ArrayList<ListAnggotaModel> userList;
    URLServer serv;;
    TextView tvClear;
    ArrayAdapter<ListAnggotaModel> adapter;
    ArrayAdapter<ListAnggotaModel> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_perintah);

        serv = new URLServer();

        lvlistanggota = findViewById(R.id.lvListAnggota);
        btnOke = findViewById(R.id.btnOKPerintah);
        editDetail = findViewById(R.id.editDetailPerintah);
        tvClear = findViewById(R.id.clearlist);

        lvlistanggota.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        userList = new ArrayList<ListAnggotaModel>();

        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printSelectedItem();
            }
        });

        initListViewData();

        lvlistanggota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView v = (CheckedTextView) view.findViewById(R.id.listAnggotadetails);

                if (lvlistanggota.isItemChecked(0)) {
                    for (int a = 0; a < userList.size(); a++) {
                        lvlistanggota.setItemChecked(a, true);
                    }
                    Toast.makeText(detail_of_perintah.this,"Menyetel Ke Seluruh Anggota dalam List",Toast.LENGTH_SHORT).show();
                }

            }

        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int a = 0; a < userList.size(); a++) {
                    lvlistanggota.setItemChecked(a, false);
                }
                Toast.makeText(detail_of_perintah.this,"Menghapus Semua Centang dalam List",Toast.LENGTH_SHORT).show();
                adapter.clear();
                initListViewData();
//                lvlistanggota.clearChoices();
            }
        });




    }

    private void initListViewData() {
        ProgressDialog pgb = new ProgressDialog(detail_of_perintah.this);
        pgb.show();
        String lat = String.valueOf(perintahModel.getLatitudePerintah());
        String lng = String.valueOf(perintahModel.getLongitudePerintah());
        AndroidNetworking.post(serv.getAllListAnggota())
                .addBodyParameter("latitude", lat)
                .addBodyParameter("longitude", lng)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ListAnggotaModel semua = new ListAnggotaModel("Semua Anggota", "semua","null");
                            userList.add(semua);
                            JSONArray ja = response.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                String name = jo.getString("nama");
                                String ids = jo.getString("id_user");
                                String foto = jo.getString("foto");

                                ListAnggotaModel userss = new ListAnggotaModel(name, ids,foto);

                                userList.add(userss);

                            }

                            adapter = new adapterListAnggotaCoba(detail_of_perintah.this,userList);
                            lvlistanggota.setAdapter(adapter);
                            pgb.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });


    }

    private void printSelectedItem() {
        SparseBooleanArray sp = lvlistanggota.getCheckedItemPositions();
        ArrayList<String> aah = new ArrayList<String>();
        String lat = String.valueOf(perintahModel.getLatitudePerintah());
        String lng = String.valueOf(perintahModel.getLongitudePerintah());
        String detail_perintah = editDetail.getText().toString();

        if (sp.size() > 0) {
            for (int i = 1; i < sp.size(); i++) {
                if (sp.valueAt(i) == true) {
                    ListAnggotaModel user = (ListAnggotaModel) lvlistanggota.getItemAtPosition(i);

                    String s = user.getId().toString();
                    aah.add(s);
                }
            }
                String ids = aah.toString();
                String id_lokasi = ids.replace("[", "").replace("]", "");
            if (!detail_perintah.equals("") && !id_lokasi.isEmpty()) {
//                sendToServer(lat, lng, id_lokasi, detail_perintah);
                sendToServer(id_lokasi, detail_perintah);
            } else {

                Toast.makeText(detail_of_perintah.this, "Complete the form", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(detail_of_perintah.this, "Complete the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToServer(String id_user, String pesan) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fjam = new SimpleDateFormat("HH:mm:ss");
        String tgl = ftgl.format(cal.getTime());
        String jam = fjam.format(cal.getTime());
        AndroidNetworking.post(serv.setPesan())
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("pesan", pesan)
                .addBodyParameter("tgl", tgl)
                .addBodyParameter("jam", jam)
                .addBodyParameter("action", "pesankirim")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(detail_of_perintah.this, "Pesan Terkirim !!!", Toast.LENGTH_SHORT).show();
                                perintahModel.clearPosPerintah();
                                startActivity(new Intent(detail_of_perintah.this, adminPageNew.class));
                                finish();
                            } else {
                                Toast.makeText(detail_of_perintah.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

//    private void sendToServer(String lat, String lng, String id_lokasi, String detail_perintah) {
//        AndroidNetworking.post(serv.setPerintahNew())
//                .addBodyParameter("lat", lat)
//                .addBodyParameter("lng", lng)
//                .addBodyParameter("id_lokasi", id_lokasi)
//                .addBodyParameter("detail_perintah", detail_perintah)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            boolean status = response.getBoolean("status");
//                            if (status) {
//                                Toast.makeText(detail_of_perintah.this, "Menambah Perintah Berhasil", Toast.LENGTH_SHORT).show();
//                                perintahModel.clearPosPerintah();
//                                startActivity(new Intent(detail_of_perintah.this, adminPageNew.class));
//                                finish();
//                            } else {
//                                Toast.makeText(detail_of_perintah.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                    }
//                });
//    }
}