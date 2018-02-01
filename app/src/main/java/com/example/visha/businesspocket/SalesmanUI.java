package com.example.visha.businesspocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.zip.Inflater;

public class SalesmanUI extends AppCompatActivity {

    private ListView listView;
    private Adapt arrayAdapter;
    private List<Salesdata> list=new ArrayList<>();
    private String businessName;
    private ChildEventListener childEventListener;
    private String productName;
    private EditText editText1;
    private EditText editText2;
    public static PlacePicker.IntentBuilder intentBuilder;

    private RequestBySaler requestBySaler;

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private final String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private GeoFire geoFire;

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
        mdatabaseReference.child("Users").child(userId).child("businessname").removeEventListener(childEventListener);
        startActivity(intent);
        finish();

    }

    public void raiseRequest(Place place){

        LatLng latLng=place.getLatLng();
        final GeoLocation requestedGeoLocation=new GeoLocation(latLng.latitude,latLng.longitude);
        mdatabaseReference.child("RequestsBySaler").push().setValue(requestBySaler).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                geoFire=new GeoFire(mdatabaseReference.child("RequestedLocations"));
                geoFire.setLocation(productName,requestedGeoLocation);
                Toast.makeText(SalesmanUI.this, "OrderPlaced", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1){

            if(resultCode==RESULT_OK) {

                Place place = PlacePicker.getPlace(data, SalesmanUI.this);
                raiseRequest(place);
                requestBySaler=new RequestBySaler(productName,editText1.getText().toString(),editText2.getText().toString());
                mdatabaseReference.child("RequestBySeller").push().setValue(requestBySaler);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_ui);

        getSupportActionBar().setTitle("Welcome Salesman");

        listView= findViewById(R.id.listView);
        arrayAdapter=new Adapt(this,R.layout.layout_emp,list);
        listView.setAdapter(arrayAdapter);

        mfirebaseDatabase=FirebaseDatabase.getInstance();
        mdatabaseReference=mfirebaseDatabase.getReference();

        View v=getLayoutInflater().inflate(R.layout.sales_add,null,false);

        intentBuilder=new PlacePicker.IntentBuilder();

        editText1=v.findViewById(R.id.editText1);
        editText2=v.findViewById(R.id.editText2);
        editText1.setHint("Enter Quantity");
        editText2.setHint("Enter Retailer Name");

        final AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("Place order")
                .setView(v)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {

                            startActivityForResult(intentBuilder.build(SalesmanUI.this), 1);

                        } catch (GooglePlayServicesRepairableException e) {
                            // ...
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            // ...
                            e.printStackTrace();
                        }

                    }
                })
                .create();

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){

                    Salesdata salesdata=dataSnapshot.getValue(Salesdata.class);
                    list.add(salesdata);
                    arrayAdapter.notifyDataSetChanged();
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

        mdatabaseReference.child("Users").child(userId).child("businessname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    businessName=(String) dataSnapshot.getValue();
                    mdatabaseReference.child(businessName).child(businessName+"-requests").addChildEventListener(childEventListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                alertDialog.show();
                productName=list.get(i).getproductName();

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
