package com.example.silacak2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.example.silacak2.advances.FileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIzinActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 10;
    final Calendar calendarOne = Calendar.getInstance();
    final Calendar calendarTwo = Calendar.getInstance();
    TextInputLayout txtDari, txtSampai, txtPilihan;
    TextInputEditText txtDateFrom, txtDateUntil;
    AutoCompleteTextView autoComplete;
    MaterialButton btnKirim, btnPilih;
    TextView tvFile;
    ArrayAdapter adapter;
    URLServer urlServer = new URLServer();
    SessionManager sessionManager;
    Uri uriPenjelajan;
    String id_user, from_date, until_date, perihal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_izin);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle("Tambah Perizinan");

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        actionBar.setDisplayHomeAsUpEnabled(true);

        txtDari = findViewById(R.id.txtDari);
        txtSampai = findViewById(R.id.txtSampai);
        txtPilihan = findViewById(R.id.txtPilihan);
        txtDateFrom = findViewById(R.id.txtDateFrom);
        txtDateUntil = findViewById(R.id.txtDateUntil);
        btnKirim = findViewById(R.id.btnKirim);
        autoComplete = findViewById(R.id.autoComplete);
        btnPilih = findViewById(R.id.btnPilih);
        tvFile = findViewById(R.id.tvFile);

        sessionManager = new SessionManager(getApplicationContext());
        id_user = sessionManager.getUserDetail().get(SessionManager.ID_USER);

        AndroidNetworking.initialize(getApplicationContext());

        enablePermission();

        txtDateFrom.setOnClickListener(view -> {
            new DatePickerDialog(AddIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                    calendarOne.set(Calendar.YEAR, year);
                    calendarOne.set(Calendar.MONTH, monthofyear);
                    calendarOne.set(Calendar.DAY_OF_MONTH, dayofmonth);
                    updateLabelOne();
                }
            }, calendarOne.get(Calendar.YEAR), calendarOne.get(Calendar.MONTH), calendarOne.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtDateUntil.setOnClickListener(view -> {
            new DatePickerDialog(AddIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                    calendarTwo.set(Calendar.YEAR, year);
                    calendarTwo.set(Calendar.MONTH, monthofyear);
                    calendarTwo.set(Calendar.DAY_OF_MONTH, dayofmonth);
                    updateLabelTwo();
                }
            }, calendarTwo.get(Calendar.YEAR), calendarTwo.get(Calendar.MONTH), calendarTwo.get(Calendar.DAY_OF_MONTH)).show();
        });


        adapter = ArrayAdapter.createFromResource(this,
                R.array.perihal, R.layout.list_items);

        autoComplete.setAdapter(adapter);


        btnPilih.setOnClickListener(view -> {
            selectFile();
        });

        btnKirim.setOnClickListener((view) -> {
            from_date = txtDari.getEditText().getText().toString();
            until_date = txtSampai.getEditText().getText().toString();
            perihal = txtPilihan.getEditText().getText().toString();

            if (from_date.equals("") || until_date.equals("") || perihal.equals("") || uriPenjelajan == null) {
                Toast.makeText(this, "Complete a form", Toast.LENGTH_SHORT).show();
            } else {
                kirimData(this);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, IzinActivity.class));
            finish();
        }
        return true;
    }

    private void enablePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(AddIzinActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
            if(!Environment.isExternalStorageManager()){
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        }else{
            ActivityCompat.requestPermissions(AddIzinActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                enablePermission();
            }
        }
    }

    private void updateLabelOne() {
        String myFormat = "yyyy-MM-dd"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtDari.getEditText().setText(sdf.format(calendarOne.getTime()));
    }

    private void updateLabelTwo() {
        String myFormat = "yyyy-MM-dd"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtSampai.getEditText().setText(sdf.format(calendarTwo.getTime()));
    }

    private void selectFile() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",// .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip",
                        "image/*"
                };
        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "Choose File"), 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 300) {
                assert data != null;
                final Uri selected = data.getData();
                uriPenjelajan = selected;
                File filePenjelasan = new File(selected.getPath());
                tvFile.setText(filePenjelasan.getName());
            }
        }
    }

    private void kirimData(Activity activity) {
        ProgressDialog pdg = new ProgressDialog(activity);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();

//            File penjelasan = new File(uriPenjelajan.getPath());
        File penjelasan = null;
        try {
            penjelasan = FileUtils.getFileFromUri(activity,uriPenjelajan);
            AndroidNetworking.upload(urlServer.server + "/absensi/addIzin.php")
                    .addMultipartParameter("id_user", id_user)
                    .addMultipartParameter("from_date", from_date)
                    .addMultipartParameter("until_date", until_date)
                    .addMultipartParameter("perihal", perihal)
                    .addMultipartFile("penjelasan", penjelasan)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // Do Something
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");
                                pdg.dismiss();
                                if(status){
                                    Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(activity,IzinActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                pdg.dismiss();
                                Toast.makeText(activity, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}