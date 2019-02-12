package com.example.superman.chatter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {
        private View groupFragmentView;
        private ListView groupList;
        private ArrayAdapter<String> arrayAdapter;
        private ArrayList<String> arrayList=new ArrayList<>();
        private FirebaseAuth mAuth;
        private DatabaseReference currentUserReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView=inflater.inflate(R.layout.fragment_groups, container, false);
        initializeFields();
        reteriveGroup();
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
             String GroupName=parent.getItemAtPosition(position).toString();
                Intent intent=new Intent(getContext(),GroupChatActivity.class);
                intent.putExtra("currentGroupName",GroupName);
                startActivity(intent);
            }
        });

        return groupFragmentView;

    }

    private void reteriveGroup()
    {
        currentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeFields()
    {
        groupList=groupFragmentView.findViewById(R.id.group_list);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        groupList.setAdapter(arrayAdapter);
        mAuth=FirebaseAuth.getInstance();
        currentUserReference= FirebaseDatabase.getInstance().getReference().child("groups");


    }

}
