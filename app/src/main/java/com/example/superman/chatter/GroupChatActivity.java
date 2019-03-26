package com.example.superman.chatter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class GroupChatActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ScrollView chatScrollView;
    private ImageButton sendImageButton;
    private TextView groupTextView;
    private EditText groupEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference nameRef,groupRef,groupMessageKeyRef;
    private String GroupName,currentUserId,userName,currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_group_chat);
        initializeField();
        getCurrentUser();
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveMessageToDatabase();
                groupEditText.setText("");
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        groupRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    getMessageOnScreen(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    getMessageOnScreen(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.group_chat_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if((item.getItemId())==R.id.group_setting)
        {
            goToGroupSettingActivity();
        }
        return true;
    }

    private void goToGroupSettingActivity()
    {
        Intent intent=new Intent(GroupChatActivity.this,GroupChatSettingsActivity.class);
        intent.putExtra("groupname",GroupName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initializeField()
    {
        mToolbar=findViewById(R.id.group_chat_Toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(GroupName);
        sendImageButton=findViewById(R.id.group_chat_send_button);
        chatScrollView=findViewById(R.id.group_chat_scroll_view);
        groupEditText=findViewById(R.id.Group_chat_edit_text);
        groupTextView=findViewById(R.id.group_chat_text_view);
        GroupName=getIntent().getExtras().get("currentGroupName").toString();
        Toast.makeText(this, GroupName, Toast.LENGTH_SHORT).show();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        nameRef=FirebaseDatabase.getInstance().getReference().child("user");
        groupRef=FirebaseDatabase.getInstance().getReference().child("groups").child(GroupName);
    }
    private void getCurrentUser()
    {
        nameRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
             if (dataSnapshot.exists())
             {
                 userName=dataSnapshot.child("name").getValue().toString();
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void saveMessageToDatabase()
    {
      String message=groupEditText.getText().toString();
      String messageKey=groupRef.push().getKey();
      if (TextUtils.isEmpty(message))
      {

      }
      else
      {

          Calendar callforTime=Calendar.getInstance();
          SimpleDateFormat TimeFormat=new SimpleDateFormat("hh:mm");
          currentTime=TimeFormat.format(callforTime.getTime());
          HashMap<String,Object> hashMap=new HashMap<>();
          groupRef.updateChildren(hashMap);
          groupMessageKeyRef=groupRef.child(messageKey);
          HashMap<String,Object> updateGroupMessage=new HashMap<>();
          updateGroupMessage.put("name",userName);
          updateGroupMessage.put("message",message);
          updateGroupMessage.put("time",currentTime);
          groupMessageKeyRef.updateChildren(updateGroupMessage);

      }
    }
    private void getMessageOnScreen(DataSnapshot dataSnapshot)
    {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            String message=(String)((DataSnapshot)iterator.next()).getValue();
            String name=(String)((DataSnapshot)iterator.next()).getValue();
            String time=(String)((DataSnapshot)iterator.next()).getValue();
            groupTextView.append(name+"\n"+message+"\n"+time+"\n\n");
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }

}
