package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        et_email = findViewById(R.id.et_email);
        et_Pass = findViewById(R.id.et_Pass);
        bt_Login = findViewById(R.id.bt_Login);
        progressDialog = new ProgressDialog(this);

        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        bt_Login.setOnClickListener(v -> {
            userValidation(et_email, et_Pass);
        });
    }

    private void userValidation(EditText email, EditText password) {
        if (email.getText().toString().equals("")) {
            // failed
            Toast.makeText(getApplicationContext(), "Email Kosong", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().equals("")) {
            // failed
            Toast.makeText(getApplicationContext(), "Password Kosong", Toast.LENGTH_SHORT).show();
        } else {
            bt_Login.setEnabled(false);
            et_email.setEnabled(false);
            et_Pass.setEnabled(false);
            validasi(et_email.getText().toString(), et_Pass.getText().toString());
        }

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
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                    bt_Login.setEnabled(true);
                    et_email.setEnabled(true);
                    et_Pass.setEnabled(true);
                }

            }
        });

    }
}
