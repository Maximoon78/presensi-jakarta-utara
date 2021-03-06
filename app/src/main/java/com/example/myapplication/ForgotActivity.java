package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotActivity extends AppCompatActivity {
    TextView textView2;
    Button btnSimpan;
    EditText et_NewPass, et_NewPass1;
    FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        btnSimpan = findViewById(R.id.btnSimpan);
        et_NewPass = findViewById(R.id.et_NewPass);

        et_NewPass1 = findViewById(R.id.et_NewPass1);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekpass();
            }
        });


        textView2 = (TextView) findViewById(R.id.closee);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotActivity.this, MainActivity.class);
                startActivity(intent);
            }


        });

    }

    private void cekpass() {
        String passlama = et_NewPass.getText().toString();
        String passbaru = et_NewPass1.getText().toString();

        if (TextUtils.isEmpty(passlama)) {
            Toast.makeText(ForgotActivity.this, "Password baru harus diisi", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passbaru)) {
            Toast.makeText(ForgotActivity.this, "Password baru harus diisi", Toast.LENGTH_SHORT).show();
        } else if (!passlama.toString().equals(passbaru)) {
            Toast.makeText(ForgotActivity.this, "tidak cocok", Toast.LENGTH_SHORT).show();
        } else if (passlama.length() < 6) {
            Toast.makeText(ForgotActivity.this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
        } else {
            change();

        }
    }

    public void change() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            progressDialog.setMessage("Mohon tunggu");
            progressDialog.show();


            user.updatePassword(et_NewPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotActivity.this, "Password berhasil diganti", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(ForgotActivity.this, MainActivity.class);
                        startActivity(i);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotActivity.this, "Penggantian password gagal", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}



