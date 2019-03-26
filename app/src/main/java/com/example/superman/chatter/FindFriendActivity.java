package com.example.superman.chatter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class FindFriendActivity extends AppCompatActivity {
    private RecyclerView findFriendsRV;
    private Toolbar mtoolbar;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        initializeFields();
    }

    private void initializeFields()
    { findFriendsRV=findViewById(R.id.find_friends_recycler_view);
      findFriendsRV.setLayoutManager(new LinearLayoutManager(this));
      mtoolbar=findViewById(R.id.find_friends_bar);
      setSupportActionBar(mtoolbar);
      getSupportActionBar().setTitle("Find Friends");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      mReference= FirebaseDatabase.getInstance().getReference().child("user");
    }

    @Override
    protected void onStart() {
        super.onStart();  
        FirebaseRecyclerOptions<GetContacts> options=new FirebaseRecyclerOptions.Builder<GetContacts>().setQuery(mReference,GetContacts.class)
                .build();
        FirebaseRecyclerAdapter<GetContacts,ViewGroupHolder> adapter=new FirebaseRecyclerAdapter<GetContacts, ViewGroupHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewGroupHolder holder, final int position, @NonNull GetContacts model)
            {
                holder.nameText.setText(model.getName());
                holder.statusText.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.imageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selected_user_id=getRef(position).getKey();
                        Intent intent=new Intent(FindFriendActivity.this,ProfileActivity.class);
                        intent.putExtra("selecteduserid",selected_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ViewGroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_custom_search,viewGroup, false);
                ViewGroupHolder viewholder=new ViewGroupHolder(view);
                return viewholder;
            }
        };
        findFriendsRV.setAdapter(adapter);
        adapter.startListening();

    }
    public static class ViewGroupHolder extends RecyclerView.ViewHolder
    {
        TextView nameText,statusText;
        ImageView imageView;

        public ViewGroupHolder(@NonNull View itemView) {
            super(itemView);
            nameText=itemView.findViewById(R.id.name);
            statusText=itemView.findViewById(R.id.status2);
            imageView=itemView.findViewById(R.id.profile_image);
        }
    }
}
