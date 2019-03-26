package com.example.superman.chatter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String selected_user_id,senderuserid,currentstate;
    private TextView username,userstatus;
    private Button sendfriendrequst,declinefriendrequest;
    private CircleImageView userimage;
    private DatabaseReference mref,chatrequestreference,contactref;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        selected_user_id=getIntent().getExtras().get("selecteduserid").toString();
        initializeField();
        retrieveData();
        manageRequest();
    }
    private void manageRequest()
    {
        if (!selected_user_id.equals(senderuserid))
        {
            sendfriendrequst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    sendfriendrequst.setEnabled(false);


                    if(currentstate.equals("new"))
                    { sendFriendRequest();
                    }
                    else if(currentstate.equals("request_sent"))
                    {
                        cancelFriendRequest();
                    }
                    else if(currentstate.equals("request_received"))
                    {
                        acceptFriendRequest();
                    }
                    else if(currentstate.equals("friend"))
                    {
                        removeFriend();
                    }
                }
            });
        }
        else
        {
            sendfriendrequst.setVisibility(View.INVISIBLE);
        }
    }



    private void retrieveData()
    {
        mref.child(selected_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
             if(dataSnapshot.exists()&&dataSnapshot.hasChild("image"))
             {
              String image=dataSnapshot.child("image").getValue().toString();
              String name=dataSnapshot.child("name").getValue().toString();
              String Status=dataSnapshot.child("status").getValue().toString();
                 Picasso.get().load(image).into(userimage);
              username.setText(name);
              userstatus.setText(Status);
              manageRequest();
             }
             else
             {
                 String name=dataSnapshot.child("name").getValue().toString();
                 String Status=dataSnapshot.child("status").getValue().toString();
                 username.setText(name);
                 userstatus.setText(Status);
                 manageRequest();
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        chatrequestreference.child(senderuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(selected_user_id))
                {
                    String flag=dataSnapshot.child(selected_user_id).child("request_type").getValue().toString();
                    if (flag.equals("sent"))
                    { currentstate="request_sent";
                        sendfriendrequst.setText("Cancel Request");
                    }
                    else if(flag.equals("received"))
                    {   currentstate="request_received";
                        sendfriendrequst.setText("Accept Request");
                        declinefriendrequest.setVisibility(View.VISIBLE);
                        declinefriendrequest.setEnabled(true);
                        declinefriendrequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                              cancelFriendRequest();
                              sendfriendrequst.setText("Send Friend Request");
                              declinefriendrequest.setVisibility(View.INVISIBLE);
                            }
                        });

                    }

                }
                contactref.child(senderuserid).child(selected_user_id).child(selected_user_id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
                                    String flag=dataSnapshot.getValue().toString();
                                    if (flag.equals("saved"))
                                    {
                                        sendfriendrequst.setText("Remove this friend");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        contactref.child(senderuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(selected_user_id))
                {
                    String flag=dataSnapshot.child(selected_user_id).getValue().toString();
                    if (flag.equals("saved"))
                    {
                        sendfriendrequst.setText("Remove this Friend");
                        sendfriendrequst.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeField()
    {
        username=findViewById(R.id.user_name);
        userstatus=findViewById(R.id.user_Status);
        sendfriendrequst=findViewById(R.id.send_request);
        declinefriendrequest=findViewById(R.id.cancel_request);
        userimage=findViewById(R.id.profile_pic);
        mref= FirebaseDatabase.getInstance().getReference().child("user");
        chatrequestreference= FirebaseDatabase.getInstance().getReference().child("chat requests");
        mauth=FirebaseAuth.getInstance();
        senderuserid=mauth.getUid();
        currentstate="new";
        contactref=FirebaseDatabase.getInstance().getReference().child("contacts");

    }




    private void sendFriendRequest()
    {
        chatrequestreference.child(senderuserid).child(selected_user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                 chatrequestreference.child(selected_user_id).child(senderuserid).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful())
                         {
                             sendfriendrequst.setEnabled(true);
                          currentstate="request_sent";
                          sendfriendrequst.setText("Cancel Request");
                          sendfriendrequst.setVisibility(View.VISIBLE);
                         }
                     }
                 });
                }


            }
        });
    }
    private void cancelFriendRequest()
    {
        chatrequestreference.child(senderuserid).child(selected_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful()) {
                    chatrequestreference.child(selected_user_id).child(senderuserid).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        sendfriendrequst.setEnabled(true);
                                        currentstate="new";
                                        sendfriendrequst.setText("SEND FRIEND REQUEST");
                                        sendfriendrequst.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }
    private void acceptFriendRequest()
    {
       contactref.child(senderuserid).child(selected_user_id).child(selected_user_id).setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task)
           {
             if (task.isSuccessful())
             {
                 contactref.child(selected_user_id).child(senderuserid).child(senderuserid).setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task)
                     {
                      chatrequestreference.child(senderuserid).child(selected_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task)
                          {
                            if (task.isSuccessful())
                            {
                                chatrequestreference.child(selected_user_id).child(senderuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            sendfriendrequst.setText("Remove this friend");
                                            sendfriendrequst.setEnabled(true);
                                            currentstate="friend";
                                            declinefriendrequest.setVisibility(View.INVISIBLE);
                                        }

                                    }
                                });
                            }
                          }
                      });
                     }
                 });

             }
           }
       });
    }
    private void removeFriend()
    {
        contactref.child(senderuserid).child(selected_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    contactref.child(selected_user_id).child(senderuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                         if (task.isSuccessful())
                         { currentstate="new";sendfriendrequst.setText("Send Friend Request");sendfriendrequst.setEnabled(true); }
                        }
                    });
                }
            }
        });
    }
}