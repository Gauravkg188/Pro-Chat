package com.kg.prochat.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kg.prochat.R;

public class LoginActivity extends AppCompatActivity {


    private EditText input_email,input_password;
    private Button login_button;
    private TextView register;
    private FirebaseAuth mAuth;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        login_button = findViewById(R.id.button_login);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = input_email.getText().toString().trim();
                password = input_password.getText().toString().trim();


                if(!email.isEmpty() && !password.isEmpty())
                {
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this, "Authentication successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                        startActivity(intent);





                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        input_email.setText("");
                                        input_email.setHint("Enter Email");
                                        input_password.setText("");
                                        input_password.setHint("Enter Password");

                                    }
                                }
                            });

                }

            }


        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       Intent intent=new Intent(Intent.ACTION_MAIN);
       intent.addCategory(intent.CATEGORY_HOME);
       intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(intent);
    }
}