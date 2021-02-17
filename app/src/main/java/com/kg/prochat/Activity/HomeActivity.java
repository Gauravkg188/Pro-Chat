package com.kg.prochat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kg.prochat.Adapter.ViewPagerAdapter;
import com.kg.prochat.Fragments.ChatFragment;
import com.kg.prochat.Fragments.ProfileFragment;
import com.kg.prochat.Fragments.UsersFragment;
import com.kg.prochat.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
     String userId;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());

        userId=FirebaseAuth.getInstance().getUid();
        adapter.addFragments(new ChatFragment(),"Chat");
        adapter.addFragments(new UsersFragment(),"Users");
        adapter.addFragments(new ProfileFragment(),"My Profile");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logOut:
                            {FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                                startActivity(intent);
                                return true;
                            }
            default: return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(intent.CATEGORY_HOME);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}