package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;

import javax.annotation.Nullable;

public class Home extends AppCompatActivity {

    TextView textView2, namaAkun, bt_ganti,bt_logout,tentang;
    private ImageView fimageView;
    private static final int CAMERA_REQUEST = 9999;
    private Button presensi,rekap;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    int hari, bulan, tahun;
    String bulan1, userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        fimageView = (ImageView)findViewById(R.id.imageViewhasil);

        firebaseAuth = FirebaseAuth.getInstance();
        bt_ganti = (TextView)findViewById(R.id.bt_ganti) ;
        namaAkun = (TextView)findViewById(R.id.nama);
        bt_logout = (TextView)findViewById(R.id.bt_logout);
        presensi = (Button)findViewById(R.id.buttonPres);
        tentang = (TextView)findViewById(R.id.tentang);
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        rekap = (Button)findViewById(R.id.rekap);


        DocumentReference documentReference = firestore.collection("Karyawan").document(userID);
        getUserName(documentReference);

        Calendar calendar = Calendar.getInstance();
        hari = calendar.get(Calendar.DATE);
        bulan = calendar.get(Calendar.MONTH);
        if (bulan == 1) {
            bulan1 = "Januari";
        } else if (bulan == 2) {
            bulan1 = "Februari";
        } else if (bulan == 3) {
            bulan1 = "Maret";
        } else if (bulan == 4) {
            bulan1 = "April";
        } else if (bulan == 5) {
            bulan1 = "Mei";
        } else if (bulan == 6) {
            bulan1 = "Juni";
        } else if (bulan == 7) {
            bulan1 = "Juli";
        } else if (bulan == 8) {
            bulan1 = "Agustus";
        } else if (bulan == 9) {
            bulan1 = "September";
        } else if (bulan == 10) {
            bulan1 = "Oktober";
        } else if (bulan == 11) {
            bulan1 = "November";
        } else {
            bulan1 = "Desember";
        }

        tahun = calendar.get(Calendar.YEAR);
        String tanggalFix = hari + " " + bulan1 + " " + tahun;

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Home.this, MainActivity.class));
            }
        });
        bt_ganti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        presensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,Camera.class);
                startActivity(intent);
            }
        });
        rekap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, rekap.class);
                startActivity(intent);
            }
        });
        textView2 = (TextView) findViewById(R.id.tanggal);
        textView2.setText(tanggalFix);
    }
    private void getUserName(DocumentReference reference){
    //    namaAkun.setText(userID);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nama = documentSnapshot.getString("nama");
                if(nama == null){
                    return;
                } else {
                    namaAkun.setText(nama);
                }
            }
        });
        tentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, tentangapp.class);
                startActivity(intent);
            }
        });
    }








    /*public Bitmap addTextToImage(Bitmap src, String textToAddOnImage, int x, int y, int color, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(textToAddOnImage, x, y, paint);

        return result;
    }*/

    //Bitmap bmp = addTextToImage(Bitmap.createBitmap(), "Mustanser Iqbal", 200, 200, Color.GREEN, 80, 24, false);

   // File f = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + fileName + ".png");
   // FileOutputStream fos = new FileOutputStream(f);
//bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
}



