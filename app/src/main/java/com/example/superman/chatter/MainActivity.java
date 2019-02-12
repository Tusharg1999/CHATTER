package com.example.superman.chatter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Toolbar mtoolbar;
    ViewPager myViewpager;
    TabLayout myTablayout;
    private tabAccessAdapter tabAccessAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private FirebaseAuth.AuthStateListener mAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFields();
        actionBar();
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    sendUserToLoginActivity();
                } else {
                    checkForCurrentUser();
                }
            }
        };


    }

    @Override

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);

    }


    private void checkForCurrentUser() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        userReference.child("user")
                .child(currentUserId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void actionBar() {
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chatter");
        tabAccessAdapter = new tabAccessAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(tabAccessAdapter);
        myTablayout.setupWithViewPager(myViewpager);

    }

    private void initializeFields() {
        mtoolbar = findViewById(R.id.main_activity_toolbar);
        myViewpager = findViewById(R.id.main_activity_viewPager);
        myTablayout = findViewById(R.id.main_activity_Tab);
    }


    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if ((item.getItemId() == R.id.settings)) {
            sendUserToSettingsActivity();
        }
        if ((item.getItemId()) == R.id.create_group) {
            requestGroup();
        }
        return true;
    }

    private void requestGroup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        alertDialog.setTitle("Enter Name Of your Group");
        final EditText alertEditText = new EditText(MainActivity.this);
        alertEditText.setHint("Group Name");
        alertDialog.setView(alertEditText);
        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = alertEditText.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Please Enter a group Name...", Toast.LENGTH_SHORT).show();
                } else {
                    createGroup(groupName);
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void createGroup(String groupname) {
        userReference.child("groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(MainActivity.this, "Group creation is Sucessfully Done...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);

    }
}