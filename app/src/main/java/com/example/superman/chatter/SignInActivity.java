package com.example.superman.chatter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.xml.transform.Templates;


public class SignInActivity extends AppCompatActivity {
    private EditText email,password;
    private Button LoginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeFields();
        mAuth=FirebaseAuth.getInstance();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             signInUser();
            }
        });
    }

    private void signInUser() {
        String userEmail=email.getText().toString();
        String userPassword=password.getText().toString();
        if (TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(SignInActivity.this,"Please enter an Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userPassword))
        {
            Toast.makeText(SignInActivity.this,"Please enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        { progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
            mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                        startActivity(intent);
                        progressDialog.cancel();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                    }
                    else {
                        String error=task.getException().toString();
                        Toast.makeText(SignInActivity.this, error, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void initializeFields() {
    email=findViewById(R.id.edit_text_email);
    password=findViewById(R.id.edit_text_password);
    LoginButton=findViewById(R.id.login_account_button);
    progressDialog=new ProgressDialog(this);
    }

    public void goToRegisterActivity(View view) {
        Intent registerIntent=new Intent(SignInActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
