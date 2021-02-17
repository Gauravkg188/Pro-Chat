package com.kg.prochat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kg.prochat.Activity.ChatActivity;
import com.kg.prochat.Adapter.UsersAdapter;
import com.kg.prochat.Model.Chat;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {


    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private List<User> userList;
    User receiver;
    User sender;
    FirebaseAuth mAuth;
    String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_users,container,false);
        recyclerView=view.findViewById(R.id.users_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        userList=new ArrayList<>();

        adapter=new UsersAdapter(getContext());
        recyclerView.setAdapter(adapter);
        getUsers();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference databaseReference=reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    sender=snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(User user) {

                receiver=user;

                final DatabaseReference dbref=FirebaseDatabase.getInstance().getReference().child("Message");
                final Chat chat=new Chat(sender.getUserId(),receiver.getUserId(),sender.getName()+receiver.getName());


                dbref.child(sender.getUserId()).child(receiver.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                        {
                            dbref.child(sender.getUserId()).child(receiver.getUserId()).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    dbref.child(receiver.getUserId()).child(sender.getUserId()).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            key=sender.getName()+receiver.getName();

                                        }
                                    });
                                }
                            });
                        }
                        else
                        {

                            Chat c=snapshot.getValue(Chat.class);
                            key=c.getKey();


                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intent=new Intent(getContext(), ChatActivity.class);

                intent.putExtra("receiverName",receiver.getName());
                intent.putExtra("senderId",sender.getUserId());
                intent.putExtra("receiverId",receiver.getUserId());
                intent.putExtra("senderLang",sender.getLanguage());
                intent.putExtra("receiverLang",receiver.getLanguage());
                intent.putExtra("key",key);
                intent.putExtra("name",sender.getName());

                if(key!=null){
                    startActivity(intent);}
            }
        });
        return view;
    }


    private void getUsers()
    {

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user=dataSnapshot.getValue(User.class);
                    assert user!=null;
                    if(!(user.getUserId().equals(firebaseUser.getUid())))
                    {
                        userList.add(user);
                        Log.d("kitty"," "+user.getName());
                    }

                }
                adapter.setUserList(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}