package com.example.silacak2;

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

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class settingAdminProfile extends AppCompatActivity {

    SessionManager sessionManager;
    URLServer serv;
    Button btnBack,btnSimpanBio,btnSimpanPass,btnInfoPass;
    Spinner spnJK,spnPangkat;
    EditText txtnama,txtemail,txttempatlahir,txttanggallahir,txtalamat,txtoldpass,txtnewpass,txtcfrpass,txtnrp;
    String nama,jenis,email,tptlahir,tgllahir,alamat,id_user,nrp,pangkat;
    final Calendar myCalendar = Calendar.getInstance();
    ArrayAdapter<CharSequence> adapter;
    ArrayList<CharSequence> listPangkat;
    ArrayAdapter adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_admin_profile);

        btnBack = findViewById(R.id.btnBackAdmin);
        btnSimpanBio = findViewById(R.id.btnsimpanBioAdm);
        btnSimpanPass = findViewById(R.id.btnsimpanPassAdm);
        btnInfoPass = findViewById(R.id.btninfoAdm);

        spnJK = findViewById(R.id.spnJenisAdm);
        spnPangkat = findViewById(R.id.spnPangkatAdm);

        txtnama = findViewById(R.id.txtNamaBioAdm);
        txtemail = findViewById(R.id.txtEmailBioAdm);
        txttempatlahir = findViewById(R.id.txtTempatBioAdm);
        txttanggallahir = findViewById(R.id.txtTanggalBioAdm);
        txtalamat = findViewById(R.id.txtAlamatBioAdm);
        txtoldpass = findViewById(R.id.txtoldpassBioAdm);
        txtnewpass = findViewById(R.id.txtnewpassBioAdm);
        txtcfrpass = findViewById(R.id.txtcfrpassBioAdm);
        txtnrp = findViewById(R.id.txtnrpAdm);

        sessionManager = new SessionManager(settingAdminProfile.this);
        serv = new URLServer();

        // Spinner Jenis Kelamin Set
        adapter = ArrayAdapter.createFromResource(this,
                R.array.list_jeniskelamin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJK.setAdapter(adapter);

        // Spinner Pangkat


        initializePangkat();

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
                new DatePickerDialog(settingAdminProfile.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(settingAdminProfile.this, profileAdmin.class);
                startActivity(i);
            }
        });

        btnSimpanBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jenis = spnJK.getSelectedItem().toString();

                if(txtalamat.getText().toString().equals("") || txtemail.getText().toString().equals("") || txtnama.getText().toString().equals("") || txttanggallahir.getText().toString().equals("")||txttempatlahir.getText().toString().equals("") || jenis.equals("--Pilih Jenis Kelamin--")){
                    Toast.makeText(settingAdminProfile.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                }else{
                    updateProfileServer();
                }
            }
        });

        btnSimpanPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtoldpass.getText().toString().equals("") || txtnewpass.getText().toString().equals("") || txtcfrpass.getText().toString().equals("")){
                    Toast.makeText(settingAdminProfile.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                }else{
                    updatePasswordServer();
                }
            }
        });

        btnInfoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pangkat = spnPangkat.getSelectedItem().toString();
                if(txtnrp.getText().toString().equals("") || pangkat.equals("--Pilih Pangkat--")){
                    Toast.makeText(settingAdminProfile.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                }else{
                    updateInfoServer();
                }
            }
        });

        initializing();

    }

    private void initializePangkat(){
        listPangkat = new ArrayList<>();
        AndroidNetworking.post(serv.server+"/struktur/getStruktur.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray ja = response.getJSONArray("data");
                            for(int i=0;i<ja.length();i++){
                                listPangkat.add(ja.getString(i));
                            }
                            adapters = new ArrayAdapter(settingAdminProfile.this,android.R.layout.simple_spinner_dropdown_item,listPangkat);
                            adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnPangkat.setAdapter(adapters);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
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
                            nrp = response.getString("nrp");
                            pangkat = response.getString("pangkat");

                            setProfile();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingAdminProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
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
        if(!nrp.equals("null")){
            txtnrp.setText(nrp);
        }
        if(!pangkat.equals("null")){
            int spinnerSelection = adapters.getPosition(pangkat);
            spnPangkat.setSelection(spinnerSelection);
        }else{
            spnPangkat.setSelection(0);
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
                                Toast.makeText(settingAdminProfile.this, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(settingAdminProfile.this, profileAdmin.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(settingAdminProfile.this, "Gagal Merubah Data", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingAdminProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(settingAdminProfile.this, response.getString("msg")+", Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
                                sessionManager.logoutSession();
                                movetoLogin();
                                stopService(new Intent(getApplicationContext(), LocationServer.class));
                            }else{
                                Toast.makeText(settingAdminProfile.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingAdminProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void updateInfoServer(){
        String pangkats = spnPangkat.getSelectedItem().toString();
        AndroidNetworking.post(serv.server + "/profile/updateAnggotaProfile.php")
                .addBodyParameter("action","info")
                .addBodyParameter("id_user",id_user)
                .addBodyParameter("pangkat",pangkats)
                .addBodyParameter("nrp",txtnrp.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(settingAdminProfile.this, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(settingAdminProfile.this, profileAdmin.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(settingAdminProfile.this, "Gagal Merubah Data", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(settingAdminProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void movetoLogin() {
        Intent i = new Intent(settingAdminProfile.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}