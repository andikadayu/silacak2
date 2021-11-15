package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class settingProfileAnggota extends AppCompatActivity {

    SessionManager sessionManager;
    URLServer serv;
    Button btnBack,btnSimpanBio,btnSimpanPass;
    Spinner spnJK;
    EditText txtnama,txtemail,txttempatlahir,txttanggallahir,txtalamat,txtoldpass,txtnewpass,txtcfrpass;
    String nama,jenis,email,tptlahir,tgllahir,alamat,id_user;
    final Calendar myCalendar = Calendar.getInstance();
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile_anggota);

        btnBack = findViewById(R.id.btnBackAnggota);
        btnSimpanBio = findViewById(R.id.btnsimpanBioAnggota);
        btnSimpanPass = findViewById(R.id.btnsimpanPassAng);

        spnJK = findViewById(R.id.spnJenisAng);

        txtnama = findViewById(R.id.txtNamaBioAng);
        txtemail = findViewById(R.id.txtEmailBioAng);
        txttempatlahir = findViewById(R.id.txtTempatBioAng);
        txttanggallahir = findViewById(R.id.txtTanggalBioAng);
        txtalamat = findViewById(R.id.txtAlamatBioAng);
        txtoldpass = findViewById(R.id.txtoldpassBioAng);
        txtnewpass = findViewById(R.id.txtnewpassBioAng);
        txtcfrpass = findViewById(R.id.txtcfrpassBioAng);


        sessionManager = new SessionManager(settingProfileAnggota.this);
        serv = new URLServer();

        // Spinner Jenis Kelamin Set
        adapter = ArrayAdapter.createFromResource(this,
                R.array.list_jeniskelamin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJK.setAdapter(adapter);


        // For Calendar https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthofyear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayofmonth);
                updateLabel();
            }
        };

        txttanggallahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(settingProfileAnggota.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(settingProfileAnggota.this, profileAnggota.class);
                startActivity(i);
            }
        });

        btnSimpanBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jenis = spnJK.getSelectedItem().toString();

                if(txtalamat.getText().toString().equals("") || txtemail.getText().toString().equals("") || txtnama.getText().toString().equals("") || txttanggallahir.getText().toString().equals("")||txttempatlahir.getText().toString().equals("") || jenis.equals("--Pilih Jenis Kelamin--")){
                    Toast.makeText(settingProfileAnggota.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                }else{
                    updateProfileServer();
                }
            }
        });

        btnSimpanPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtoldpass.getText().toString().equals("") || txtnewpass.getText().toString().equals("") || txtcfrpass.getText().toString().equals("")){
                    Toast.makeText(settingProfileAnggota.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                }else{
                    updatePasswordServer();
                }
            }
        });

        initializing();
    }

    private void initializing(){
        id_user = sessionManager.getUserDetail().get(SessionManager.ID_USER);
        AndroidNetworking.post(serv.server + "/profile/getDetailProfile.php")
                .addBodyParameter("id_user",id_user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            nama = response.getString("nama");
                            email = response.getString("email");
                            alamat = response.getString("alamat");
                            jenis = response.getString("jenis_kelamin");
                            tptlahir = response.getString("tempat_lahir");
                            tgllahir = response.getString("tanggal_lahir");

                            setProfile();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingProfileAnggota.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void setProfile(){
        txtnama.setText(nama);
        txtemail.setText(email);
        if(!jenis.equals("null")){
            int spinnerSelections = adapter.getPosition(jenis);
            spnJK.setSelection(spinnerSelections);
        }else{
            spnJK.setSelection(0);
        }
        if(!alamat.equals("null")){
            txtalamat.setText(alamat);
        }
        if(!tptlahir.equals("null")){
            txttempatlahir.setText(tptlahir);
        }
        if(!tgllahir.equals("null")){
            txttanggallahir.setText(tgllahir);
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txttanggallahir.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateProfileServer(){
        String jenis = spnJK.getSelectedItem().toString();
        AndroidNetworking.post(serv.server + "/profile/updateAnggotaProfile.php")
                .addBodyParameter("action","biodata")
                .addBodyParameter("id_user",id_user)
                .addBodyParameter("nama",txtnama.getText().toString())
                .addBodyParameter("tempat",txttempatlahir.getText().toString())
                .addBodyParameter("tanggal",txttanggallahir.getText().toString())
                .addBodyParameter("jenis",jenis)
                .addBodyParameter("alamat",txtalamat.getText().toString())
                .addBodyParameter("email",txtemail.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(settingProfileAnggota.this, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(settingProfileAnggota.this, profileAnggota.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(settingProfileAnggota.this, "Gagal Merubah Data", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingProfileAnggota.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void updatePasswordServer(){
        AndroidNetworking.post(serv.server + "/profile/updateAnggotaProfile.php")
                .addBodyParameter("action","password")
                .addBodyParameter("id_user",id_user)
                .addBodyParameter("old",txtoldpass.getText().toString())
                .addBodyParameter("new",txtnewpass.getText().toString())
                .addBodyParameter("confirm",txtcfrpass.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(settingProfileAnggota.this, response.getString("msg")+", Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
                                sessionManager.logoutSession();
                                movetoLogin();
                                stopService(new Intent(getApplicationContext(), LocationServer.class));
                            }else{
                                Toast.makeText(settingProfileAnggota.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingProfileAnggota.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void movetoLogin() {
        Intent i = new Intent(settingProfileAnggota.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}