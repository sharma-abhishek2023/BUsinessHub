package com.example.visha.businesspocket;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SwipeActivity extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String userId;
    private String phoneNo;
    private ViewPager viewPager;

    public void ownerUI(){

        Intent intent=new Intent(this,OwnerUI.class);
        startActivity(intent);
        finish();

    }

    public void EmployeeUI(){

        if(EmployeeLogIn.spinner.getSelectedItem().toString().equals("Deliveryboy")) {

            Intent intent = new Intent(this, DeliveryBoyUI.class);
            startActivity(intent);
            finish();

        }
        else{

            Intent intent = new Intent(this, SalesmanUI.class);
            startActivity(intent);
            finish();

        }

        mDatabaseReference.child("BusinessNames").removeEventListener(EmployeeLogIn.childEventListener);

    }

    public void onContinueClicked(View view){

        if(viewPager.getCurrentItem()==0) {

            if (ownerLogIn.name.getText().toString().trim().length() > 0 && ownerLogIn.businessName.getText().toString().trim().length() > 0 && ownerLogIn.officeNumber.getText().toString().trim().length() > 0) {

                Owner owner = new Owner(ownerLogIn.name.getText().toString(), ownerLogIn.officeNumber.getText().toString(), ownerLogIn.businessName.getText().toString(), "owner");
                mDatabaseReference.child("Users").child(userId).setValue(owner);
                mDatabaseReference.child("BusinessNames").push().setValue(ownerLogIn.businessName.getText().toString());
                ownerUI();

            } else {

                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show();

            }
        }
        else{

            if (EmployeeLogIn.name.getText().toString().trim().length() > 0){

                Employee employee=new Employee(EmployeeLogIn.name.getText().toString(),EmployeeLogIn.spinner.getSelectedItem().toString(),EmployeeLogIn.businessSpinner.getSelectedItem().toString(),phoneNo);
                mDatabaseReference.child("Users").child(userId).setValue(employee);
                mDatabaseReference.child(EmployeeLogIn.businessSpinner.getSelectedItem().toString()).child(EmployeeLogIn.businessSpinner.getSelectedItem().toString()+"-employee").push().setValue(employee);
                EmployeeUI();

            } else {

                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show();

            }

        }

    }

    public void setUpViewPager(ViewPager viewPager){

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ownerLogIn(),"One");
        viewPagerAdapter.addFragment(new EmployeeLogIn(),"Two");
        viewPager.setAdapter(viewPagerAdapter);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);


        mFireBaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        userId=mFireBaseAuth.getCurrentUser().getUid();
        phoneNo=mFireBaseAuth.getCurrentUser().getPhoneNumber();


        viewPager=findViewById(R.id.viewPager);
        setUpViewPager(viewPager);

    }
}
