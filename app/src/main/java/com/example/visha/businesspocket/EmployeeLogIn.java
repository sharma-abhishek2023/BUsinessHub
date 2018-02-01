package com.example.visha.businesspocket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by visha on 01 Feb 2018.
 */

public class EmployeeLogIn extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private AlertDialog alertDialog;
    public static ChildEventListener childEventListener;

    public static EditText name;
    public static Spinner spinner;
    public static Spinner businessSpinner;
    List<String> categoryList= Arrays.asList("Salesman","Deliveryboy");
    List<String> businessNames=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {

                    businessNames.add((String) dataSnapshot.getValue());
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, businessNames);
                    businessSpinner.setAdapter(arrayAdapter);

                } else {

                    businessSpinner.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            alertDialog.show();
                            return false;
                        }
                    });

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void backToHome(){

        AuthUI.getInstance().signOut(getActivity());
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.employee_login,container,false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("BusinessNames").addChildEventListener(childEventListener);

        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,categoryList);
        spinner=v.findViewById(R.id.spinner);
        businessSpinner=v.findViewById(R.id.spinner2);

        spinner.setAdapter(arrayAdapter);
        name= v.findViewById(R.id.enterNameEditText);

        alertDialog=new AlertDialog.Builder(getActivity())
                .setMessage("No business found :(")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        backToHome();
                    }
                })
                .create();

        return v;
    }
}
