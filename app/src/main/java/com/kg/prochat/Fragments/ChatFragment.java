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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kg.prochat.Activity.ChatActivity;
import com.kg.prochat.Adapter.ChatUserAdapter;
import com.kg.prochat.Model.Chat;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    User receiver;
    User sender;
    FirebaseAuth mAuth;
    String key;
    RecyclerView recyclerView;
    ChatUserAdapter adapter;
    DatabaseReference reference;
    List<Chat> chatList=new ArrayList<>();
    List<User> userList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView=view.findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter=new ChatUserAdapter(getContext());
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());

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

        reference= FirebaseDatabase.getInstance().getReference().child("Message").child(mAuth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists())
                {

                    chatList.clear();
                    userList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Chat chat=dataSnapshot.getValue(Chat.class);
                        chatList.add(chat);
                        if(!chat.getSender().equals(mAuth.getUid()))
                        {
                            getUserList(chat.getSender());
                        }
                        else
                        {
                            getUserList(chat.getReceiver());
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("kitty", "onCreateView: chatsize"+userList.size());



        adapter.setUserList(userList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChatUserAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(User user) {
                receiver=user;

                final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Message");
                ref.child(sender.getUserId()).child(receiver.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
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


    public void getUserList(String userId)
    {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    User user=snapshot.getValue(User.class);

                    if(!userList.contains(user))
                    {userList.add(user);
                    adapter.notifyDataSetChanged();}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }








}