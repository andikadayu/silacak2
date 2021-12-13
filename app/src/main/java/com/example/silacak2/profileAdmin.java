package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.WriterException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class profileAdmin extends AppCompatActivity {
    SessionManager sessionManager;
    URLServer serv;
    TextView txnama,txtnrp,lblnama,lbljenis,lblemail,lblttl,lblalamat,lblnrp;
    Button btLogout, btnfoto,btnedit;
    ImageView imgqr,imgprofile;
    String nama,nrp,jenis,email,ttl,alamat,id_user,foto,pangkat;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        txnama = (TextView) findViewById(R.id.txtnamaanggota);
        txtnrp = findViewById(R.id.txtnrpanggota);

        btLogout = (Button) findViewById(R.id.btnlogoutsAnggota);
        btnedit = findViewById(R.id.btneditbiodata);
        btnfoto = findViewById(R.id.btneditfoto);

        lblnama = findViewById(R.id.lblNamaBio);
        lbljenis = findViewById(R.id.lblJenisBio);
        lblemail = findViewById(R.id.lblEmailBio);
        lblttl = findViewById(R.id.lblTTLBio);
        lblalamat = findViewById(R.id.lblAlamatBio);
        lblnrp = findViewById(R.id.lblNRPBio);

        imgqr = findViewById(R.id.imgQR);
        imgprofile = findViewById(R.id.imgprofileanggota);

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        //bottomNavigationView.setSelectedItemId(R.id.profiles);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.profiles:
//                        return true;
                    case R.id.homes:
                        startActivity(new Intent(profileAdmin.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(profileAdmin.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(profileAdmin.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(profileAdmin.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(profileAdmin.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        serv = new URLServer();
        sessionManager = new SessionManager(profileAdmin.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        initializeProfile();
//        txnama.setText(sessionManager.getUserDetail().get(SessionManager.NAMA));

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutSession();
                movetoLogin();
                stopService(new Intent(getApplicationContext(), LocationServer.class));
            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(profileAdmin.this, settingAdminProfile.class);
                startActivity(i);
            }
        });

        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(profileAdmin.this, uploadImageProfile.class);
                startActivity(i);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_admin, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.oProfileAdmin){
            startActivity(new Intent(this, profileAdmin.class));
        }
//        if(item.getItemId()==R.id.oListPerintah){
//            startActivity(new Intent(this,adminListPerintah.class));
//        }
        if(item.getItemId()==R.id.oLokasiUser){
            startActivity(new Intent(this,adminLokasiAll.class));
        }
        if(item.getItemId() == R.id.oScanQR){
            startActivity(new Intent(this, scan_qrcode.class));
        }
        if(item.getItemId() == R.id.oLaporan){
            startActivity(new Intent(this,adminListLaporan.class));
        }

        if(item.getItemId() == R.id.oAbsensi){
            startActivity(new Intent(this,AbsensiActivity.class));
        }
        return true;
    }
    private void movetoLogin() {

        Intent i = new Intent(profileAdmin.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void initializeProfile(){
        ProgressDialog pgr;
        pgr = ProgressDialog.show(profileAdmin.this,"Collecting Data", "Please wait...",true,false);
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
                            ttl = response.getString("tempat_lahir") + ", "+ response.getString("tanggal_lahir");
                            nrp = response.getString("nrp");
                            jenis = response.getString("jenis_kelamin");
                            foto = response.getString("foto");
                            pangkat = response.getString("pangkat");

                            setProfile();
                            generateQRCode();
                            setImageProfile();
                            pgr.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(profileAdmin.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }
    private void setProfile(){

        txnama.setText(pangkat +" "+nama);
        txtnrp.setText(nrp);
        lblnama.setText(nama);
        lblemail.setText(email);
        lblttl.setText(ttl);
        lblalamat.setText(alamat);
        lbljenis.setText(jenis);
        lblnrp.setText("NRP : "+nrp);
    }
    //    https://www.geeksforgeeks.org/how-to-generate-qr-code-in-android/
    private void generateQRCode(){
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

    private void setImageProfile(){
        if (!foto.equalsIgnoreCase("null")){
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            imgprofile.setImageBitmap(bitmap);
        }
    }
}