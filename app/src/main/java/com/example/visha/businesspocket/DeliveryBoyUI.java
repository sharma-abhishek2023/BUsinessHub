package com.example.visha.businesspocket;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DeliveryBoyUI extends AppCompatActivity {

    private ListView listView;
    private List<RequestBySaler> list=new ArrayList<>();

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private GeoFire geoFire;

    private String productName;

    private ChildEventListener childEventListener;
    private Location currentLocation;
    private AlertDialog alertDialog;
    private GeoLocation requestedGeoLocation;

    private Boolean navigationAccepted=false;
    private Boolean destinationArrived=false;

    private LocationRequest locationRequest;
    private com.google.android.gms.location.LocationCallback locationCallback;

    private FusedLocationProviderClient mFusedLocationClient;

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
        mdatabaseReference.child("RequestBySeller").removeEventListener(childEventListener);
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        startActivity(intent);
        finish();

    }

    public void NavigationUI(){

        geoFire=new GeoFire(mdatabaseReference.child("RequestedLocations"));
        geoFire.getLocation(productName, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {

                requestedGeoLocation=location;

                navigationAccepted=true;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + location.latitude + "," + location.longitude));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeliveryBoyUI.this, "Failed :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            currentLocation=location;
                            if (navigationAccepted && !destinationArrived) {
                                if (calculateDistanceLeft() < 10) {

                                    Toast.makeText(DeliveryBoyUI.this, "Destination arrived", Toast.LENGTH_SHORT).show();
                                    destinationArrived = true;

                                }

                            }

                        }
                    });

                }

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void createLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public float calculateDistanceLeft(){

            Location requestedLocation=new Location(LOCATION_SERVICE);
            requestedLocation.setLatitude(requestedGeoLocation.latitude);
            requestedLocation.setLongitude(requestedGeoLocation.longitude);
            float distance = currentLocation.distanceTo(requestedLocation);
            float distanceInDP = (Math.round(distance * 10)) / 10;
            return distanceInDP;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_ui);

        getSupportActionBar().setTitle("Welcome DeliveryBoy");

        listView=findViewById(R.id.listView);
        final Adapt arrayAdapter=new Adapt(this,R.layout.layout_emp,list);
        listView.setAdapter(arrayAdapter);

        currentLocation=new Location(LOCATION_SERVICE);

        mfirebaseDatabase=FirebaseDatabase.getInstance();
        mdatabaseReference=mfirebaseDatabase.getReference();

        locationCallback = new com.google.android.gms.location.LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location:locationResult.getLocations()) {

                    currentLocation = location;
                    if (navigationAccepted && !destinationArrived) {
                        if (calculateDistanceLeft() < 10) {

                            Toast.makeText(DeliveryBoyUI.this, "Destination arrived", Toast.LENGTH_SHORT).show();
                            destinationArrived = true;

                        }

                    }
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof ResolvableApiException){

                    try{

                        ResolvableApiException resolve=(ResolvableApiException) e;
                        resolve.startResolutionForResult(DeliveryBoyUI.this,1);

                    }
                    catch(Exception e1){

                        e.printStackTrace();

                    }

                }


            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    currentLocation=location;
                    if (navigationAccepted && !destinationArrived) {
                        if (calculateDistanceLeft() < 10) {

                            Toast.makeText(DeliveryBoyUI.this, "Destination arrived", Toast.LENGTH_SHORT).show();
                            destinationArrived = true;

                        }

                    }

                }
            });

        }

        else{

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }

        alertDialog=new AlertDialog.Builder(this)
                .setMessage("Navigate to selected location")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavigationUI();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists()){

                    RequestBySaler requestBySaler=dataSnapshot.getValue(RequestBySaler.class);
                    list.add(requestBySaler);
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

        mdatabaseReference.child("RequestBySeller").addChildEventListener(childEventListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                productName=list.get(i).getName();
                alertDialog.show();
            }
        });

    }

    public class Adapt extends ArrayAdapter<RequestBySaler> {
        List<RequestBySaler> elsd = new ArrayList<>();

        public Adapt(Context context, int resource, List<RequestBySaler> objects) {
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
            item.setText(elsd.get(position).getRetailername());
            ql.setText(elsd.get(position).getName());
            qs.setText(elsd.get(position).getQuantity());
            return convertView;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(navigationAccepted && !destinationArrived){

            recreate();

        }

    }
}
