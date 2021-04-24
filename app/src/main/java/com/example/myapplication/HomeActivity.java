package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.myapplication.POJO.DataAbsen;
import com.example.myapplication.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 9999;
    TextView textView2, namaAkun, bt_ganti, bt_logout, tentang, statusAbsensiPagi, statusAbsensiSore;
    FirebaseFirestore firestore;
    int hari, bulan, tahun;
    String bulan1, userID;

    private ImageView fimageView;
    private Button presensi, rekap, bt_Name;
    private CardView cv_Name;
    private EditText et_Name;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        getUserAbsenceStatus(userID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserAbsenceStatus(userID);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //disable back, cuz it will back to camera activity
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        fimageView = findViewById(R.id.imageViewhasil);
        bt_ganti = findViewById(R.id.bt_ganti);
        namaAkun = findViewById(R.id.nama);
        bt_logout = findViewById(R.id.bt_logout);
        presensi = findViewById(R.id.buttonPres);
        tentang = findViewById(R.id.tentang);
        textView2 = findViewById(R.id.tanggal);
        et_Name = findViewById(R.id.inputNewName);
        bt_Name = findViewById(R.id.bt_name);
        cv_Name = findViewById(R.id.updateName);
        rekap = findViewById(R.id.rekap);
        statusAbsensiPagi = findViewById(R.id.status_absen_pagi);
        statusAbsensiSore = findViewById(R.id.status_absen_sore);

        bt_Name.setOnClickListener(v -> {
            if (et_Name.toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Nama Kosong", Toast.LENGTH_SHORT).show();
            } else {
                bt_Name.setEnabled(false);
                et_Name.setEnabled(false);
                Log.d("nama", "onCreate: " + et_Name.getText().toString());
                setUserName(et_Name.getText().toString());
            }
        });

        //get user name
        getUserName();

        //get status
        getUserAbsenceStatus(userID);

        tentang.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TentangAppActivity.class);
            startActivity(intent);
        });

        //can be removed, doesn't do anything
        DocumentReference documentReference = firestore.collection("Karyawan").document(userID);
        getUserName(documentReference);

        Calendar calendar = Calendar.getInstance();
        hari = calendar.get(Calendar.DATE);
        bulan = calendar.get(Calendar.MONTH) + 1;
        bulan1 = Utility.convertMonthToBulan(bulan);
        tahun = calendar.get(Calendar.YEAR);
        String hariAbsen = Utility.convertDayOfWeekToHari(Calendar.DAY_OF_WEEK);

        // TODO: 4/24/2021 remove this, just for debugging purposes
        int jamAbsen = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int menitAbsen = Utility.ConvertEpochToCalendar(System.currentTimeMillis()).get(Calendar.MINUTE);
        String jamAbsenUI = jamAbsen + " : " + menitAbsen;

        // TODO: 4/24/2021 DONT REMOVE THIS, JUST REMOVE THE jamAbsenUI!
        String tanggalFix = hariAbsen + ", " + hari + " " + bulan1 + " " + tahun + " " + jamAbsenUI;

        //clicklistener for logout
        //original codes
        bt_logout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });

        //clicklistener for ganti password
        //original codes
        bt_ganti.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ForgotActivity.class);
            startActivity(intent);
        });

        //clicklistener for rekap
        //original codes
        rekap.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RekapActivity.class);
            startActivity(intent);
        });

        //set text for tanggal
        //original codes
        textView2.setText(tanggalFix);

        //clicklistener for presensi
        presensi.setOnClickListener(v -> controlButtonAbsensi());
    }

    private void controlButtonAbsensi() {
        int currentTimeHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int startDayAbsensiHours = Utility.ConvertEpochToCalendar(Utility.startAbsenPagi).get(Calendar.HOUR_OF_DAY);
        int endDayAbsensiHours = Utility.ConvertEpochToCalendar(Utility.endAbsenPagi).get(Calendar.HOUR_OF_DAY);
        int startNoonAbsensiHours = Utility.ConvertEpochToCalendar(Utility.startAbsenSore).get(Calendar.HOUR_OF_DAY);
        int endNoonAbsensiHours = Utility.ConvertEpochToCalendar(Utility.endAbsenSore).get(Calendar.HOUR_OF_DAY);

        if (!(currentTimeHours >= startNoonAbsensiHours & currentTimeHours <= endNoonAbsensiHours)) {
            // TODO: 4/24/2021 REMOVE LOGGING
            Log.d("control button absensi2", "current time: " + currentTimeHours);
            Log.d("control button absensi2", "startday: " + startNoonAbsensiHours);
            Log.d("control button absensi2", "endday: " + endNoonAbsensiHours);
            Log.d("control button absensi2", "1st condition: " + (currentTimeHours >= startNoonAbsensiHours));
            Log.d("control button absensi2", "2nd condition: " + (currentTimeHours <= startNoonAbsensiHours));
            if (statusAbsensiPagi.getText().equals("Belum Absen Pagi")) {
                if ((currentTimeHours >= startDayAbsensiHours & currentTimeHours <= endDayAbsensiHours)) {
                    Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else if (currentTimeHours < startDayAbsensiHours) {
                    Toast.makeText(getApplicationContext(), "Belum Waktu Absen Pagi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Anda Melewati Absen Pagi", Toast.LENGTH_SHORT).show();
                }
            } else if (statusAbsensiPagi.getText().equals("Sudah Absen Pagi")) {
                Toast.makeText(getApplicationContext(), "Tidak Bisa Absen Lagi", Toast.LENGTH_SHORT).show();
            }
        } else if (!(currentTimeHours >= startDayAbsensiHours & currentTimeHours <= endDayAbsensiHours)) {
            // TODO: 4/24/2021 REMOVE LOGGING
            Log.d("control button absensi", "current time: " + currentTimeHours);
            Log.d("control button absensi", "startday: " + startDayAbsensiHours);
            Log.d("control button absensi", "endday: " + endDayAbsensiHours);
            Log.d("control button absensi", "1st condition: " + (currentTimeHours >= startDayAbsensiHours));
            Log.d("control button absensi", "2nd condition: " + (currentTimeHours <= endDayAbsensiHours));
            if (statusAbsensiSore.getText().equals("Belum Absen Sore")) {
                if ((currentTimeHours >= startNoonAbsensiHours & currentTimeHours <= endNoonAbsensiHours)) {
                    Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else if (currentTimeHours < startNoonAbsensiHours) {
                    Toast.makeText(getApplicationContext(), "Belum Waktu Absen Sore", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Anda Melewati Absen Sore", Toast.LENGTH_SHORT).show();
                }
            } else if (statusAbsensiSore.getText().equals("Sudah Absen Sore")) {
                Toast.makeText(getApplicationContext(), "Tidak Bisa Absen Lagi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserAbsenceStatus(String userID) {
        DatabaseReference absensiDatabaseReference = FirebaseDatabase.getInstance().getReference().child("absensi");

        //get status absen pagi
        absensiDatabaseReference.child(String.valueOf(String.valueOf(Utility.startAbsenPagi))).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataAbsen dataAbsen = snapshot.getValue(DataAbsen.class);
                if (dataAbsen == null) {
                    statusAbsensiPagi.setText("Belum Absen Pagi");
                    statusAbsensiPagi.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.button_red));
                } else {
                    statusAbsensiPagi.setText("Sudah Absen Pagi");
                    statusAbsensiPagi.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.button_green));
                }
                statusAbsensiPagi.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get status absen sore
        absensiDatabaseReference.child(String.valueOf(String.valueOf(Utility.startAbsenSore))).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataAbsen dataAbsen = snapshot.getValue(DataAbsen.class);
                if (dataAbsen == null) {
                    statusAbsensiSore.setText("Belum Absen Sore");
                    statusAbsensiSore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.button_red));
                } else {
                    statusAbsensiSore.setText("Sudah Absen Sore");
                    statusAbsensiSore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.button_green));
                }
                statusAbsensiSore.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserName(String UserName) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        userDatabase.child(userID).child("nama").setValue(UserName).addOnSuccessListener(aVoid -> {
            getUserName();
            Toast.makeText(getApplicationContext(), "Sukses Simpan Nama", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserName() {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    namaAkun.setText("Halo, pengguna baru");
                    presensi.setVisibility(View.GONE);
                    rekap.setVisibility(View.GONE);
                    cv_Name.setVisibility(View.VISIBLE);
                } else {

                    Log.d("nama", "onDataChange: " + user.getNama());
                    namaAkun.setText("Halo, " + user.getNama());
                    presensi.setVisibility(View.VISIBLE);
                    rekap.setVisibility(View.VISIBLE);
                    cv_Name.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: 4/24/2021 this can be removed, doesnt have any purposes
    // see getUserName() function
    private void getUserName(DocumentReference reference) {
        reference.get().addOnSuccessListener(documentSnapshot -> {
            String nama = documentSnapshot.getString("nama");
            if (nama != null) {
                namaAkun.setText(nama);
            }
        });

    }

    //ORIGINAL CODES, I DONT KNOW THE PURPOSES
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



