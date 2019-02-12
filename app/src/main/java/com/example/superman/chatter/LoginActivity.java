package com.example.superman.chatter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();



    }

    public void goToSignInActivity(View view) {
        Intent signIntent=new Intent(LoginActivity.this,SignInActivity.class);
        startActivity(signIntent);
    }

    public void goToRegisterActivity(View view) {
        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}