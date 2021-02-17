package com.kg.prochat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.kg.prochat.Adapter.ChatAdapter;
import com.kg.prochat.Model.Message;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private static final int NUMBER_OF_THREADS = 2;
    public static final ExecutorService Executor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    AlertDialog.Builder builder;
    Button buttonSend;
    String tempMes;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    EditText text_message;
    RecyclerView recyclerView;
    String senderId,receiverId,senderLang,receiverLang,key,receiverName,senderName;
    ChatAdapter adapter;
    List<Message> messageList=new ArrayList<>();
    TranslatorOptions options;
    Translator translator;
    DownloadConditions conditions;
    static String eng="ENGLISH";
    static String ger="GERMAN";
    static String hin="HINDI";
    static String fre="FRENCH";
    TextView user_name,user_status;
    ImageView user_image;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Bundle bundle=getIntent().getExtras();
        receiverName=bundle.getString("receiverName");
        senderId=bundle.getString("senderId");
        receiverId=bundle.getString("receiverId");
        senderLang=bundle.getString("senderLang");
        receiverLang=bundle.getString("receiverLang");
        key=bundle.getString("key");
        senderName=bundle.getString("name");
        buttonSend=findViewById(R.id.button_send);
        text_message=findViewById(R.id.message);
        recyclerView=findViewById(R.id.recyclerView);
        toolbar=findViewById(R.id.toolbar);
        userId=FirebaseAuth.getInstance().getUid();
        user_name=findViewById(R.id.user_profileName);
        user_image=findViewById(R.id.user_profileImage);
        user_status=findViewById(R.id.user_profileStatus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth=FirebaseAuth.getInstance();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users").child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user=snapshot.getValue(User.class);
                    Picasso.with(ChatActivity.this).load(user.getImageUri()).into(user_image);
                    user_name.setText(user.getName());
                    user_status.setText(user.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        downloadTranslator();

        getSupportActionBar().setTitle(receiverName);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter=new ChatAdapter(ChatActivity.this,mAuth.getUid());
        setMessages();
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!text_message.getText().toString().isEmpty())
                {

                    Executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            options= new TranslatorOptions.Builder()
                                    .setSourceLanguage(getLanguage(senderLang))
                                    .setTargetLanguage(getLanguage(receiverLang))
                                    .build();
                            translator =Translation.getClient(options);
                            conditions = new DownloadConditions.Builder()
                                    .requireWifi()
                                    .build();
                            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    translator.translate(text_message.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {

                                            //tempMes=s;
                                            Log.d("kitty", "onSuccess: "+" "+s);
                                            Date c = Calendar.getInstance().getTime();


                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                            String formattedDate = df.format(c);
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            String time = sdf.format(Calendar.getInstance().getTime());
                                            Message message=new Message(text_message.getText().toString().trim(),s,senderId,receiverId,formattedDate,time,senderLang,senderName,false);
                                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("chat").child(key).push();
                                            reference.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // adapter.notifyDataSetChanged();

                                                    text_message.setText("");
                                                    text_message.setHint("Enter your message");
                                                }
                                            });


                                        }
                                    });

                                }
                            });



                                }
                            });





                }
            }
        });










    }




    private void downloadTranslator()
    {
        Executor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

    }




    private void setMessages()
    {
        builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setView(R.layout.progress);
        final Dialog dialog = builder.create();

        dialog.show();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("chat").child(key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();


                if(snapshot.exists())
                {

                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Message message=dataSnapshot.getValue(Message.class);

                        messageList.add(message);

                        if(!message.getCheck() && message.getReceiver().equals(mAuth.getUid()))
                        {

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("check",true);
                            dataSnapshot.getRef().updateChildren(hashMap);


                        }

                    }

                }
                adapter.setMessageList(messageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setAdapter(adapter);
        dialog.dismiss();

    }

    public  String getLanguage(String lang)
    {
        if(lang.equals(eng))
        {
            return "en";
        }
        else if(lang.equals(ger))
        {
            return "de";
        }
        else  if(lang.equals(hin))
        {
            return "hi";
        }
        else if(lang.equals(fre))
        {
            return "fr";
        }
        else
        {
            return "en";
        }

    }

    private void updateStatus(String status)
    {

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        databaseReference.updateChildren(hashMap);





    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("Active Now");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Date c = Calendar.getInstance().getTime();


        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(Calendar.getInstance().getTime());
        String status="Last Active at "+time+" on "+formattedDate;
        updateStatus(status);

    }

}