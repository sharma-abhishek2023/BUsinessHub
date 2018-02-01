package com.example.visha.businesspocket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentThree extends ListFragment {
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private final String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static String businessName;

    private Adaptr adaptr;

    public static ChildEventListener childEventListener;

    List<Employee> empls=new ArrayList<Employee>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_three,container,false);
        mfirebaseDatabase=FirebaseDatabase.getInstance();
        mdatabaseReference=mfirebaseDatabase.getReference();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        ListView lv =getListView();

        adaptr=new Adaptr(getActivity(),R.layout.layout_emp,empls);
        lv.setAdapter(adaptr);

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Employee employee=dataSnapshot.getValue(Employee.class);
                empls.add(employee);
                adaptr.notifyDataSetChanged();
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

        mdatabaseReference.child("Users").child(userId).child("businessname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    businessName=(String) dataSnapshot.getValue();
                    mdatabaseReference.child(businessName).addChildEventListener(childEventListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public class Adaptr extends ArrayAdapter<Employee> {
        List<Employee> elsd = new ArrayList<>();

        public Adaptr(Context context, int resource, List<Employee> objects) {
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
            TextView qs = convertView.findViewById(R.id.quantitySold);

            item.setText(elsd.get(position).getName());
            ql.setText(elsd.get(position).getType());
            qs.setText(elsd.get(position).getPhoneNo());


            return convertView;
        }
    }
}
