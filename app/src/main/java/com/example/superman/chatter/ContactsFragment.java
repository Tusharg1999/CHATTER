package com.example.superman.chatter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragment extends Fragment
{   private View contactfragmentview;
    private RecyclerView mrecyclerview;
    private DatabaseReference contactref,userref;
    private FirebaseAuth mAuth;
    private String currentUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactfragmentview=inflater.inflate(R.layout.fragment_contacts, container, false);
        initializeFields();
        return contactfragmentview;
    }

    private void initializeFields()
    {
        mrecyclerview=contactfragmentview.findViewById(R.id.contacts_recycler_view_list);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        contactref=FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUser);
        userref=FirebaseDatabase.getInstance().getReference().child("user");


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<GetContacts>options=new FirebaseRecyclerOptions.Builder<GetContacts>().setQuery(contactref,GetContacts.class)
                .build();
        FirebaseRecyclerAdapter<GetContacts,ViewHolder> adapter=new FirebaseRecyclerAdapter<GetContacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull GetContacts model)
            {
                String userid=getRef(position).getKey();
                userref.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("image")&&dataSnapshot.hasChild("name"))
                        {
                            String image=dataSnapshot.child("image").getValue().toString();
                            String name=dataSnapshot.child("name").getValue().toString();
                            String status=dataSnapshot.child("status").getValue().toString();
                            holder.nameText.setText(name);
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageView);
                            holder.statusText.setText(status);

                        }
                        else if(dataSnapshot.hasChild("name"))
                        { String name=dataSnapshot.child("name").getValue().toString();
                            String status=dataSnapshot.child("status").getValue().toString();
                            holder.nameText.setText(name);
                            holder.statusText.setText(status);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_custom_search,viewGroup,false);
                ViewHolder viewHolder=new ViewHolder(view);
                return viewHolder;
            }
        };
        mrecyclerview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {   private TextView nameText,statusText;
        private CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText=itemView.findViewById(R.id.name);
            statusText=itemView.findViewById(R.id.status2);
            imageView=itemView.findViewById(R.id.profile_image);
        }
    }

}



