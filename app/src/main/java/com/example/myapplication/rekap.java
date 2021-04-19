package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class rekap extends AppCompatActivity {

    private TextView balik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        balik = (TextView) findViewById(R.id.balik);

        balik.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                Intent intent=new Intent(rekap.this,Home.class);
                startActivity(intent);
            }
        });

    }
}
