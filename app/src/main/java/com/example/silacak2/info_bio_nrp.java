package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.WriterException;

import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class info_bio_nrp extends AppCompatActivity {

    URLServer serv;
    TextView txnama, txtnrp, lblnama, lbljenis, lblemail, lblttl, lblalamat, lblnrp;
    Button btnBack;
    ImageView imgqr, imgprofile;
    String nama, nrp, jenis, email, ttl, alamat, id_user, foto, pangkat, from_menu;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bio_nrp);

        txnama = (TextView) findViewById(R.id.txtnamaanggota);
        txtnrp = findViewById(R.id.txtnrpanggota);

        btnBack = findViewById(R.id.btnBackNrp);

        lblnama = findViewById(R.id.lblNamaBio);
        lbljenis = findViewById(R.id.lblJenisBio);
        lblemail = findViewById(R.id.lblEmailBio);
        lblttl = findViewById(R.id.lblTTLBio);
        lblalamat = findViewById(R.id.lblAlamatBio);
        lblnrp = findViewById(R.id.lblNRPBio);

        imgqr = findViewById(R.id.imgQR);
        imgprofile = findViewById(R.id.imgprofileanggota);


        Intent data = getIntent();
        id_user = data.getStringExtra("nrp");
        from_menu = data.getStringExtra("from_menu");

        serv = new URLServer();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from_menu.equals("home")) {
                    Intent i = new Intent(info_bio_nrp.this, adminPageNew.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        initializeProfile();
    }

    private void initializeProfile() {
        ProgressDialog pgr;
        pgr = ProgressDialog.show(info_bio_nrp.this, "Collecting Data", "Please wait...", true, false);

        AndroidNetworking.post(serv.server + "/profile/ceknrp.php")
                .addBodyParameter("nrp", id_user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {

                                AndroidNetworking.post(serv.server + "/profile/getNrpDetailProfile.php")
                                        .addBodyParameter("nrp", id_user)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    nama = response.getString("nama");
                                                    email = response.getString("email");
                                                    alamat = response.getString("alamat");
                                                    ttl = response.getString("tempat_lahir") + ", " + response.getString("tanggal_lahir");
                                                    nrp = response.getString("nrp");
                                                    jenis = response.getString("jenis_kelamin");
                                                    foto = response.getString("foto");
                                                    pangkat = response.getString("pangkat");

                                                    setProfile();
                                                    generateQRCode();
                                                    setImageProfile();
                                                    pgr.dismiss();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(info_bio_nrp.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                anError.printStackTrace();
                                            }
                                        });
                            } else {
                                Toast.makeText(info_bio_nrp.this, "NRP Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(info_bio_nrp.this,adminPageNew.class));
                                finish();
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

    private void setProfile() {

        txnama.setText(pangkat + " " + nama);
        txtnrp.setText(nrp);
        lblnama.setText(nama);
        lblemail.setText(email);
        lblttl.setText(ttl);
        lblalamat.setText(alamat);
        lbljenis.setText(jenis);
        lblnrp.setText("NRP : " + nrp);
    }

    //    https://www.geeksforgeeks.org/how-to-generate-qr-code-in-android/
    private void generateQRCode() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(nrp, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            imgqr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

    private void setImageProfile() {
        if (!foto.equalsIgnoreCase("null")) {
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imgprofile.setImageBitmap(bitmap);
        }
    }

}