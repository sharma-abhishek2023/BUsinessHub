package com.example.visha.businesspocket;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by visha on 01 Feb 2018.
 */

public class FragmentOne extends ListFragment {
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private EditText editText1, editText2, editText3;
    FloatingActionButton fb;
    long datac;
    //Adapt eadt;
    private AlertDialog alertDialog;
    List<Salesdata> empls = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        fb = view.findViewById(R.id.floatingActionButton4);
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();
        View v = getLayoutInflater().inflate(R.layout.sales_add, null, false);
        editText1 = v.findViewById(R.id.editText1);
        editText2 = v.findViewById(R.id.editText2);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Add Product")
                .setView(v)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editText1.getText().toString().trim().length() > 0 && editText2.getText().toString().trim().length() > 0) {

                            final String string1 = editText1.getText().toString();
                            final String string2 = editText2.getText().toString();
                            final Salesdata salesdata = new Salesdata(string1, string2);
                            mdatabaseReference.child("ActiveProducts").child(userId).push().setValue(salesdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "submitted", Toast.LENGTH_SHORT).show();
                                    editText1.getText().clear();
                                    editText2.getText().clear();
                                    mdatabaseReference.child(FragmentThree.businessName).child(FragmentThree.businessName+"-requests").push().setValue(salesdata);
                                }
                            });


                        } else {

                            Toast.makeText(getContext(), "Please enter valid details", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.show();

            }
        });
        final Adapt adapt=new Adapt(getActivity(),R.layout.layout_emp,empls);
        lv.setAdapter(adapt);

        mdatabaseReference.child("ActiveProducts").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                        if(empls.size()<=dataSnapshot.getChildrenCount()) {
                            Salesdata salesdata = (dataSnapshot.getValue(Salesdata.class));
                            empls.add(salesdata);
                            adapt.notifyDataSetChanged();

                            Log.i("salesData", empls.toString());
                        }

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
        });

    }

    public class Adapt extends ArrayAdapter<Salesdata> {
        List<Salesdata> elsd = new ArrayList<Salesdata>();

        public Adapt(Context context, int resource, List<Salesdata> objects) {
            super(context, resource, objects);
            elsd = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.layout_emp, parent, false);
            }
            TextView item = convertView.findViewById(R.id.item);
            TextView ql = convertView.findViewById(R.id.quantity);
            item.setText(elsd.get(position).getproductName());
            ql.setText(elsd.get(position).getquantity());
            return convertView;

        }
    }
}

