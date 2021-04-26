package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.POJO.Absensi;
import com.example.myapplication.adapter.RekapAbsensiAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RekapActivity extends AppCompatActivity {

    private ImageView bt_balik;
    private RecyclerView recyclerView;
    private RekapAbsensiAdapter rekapAbsensiAdapter;
    private ScrollView scrollView;
    private CardView titleCardRekap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        //initialize UI
        initializeUI();

        //get rekap absensi
        getRekapAbsensi(FirebaseAuth.getInstance().getCurrentUser().getUid());

        bt_balik.setOnClickListener(v -> {
            Intent intent = new Intent(RekapActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void initializeUI() {
        bt_balik = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.rekapAbsensiRecyclerView);
        scrollView = findViewById(R.id.scrollview_rekap);
        titleCardRekap = findViewById(R.id.title_card_rekap);
    }

    private void getRekapAbsensi(String userID) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query rekapAbsensiQuery = FirebaseDatabase.getInstance().getReference().child("user").child(userID).child("absensi");
        FirebaseRecyclerOptions<Absensi> options =
                new FirebaseRecyclerOptions.Builder<Absensi>()
                        .setQuery(rekapAbsensiQuery, Absensi.class)
                        .build();
        rekapAbsensiAdapter = new RekapAbsensiAdapter(options, RekapActivity.this);
        recyclerView.setAdapter(rekapAbsensiAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rekapAbsensiAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        rekapAbsensiAdapter.stopListening();
    }
}
