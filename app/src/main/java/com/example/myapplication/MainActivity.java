package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView gntPass;
    private EditText et_email, et_Pass;
    private Button bt_Login;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

       et_email = (EditText)findViewById(R.id.et_email);
        et_Pass = (EditText)findViewById(R.id.et_Pass);
        bt_Login = findViewById(R.id.bt_Login);
        progressDialog = new ProgressDialog(this);
        gntPass = (TextView)findViewById(R.id.gntPass);

        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, Home.class));
        }


        bt_Login.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validasi(et_email.getText().toString(),et_Pass.getText().toString());


            }

        }));
    }

    private void validasi(String userName, String userpassword) {
        progressDialog.setMessage("Mohon tunggu");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, Home.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                }

            }
        });

        gntPass=(TextView)findViewById(R.id.gntPass);
        gntPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);

            }
        });
    }
}
