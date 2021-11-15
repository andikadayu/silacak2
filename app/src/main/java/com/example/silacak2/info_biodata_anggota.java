package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.WriterException;

import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class info_biodata_anggota extends AppCompatActivity {
    URLServer serv;
    TextView txnama,txtnrp,lblnama,lbljenis,lblemail,lblttl,lblalamat,lblnrp;
    Button btnView, btnChange,btnBack;
    ImageView imgqr,imgprofile,imglarge;
    String nama,nrp,jenis,email,ttl,alamat,id_user,foto,pangkat;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Spinner spnpangkat;
    EditText editnrp;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_biodata_anggota);

        txnama = (TextView) findViewById(R.id.txtnamaanggota);
        txtnrp = findViewById(R.id.txtnrpanggota);

        btnBack = findViewById(R.id.btnBackBio);
        btnChange = findViewById(R.id.btnDialBio);
        btnView = findViewById(R.id.btnInfoBio);

        lblnama = findViewById(R.id.lblNamaBio);
        lbljenis = findViewById(R.id.lblJenisBio);
        lblemail = findViewById(R.id.lblEmailBio);
        lblttl = findViewById(R.id.lblTTLBio);
        lblalamat = findViewById(R.id.lblAlamatBio);
        lblnrp = findViewById(R.id.lblNRPBio);

        imgqr = findViewById(R.id.imgQR);
        imgprofile = findViewById(R.id.imgprofileanggota);


        Intent data = getIntent();
        id_user = data.getStringExtra("id_user");

        serv = new URLServer();

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogView();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogChange();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(info_biodata_anggota.this, adminListAnggota.class);
                startActivity(i);
                finish();
            }
        });

        initializeProfile();
    }

    private void initializeProfile(){
        ProgressDialog pgr;
        pgr = ProgressDialog.show(info_biodata_anggota.this,"Collecting Data", "Please wait...",true,false);
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
                            Toast.makeText(info_biodata_anggota.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
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

    private void openDialogView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(info_biodata_anggota.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(info_biodata_anggota.this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewphoto, null);
        imglarge = view.findViewById(R.id.viewphotobio);

        if (!foto.equalsIgnoreCase("null")){
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            imglarge.setImageBitmap(bitmap);
        }

        builder.setView(view)
                .setTitle("View Photo")
                .setIcon(R.drawable.user1)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog ald = builder.create();
        ald.show();
    }

    private void openDialogChange(){
        AlertDialog.Builder builder = new AlertDialog.Builder(info_biodata_anggota.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(info_biodata_anggota.this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.editbioinfo, null);
        spnpangkat = view.findViewById(R.id.spnBioPangkat);
        editnrp = view.findViewById(R.id.txtBioNrp);

        // Spinner Pangkat
        adapter = ArrayAdapter.createFromResource(this,
                R.array.pangkat_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnpangkat.setAdapter(adapter);

        editnrp.setText(nrp);

        if(!pangkat.equals("null")){
            int spinnerSelection = adapter.getPosition(pangkat);
            spnpangkat.setSelection(spinnerSelection);
        }else{
            spnpangkat.setSelection(0);
        }

        builder.setView(view)
                .setTitle("Merubah Info")
                .setIcon(R.drawable.ic_baseline_verified_user_24)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nrp = editnrp.getText().toString();
                        String pangkat = spnpangkat.getSelectedItem().toString();
                        if(pangkat.equals("--Pilih Pangkat--") || nrp.equals("")){
                            Toast.makeText(info_biodata_anggota.this, "Lengkapi Informasi", Toast.LENGTH_SHORT).show();
                        }else{
                            setUpdateInfo(nrp, pangkat);
                        }
                    }
                });

        AlertDialog ald = builder.create();
        ald.show();
    }

    private void setUpdateInfo(String nrp,String pangkat){
        AndroidNetworking.post(serv.server + "/profile/updateAnggotaProfile.php")
                .addBodyParameter("action","info")
                .addBodyParameter("id_user",id_user)
                .addBodyParameter("pangkat",pangkat)
                .addBodyParameter("nrp",nrp)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(info_biodata_anggota.this, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(info_biodata_anggota.this, adminListAnggota.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(info_biodata_anggota.this, "Gagal Merubah Data", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(info_biodata_anggota.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }
}