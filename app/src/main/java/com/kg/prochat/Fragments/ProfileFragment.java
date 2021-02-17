package com.kg.prochat.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kg.prochat.Activity.SignUpActivity;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ImageButton button_editName,button_editImage;
    ImageView image;
    TextView edit_name;
    User user;
    Uri imageUri;
    private static int Code = 1 ;
    private static int REQUESTCODE = 1 ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
      button_editImage=view.findViewById(R.id.button_newImage);
      button_editName=view.findViewById(R.id.button_newName);
      image=view.findViewById(R.id.userImage);
      edit_name=view.findViewById(R.id.userName);
      edit_name.setEnabled(false);
      mAuth=FirebaseAuth.getInstance();
      mDatabase=FirebaseDatabase.getInstance();

      final AlertDialog.Builder mBuilder=new AlertDialog.Builder(getContext());
      View mView=LayoutInflater.from(getContext()).inflate(R.layout.progress,null);
      mBuilder.setView(mView);
      mBuilder.setCancelable(false);
      final AlertDialog dialog=mBuilder.create();
        dialog.show();
      DatabaseReference reference=mDatabase.getReference().child("users").child(mAuth.getUid());
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists())
              {

                  user=snapshot.getValue(User.class);
                  Picasso.with(getContext()).load(user.getImageUri()).into(image,new com.squareup.picasso.Callback()
                  {
                      @Override
                      public void onSuccess() {

                          if(dialog.isShowing())
                          {
                              dialog.dismiss();
                          }
                      }

                      @Override
                      public void onError() {
                          if(dialog.isShowing())
                          {
                              dialog.dismiss();
                          }
                      }
                  });
                  edit_name.setText(user.getName());

              }


          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });


      button_editImage.setOnClickListener(new View.OnClickListener() {
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

      image.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              AlertDialog.Builder mBuilder = new
                      AlertDialog.Builder(getContext());
              View mView = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, null);
              PhotoView photoView = mView.findViewById(R.id.imageView);
              Picasso.with(getContext()).load(user.getImageUri()).into(photoView);

              mBuilder.setView(mView);
              AlertDialog mDialog = mBuilder.create();
              mDialog.show();
          }
      });

      button_editName.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
           AlertDialog.Builder mBuilder = new
                      AlertDialog.Builder(getContext());
           View mView=LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_name,null);
           final EditText editText=mView.findViewById(R.id.user_newName);

           mBuilder.setCancelable(false)
                   .setView(mView)
                   .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(final DialogInterface dialogInterface, int i) {
                           DatabaseReference databaseReference=mDatabase.getReference().child("users").child(mAuth.getUid());
                           user.setName(editText.getText().toString().trim());
                           databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(getContext(),"Successfully updated name",Toast.LENGTH_LONG).show();
                                   dialogInterface.dismiss();
                               }
                           });
                       }
                   }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.cancel();
               }
           });

           AlertDialog dialog=mBuilder.create();
           dialog.setTitle("Enter New Name");
           dialog.show();

          }
      });


        return  view;
    }



    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getContext(),"Please accept the required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Code);
            }


            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {

            imageUri = data.getData();
            image.setImageURI(imageUri);

            updateUserInfo(imageUri,user);

        }


    }


    private void updateUserInfo(Uri Uri, final User user) {


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
                                .setDisplayName(user.getName())
                                .setPhotoUri(uri)
                                .build();


                        mAuth.getCurrentUser().updateProfile(profileChangeRequest)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully


                                            final DatabaseReference user_Reference=mDatabase.getReference().child("users").child(mAuth.getUid());

                                            String image=uri.toString();

                                            user.setImageUri(image);

                                            user_Reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    Toast.makeText(getContext(), "successfully Updated ", Toast.LENGTH_SHORT).show();

                                                }
                                            });




                                        }

                                    }
                                })
                                .addOnFailureListener(getActivity(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                })
                        ;

                    }
                });





            }
        });






    }













}