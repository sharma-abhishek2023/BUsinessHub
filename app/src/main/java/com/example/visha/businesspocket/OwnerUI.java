package com.example.visha.businesspocket;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OwnerUI extends AppCompatActivity {

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.signOut :
                AuthUI.getInstance().signOut(this);
                SignOut();


        }
        return super.onOptionsItemSelected(item);
    }

    public void SignOut(){

        Intent intent=new Intent(this,MainActivity.class);
        mdatabaseReference.child(FragmentThree.businessName).removeEventListener(FragmentThree.childEventListener);
        startActivity(intent);
        finish();

    }

    public void setUpViewPager(ViewPager viewPager){

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentOne(),"One");
        viewPagerAdapter.addFragment(new FragmentTwo(),"Two");
        viewPagerAdapter.addFragment(new FragmentThree(),"Three");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_ui);

        Toolbar toolbar=findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        mfirebaseDatabase=FirebaseDatabase.getInstance();
        mdatabaseReference=mfirebaseDatabase.getReference();

        ViewPager viewPager=findViewById(R.id.viewPager);

        TabLayout tabLayout=findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        setUpViewPager(viewPager);

    }
}
