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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;


public class RegisterFromPhoneActivity extends AppCompatActivity {
    private EditText userPhoneNumber,verificationCodeEditText;
    private Button verificationButton,verifyButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_from_phone);
        initializeField();
        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              String PhoneNumber=userPhoneNumber.getText().toString();
              if (TextUtils.isEmpty(PhoneNumber))
              {

              }
              else
              {    mProgressDialog.setTitle("Loading");
                  mProgressDialog.setMessage("Please wait while We are Sending verification code...");
                  mProgressDialog.setCanceledOnTouchOutside(false);
                  mProgressDialog.show();
                  PhoneAuthProvider.getInstance().verifyPhoneNumber(
                          PhoneNumber,        // Phone number to verify
                          60,                 // Timeout duration
                          TimeUnit.SECONDS,   // Unit of timeout
                          RegisterFromPhoneActivity.this,               // Activity (for callback binding)
                          mCallbacks);        // OnVerificationStateChangedCallbacks

              }
            }
        });
       mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
           @Override
           public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
           {
              createUserAccount(phoneAuthCredential);
           }


           @Override
           public void onVerificationFailed(FirebaseException e)
           {  mProgressDialog.dismiss();
               Toast.makeText(RegisterFromPhoneActivity.this, "Invalid Number Please Check it...", Toast.LENGTH_SHORT).show();
               userPhoneNumber.setVisibility(View.VISIBLE);
               verificationCodeEditText.setVisibility(View.VISIBLE);
               verificationButton.setVisibility(View.VISIBLE);
               verifyButton.setVisibility(View.VISIBLE);
           }
           @Override
           public void onCodeSent(String verificationId,
                                  PhoneAuthProvider.ForceResendingToken token) {
               // The SMS verification code has been sent to the provided phone number, we
               // now need to ask the user to enter the code and then construct a credential
               // by combining the code with a verification ID.

               // Save verification ID and resending token so we can use them later
               mProgressDialog.dismiss();
               mVerificationId = verificationId;
                mResendToken = token;
               userPhoneNumber.setVisibility(View.INVISIBLE);
               verificationCodeEditText.setVisibility(View.VISIBLE);
               verificationButton.setVisibility(View.INVISIBLE);
               verifyButton.setVisibility(View.VISIBLE);

               // ...
           }
       };
       verifyButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
             String verificationcode=verificationCodeEditText.getText().toString();
             if(TextUtils.isEmpty(verificationcode))
             {
                 Toast.makeText(RegisterFromPhoneActivity.this, "You need to Enter your verification code To proceed...", Toast.LENGTH_SHORT).show();
             }
             else
             {   userPhoneNumber.setVisibility(View.INVISIBLE);
                 verificationButton.setVisibility(View.INVISIBLE);
                 mProgressDialog.setTitle("Loading");
                 mProgressDialog.setMessage("Please wait while We are creating your account");
                 mProgressDialog.setCanceledOnTouchOutside(false);
                 mProgressDialog.show();
                 PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                 createUserAccount(credential);
             }
           }
       });

    }


    private void initializeField()
    {
        userPhoneNumber=findViewById(R.id.edit_text_get_number);
        verificationCodeEditText=findViewById(R.id.edit_text_get_password);
        verificationButton=findViewById(R.id.Verification_code_button);
        verifyButton=findViewById(R.id.Verify_button);
        mAuth=FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(this);
    }
    private void createUserAccount(PhoneAuthCredential phoneAuthCredential)
    {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                  mProgressDialog.dismiss();
                    Toast.makeText(RegisterFromPhoneActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    sendToMainActivity();

                }
                else
                {
                    String error=task.getException().toString();
                    Toast.makeText(RegisterFromPhoneActivity.this, error, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void sendToMainActivity()
    {
        Intent intent =new Intent(RegisterFromPhoneActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
