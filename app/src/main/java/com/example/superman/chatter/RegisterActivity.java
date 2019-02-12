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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private EditText email,password;
    private Button RegisterButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        rootReference = FirebaseDatabase.getInstance().getReference();
        initializeFields();
        mAuth=FirebaseAuth.getInstance();
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUserAccount();
            }
        });

    }

    private void createNewUserAccount() {
        String userEmail=email.getText().toString();
        String userPassword=password.getText().toString();
        if (TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(RegisterActivity.this,"Please enter an Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userPassword))
        {
            Toast.makeText(RegisterActivity.this,"Please enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait while We Are Creating Your Account...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {   String userId=mAuth.getCurrentUser().getUid();
                        rootReference.child("user").child(userId).setValue("");
                        Toast.makeText(RegisterActivity.this,"Your Account is Succesfully Created",Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        String error=task.getException().toString();
                        Toast.makeText(RegisterActivity.this,error,Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }


                }
            });
        }

    }

    private void initializeFields() {
        email=findViewById(R.id.edit_text_email_signup);
        password=findViewById(R.id.edit_text_password_signup);
        RegisterButton=findViewById(R.id.submit_account_info);
        progressDialog=new ProgressDialog(this);

    }

    public void goToSignInActivity(View view) {
        Intent signIntent=new Intent(RegisterActivity.this,SignInActivity.class);
        startActivity(signIntent);
    }

    public void goToSignInRegisterFromPhoneActivity(View view)
    {
        Intent intent=new Intent(RegisterActivity.this,RegisterFromPhoneActivity.class);
        startActivity(intent);
    }
}
