/**
 *
 * By Gaurav Kumar
 * Application along with all chat  application functionality provide magical ability to make users able to chat in their language and application translate message for them
 */

package com.kg.prochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.kg.prochat.Activity.HomeActivity;
import com.kg.prochat.Activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onStart() {
        super.onStart();


        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {

            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }


}