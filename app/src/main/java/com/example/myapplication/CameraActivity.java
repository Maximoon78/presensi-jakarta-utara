package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int CAMERA_REQUEST = 1888;
    String currentPhotoPath;
    String uID;
    private ImageView fimageView;
    private Button kirim, kembali;
    private TextView textViewLokasiDanWaktu;
    private Bitmap photo;
    private Uri aplot;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        fimageView = findViewById(R.id.imageViewhasil);
        kirim = findViewById(R.id.bt_kirim);
        kembali = findViewById(R.id.bt_kembali);
        textViewLokasiDanWaktu = findViewById(R.id.textViewLokasiDanWaktu);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
//            Toast.makeText(this, "Permission Location not Granted", Toast.LENGTH_LONG).show();
        }

        fimageView.setOnClickListener(this);

        if (aplot == null) {
            kirim.setOnClickListener(v -> {
                Toast.makeText(this, "Silahkan ambil foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            });
            kembali.setOnClickListener(v -> {
                Toast.makeText(this, "Silahkan ambil foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private void savePhoto() {
        textViewLokasiDanWaktu.setDrawingCacheEnabled(true);
        Bitmap bmpLokasiWaktu = Bitmap.createBitmap(textViewLokasiDanWaktu.getDrawingCache());
        Bitmap combined = combineImages(photo, bmpLokasiWaktu);
        saveImage(combined);
    }

    public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        int width = 0, height = 0;
        Bitmap cs;

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(foreground, comboImage.getWidth() - foreground.getWidth() - 16, comboImage.getHeight() - foreground.getHeight() - 16, null);

        return cs;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            assert data != null;

            photo = (Bitmap) data.getExtras().get("data");
            fimageView.setImageBitmap(photo);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
            aplot = Uri.parse(path);

            Log.d("Upload process", "onActivityResult: " + aplot);
            kirim.setOnClickListener(this);
            kembali.setOnClickListener(this);
        }
    }

    private void Uploader() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Mohon tunggu");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                //get user data

                if (user != null) {
                    //photo storage location
                    StorageReference riversRef = storageReference.child("Absen/" + user.getNama() + "/" + randomKey);

                    //uploading photos to firebase storage
                    riversRef.putFile(aplot)
                            .addOnSuccessListener(taskSnapshot -> {
                                long currentTimeEpoch = System.currentTimeMillis();
                                int currentTimeHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                int startDayAbsensiHours = Utility.ConvertEpochToCalendar(Utility.startAbsenPagi).get(Calendar.HOUR_OF_DAY);
                                int endDayAbsensiHours = Utility.ConvertEpochToCalendar(Utility.endAbsenPagi).get(Calendar.HOUR_OF_DAY);
                                int startNoonAbsensiHours = Utility.ConvertEpochToCalendar(Utility.startAbsenSore).get(Calendar.HOUR_OF_DAY);
                                int endNoonAbsensiHours = Utility.ConvertEpochToCalendar(Utility.endAbsenSore).get(Calendar.HOUR_OF_DAY);

                                // TODO: 4/24/2021 REMOVE LOGGING
                                //if successful
                                Log.d("uploader", "onDataChange: Upload successfull!");
                                Log.d("uploader", "onDataChange: " + currentTimeEpoch);
                                Log.d("JAM", "onDataChange: " + Utility.startAbsenSore);

                                //input data into absensi database
                                //------------------------------

                                if ((currentTimeHours >= startDayAbsensiHours & currentTimeHours <= endDayAbsensiHours)) {
                                    // TODO: 4/24/2021 REMOVE LOGGING
                                    //absen pagi
                                    Log.d("uploader", "onDataChange: absen pagi");
                                    Log.d("absen pagi", "current time: " + currentTimeHours);
                                    Log.d("absen pagi", "startday: " + startDayAbsensiHours);
                                    Log.d("absen pagi", "endday: " + endDayAbsensiHours);
                                    Log.d("absen pagi", "1st condition: " + (currentTimeHours > startDayAbsensiHours));
                                    Log.d("absen pagi", "2nd condition: " + (currentTimeHours < endDayAbsensiHours));
                                    inputDataAbsen(currentTimeEpoch, uID, Utility.startAbsenPagi, riversRef);
                                } else if ((currentTimeHours >= startNoonAbsensiHours & currentTimeHours <= endNoonAbsensiHours)) {
                                    // TODO: 4/24/2021 REMOVE LOGGING
                                    //absen sore
                                    Log.d("uploader", "onDataChange: absen sore");
                                    Log.d("absen sore", "current time: " + currentTimeHours);
                                    Log.d("absen sore", "startday: " + startNoonAbsensiHours);
                                    Log.d("absen sore", "endday: " + startNoonAbsensiHours);
                                    Log.d("absen sore", "1st condition: " + (currentTimeHours > startNoonAbsensiHours));
                                    Log.d("absen sore", "2nd condition: " + (currentTimeHours < startNoonAbsensiHours));
                                    inputDataAbsen(currentTimeEpoch, uID, Utility.startAbsenSore, riversRef);
                                }
                                savePhoto();
                                pd.dismiss();
                                Toast.makeText(CameraActivity.this, "Foto berhasil di upload", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CameraActivity.this, HomeActivity.class));
                            })
                            .addOnFailureListener(exception -> {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(getApplicationContext(), "Upload gagal", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inputDataAbsen(Long waktuSaatIni, String uID, Long JenisWaktuAbsen, StorageReference storageReference) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //step 1: get time in milis (epoch), check whether the time is current day or not
        databaseReference.child("absensi").child(String.valueOf(JenisWaktuAbsen)).child(uID).child("Waktu").setValue(waktuSaatIni);

        //step 2: get current location and put into absensi
        databaseReference.child("absensi").child(String.valueOf(JenisWaktuAbsen)).child(uID).child("Lokasi").setValue(textViewLokasiDanWaktu.getText().toString().substring(0, textViewLokasiDanWaktu.getText().toString().indexOf("\n")));

        //step 3: get file link and send into firebase
        storageReference.getDownloadUrl().addOnSuccessListener(taskSnapshots -> {
            Log.d("Uploader", "onDataChange: Photo link after uploading" + taskSnapshots.toString());
            databaseReference.child("absensi").child(String.valueOf(JenisWaktuAbsen)).child(uID).child("Foto").setValue(taskSnapshots.toString());
        });

        //step 4: for recap ease, put current end of the day into user database
        databaseReference.child("user").child(uID).child("absensi").child(String.valueOf(JenisWaktuAbsen)).child("WaktuAbsen").setValue(waktuSaatIni);
        databaseReference.child("user").child(uID).child("absensi").child(String.valueOf(JenisWaktuAbsen)).child("HariAbsen").setValue(Utility.startAbsenPagi);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveImage(Bitmap finalBitmap) {
        String root = getApplicationContext().getExternalFilesDir(null).toString();
        File myDir = new File(root);
        String fname = UUID.randomUUID() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCurrentLocation() {

        final LocationManager locationManager = (LocationManager) CameraActivity.this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    String locationTime = getCityAndProvince(location) + "\n" + getCurrentTime();
                    textViewLokasiDanWaktu.setText(locationTime);
                    getCurrentTime();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CameraActivity.this);
                alertDialogBuilder.setTitle("GPS mati");
                alertDialogBuilder
                        .setMessage("Apakah ingin menghidupkan GPS?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };

        if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 50, locationListener);
    }

    private String getCityAndProvince(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        String address = null;
        geocoder = new Geocoder(CameraActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getLocality();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_kirim:
                Uploader();
                break;
            case R.id.bt_kembali:
                startActivity(new Intent(CameraActivity.this, CameraActivity.class));
                break;
            case R.id.imageViewhasil:
                takePhoto();
                break;
        }
    }


    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date tomorrow = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(tomorrow);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        getCurrentLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}

