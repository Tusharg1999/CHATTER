package com.example.superman.chatter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatSettingsActivity extends AppCompatActivity {
    private CircleImageView groupimage;
    private EditText groupname,groupdescription;
    private Button submitbutton;
    private FirebaseAuth mauth;
    private StorageReference mstorageref;
    private DatabaseReference mref;
    private String currentgroupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_settings);
        currentgroupname = getIntent().getExtras().get("groupname").toString();
        initializeField();
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             String name=groupname.getText().toString();
             String description=groupdescription.getText().toString();
             if (TextUtils.isEmpty(description )&&TextUtils.isEmpty(name))
             {

             }
             else
             {
                 HashMap<String,String> hashMap=new HashMap<>();
                 hashMap.put("description",description);
             mref.child(currentgroupname).child("group info").setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(GroupChatSettingsActivity.this, "Sucessfuly update", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                       String error =task.getException().toString();
                        Toast.makeText(GroupChatSettingsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                 }
             });
             }
            }
        });
    }



    private void initializeField()
    {
        groupname=findViewById(R.id.group_settings_name);
        groupimage=findViewById(R.id.group_image);
        groupdescription=findViewById(R.id.group_discription);
        submitbutton=findViewById(R.id.group_setting_submit_button);
        mauth=FirebaseAuth.getInstance();
        mref= FirebaseDatabase.getInstance().getReference().child("groups");
    }
}
