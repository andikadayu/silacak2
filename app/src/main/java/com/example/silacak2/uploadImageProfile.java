package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class uploadImageProfile extends AppCompatActivity {

    URLServer serv;
    SessionManager sessionManager;
    ImageView imgView;
    Button btnChoose,btnUpload,btnBack;
    Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 12312;
    Uri filePath;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image_profile);

        imgView = findViewById(R.id.imgViewChange);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBackFoto);

        serv = new URLServer();
        sessionManager = new SessionManager(uploadImageProfile.this);

        id_user = sessionManager.getUserDetail().get(SessionManager.ID_USER);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileExporer();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String role = sessionManager.getUserDetail().get(SessionManager.ROLE);
                if(role.equalsIgnoreCase("anggota")){
                    Intent i = new Intent(uploadImageProfile.this, profileAnggota.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(uploadImageProfile.this, profileAdmin.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        initializing();

    }

    private void initializing(){
        AndroidNetworking.post(serv.server + "/profile/photoAnggota.php")
                .addBodyParameter("id_user",id_user)
                .addBodyParameter("action","get")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String images = response.getString("image");
                            if (!images.equalsIgnoreCase("null")){
                                byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                                bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

                                imgView.setImageBitmap(bitmap);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(uploadImageProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void showFileExporer(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getStringImage(@NonNull Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        ProgressDialog pgr;
        Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        if(bitmap.sameAs(emptyBitmap)){
            Toast.makeText(uploadImageProfile.this, "Pilih Foto Dahulu", Toast.LENGTH_SHORT).show();
        }else{
            pgr = ProgressDialog.show(uploadImageProfile.this,"Uploading Image", "Please wait...",true,true);
            String images = getStringImage(bitmap);
            AndroidNetworking.post(serv.server + "/profile/photoAnggota.php")
                    .addBodyParameter("id_user",id_user)
                    .addBodyParameter("action","upload")
                    .addBodyParameter("image",images)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("status");
                                if(status){
                                    Toast.makeText(uploadImageProfile.this, "Berhasil Dirubah", Toast.LENGTH_SHORT).show();
                                    pgr.dismiss();
                                }else{
                                    Toast.makeText(uploadImageProfile.this, "Gagal Dirubah", Toast.LENGTH_SHORT).show();
                                    pgr.dismiss();
                                }
                            } catch (JSONException e) {
                                pgr.dismiss();
                                e.printStackTrace();
                                Toast.makeText(uploadImageProfile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            pgr.dismiss();
                        }
                    });
        }
    }
}