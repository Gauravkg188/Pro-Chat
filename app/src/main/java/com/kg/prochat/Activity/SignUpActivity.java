package com.kg.prochat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;


public class SignUpActivity extends AppCompatActivity {

    EditText input_email,input_name,input_password;
    ImageView user_image;
    Button button;
    String email,name,password,language;
    FirebaseAuth mAuth;
    Uri imageUri;
    private static int Code = 1 ;
    private static int REQUESTCODE = 1 ;
    FirebaseDatabase user_Database;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        input_email=findViewById(R.id.input_email);
        input_password=findViewById(R.id.input_password);
        input_name=findViewById(R.id.input_name);
        user_image=findViewById(R.id.user_image);
        button=findViewById(R.id.login);
        spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.language,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mAuth=FirebaseAuth.getInstance();

        language="English";
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>=23)
                {
                    checkPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                language=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = input_email.getText().toString().trim();
                password = input_password.getText().toString().trim();
                name = input_name.getText().toString().trim();

                if(!name.isEmpty() && !password.isEmpty() && !email.isEmpty() && user_image.getDrawable() != null)
                {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        updateUserInfo(name, imageUri, mAuth.getCurrentUser());



                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();

                                    }

                                }

                            })
                            .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else {

                    Toast.makeText(SignUpActivity.this,"Enter data properly",Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(SignUpActivity.this,"Please accept the required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Code);
            }


            if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){

                openGallery();
            }


        }
        else
            openGallery();

    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null ) {

            imageUri = data.getData() ;
            user_image.setImageURI(imageUri);


        }


    }



    private void updateUserInfo(final String name, Uri Uri, final FirebaseUser currentUser) {


        //final boolean result = false;
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");


        final StorageReference imageFilePath = mStorage.child(Uri.getLastPathSegment());
        imageFilePath.putFile(Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {


                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                                            final String user_id=current_user.getUid();
                                            user_Database=FirebaseDatabase.getInstance();
                                            final DatabaseReference user_Reference=user_Database.getReference().child("users").child(user_id);

                                            String image=uri.toString();
                                            User userModel=new User(user_id,image,language,current_user.getDisplayName());

                                            user_Reference.setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    Toast.makeText(SignUpActivity.this, "successfully Signed", Toast.LENGTH_SHORT).show();
                                                    //Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                                                    //startActivity(intent);
                                                    finish();
                                                }
                                            });




                                        }

                                    }
                                })
                                .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                })
                        ;

                    }
                });





            }
        });






    }




}