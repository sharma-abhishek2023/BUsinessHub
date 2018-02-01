package com.example.visha.businesspocket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 123;
    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
    );

    public void comingBackOwner() {

        Intent intent=new Intent(this,OwnerUI.class);
        startActivity(intent);
        finish();

    }

    public void comingBackDeliveryBoy() {

        Intent intent=new Intent(this,DeliveryBoyUI.class);
        startActivity(intent);
        finish();

    }

    public void comingBackSalesman(){

        Intent intent=new Intent(this,SalesmanUI.class);
        startActivity(intent);
        finish();

    }

    public void SignUpUI() {

        Intent intent=new Intent(this,SwipeActivity.class);
        startActivity(intent);
        finish();

    }

    public void redirectAccording2user() {

        userId = mFireBaseAuth.getCurrentUser().getUid();
        mDatabaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("type").getValue().equals("owner")) {

                        comingBackOwner();

                    } else if (dataSnapshot.child("type").getValue().equals("Deliveryboy")){

                        comingBackDeliveryBoy();

                    } else{

                        comingBackSalesman();

                    }
                    progressDialog.dismiss();
                } else {

                    progressDialog.dismiss();
                    AuthUI.getInstance().signOut(MainActivity.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkIfUserExist() {

        userId = mFireBaseAuth.getCurrentUser().getUid();
        mDatabaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("type").getValue().equals("owner")) {

                        comingBackOwner();

                    } else if (dataSnapshot.child("type").getValue().equals("Deliveryboy")){

                        comingBackDeliveryBoy();

                    } else{

                        comingBackSalesman();

                    }
                } else {

                    SignUpUI();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onContinueClicked(View view) {

        if (mFireBaseAuth.getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {

                //buttonActive=false;
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                checkIfUserExist();

            } else {

                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "SignIn cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this);

        if (mFireBaseAuth.getCurrentUser() != null) {

            //linearLayout.setVisibility(View.INVISIBLE);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
            redirectAccording2user();

        }
    }
}
