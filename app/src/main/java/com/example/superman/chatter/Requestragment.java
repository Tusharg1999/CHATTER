package com.example.superman.chatter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Requestragment extends Fragment {
    private View view;
    private RecyclerView mrecyclerview;
    private DatabaseReference requestref,userref;
    private FirebaseAuth mauth;
    private String currentuserid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
     view = inflater.inflate(R.layout.fragment_requestragment, container, false);
     init();
     return view;
    }

    private void init() {
        mrecyclerview=view.findViewById(R.id.request_rv);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mauth=FirebaseAuth.getInstance();
        currentuserid=mauth.getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("user");
        requestref= FirebaseDatabase.getInstance().getReference().child("chat requests").child(currentuserid);
    }

    @Override
    public void onStart() {


        FirebaseRecyclerOptions<GetContacts> options = new FirebaseRecyclerOptions.Builder<GetContacts>()
                .setQuery(requestref, GetContacts.class).build();
        FirebaseRecyclerAdapter<GetContacts, ViewHolder> adapter = new FirebaseRecyclerAdapter<GetContacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull GetContacts model) {
                String userid = getRef(position).getKey();
                userref.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image") && dataSnapshot.hasChild("name")) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            holder.nameText.setText(name);
                            holder.statusText.setText(status);
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageView);
                        } else if (dataSnapshot.hasChild("name")) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            holder.nameText.setText(name);
                            holder.statusText.setText(status);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_custom_search, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }

        };
        super.onStart();
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

