package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pal.mapd.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pal.mapd.ui.Constants.MAPVIEW_BUNDLE_KEY;

public class MapMainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private MapView mMapView;
    private DatabaseReference mDatabseRef;
    private FirebaseAuth mAuth;
    private TextView name;
    private TextView curr_loc;
    private CircleImageView image;
    private GoogleMap mMap;
    private ImageView mImage;
    private FusedLocationProviderClient mFusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //View intialization
        name = findViewById(R.id.map_page_user_name);
        image = findViewById(R.id.map_page_user_image);
        curr_loc = findViewById(R.id.map_page_current_location);
        mImage = findViewById(R.id.nearby_place_btn);

        //Firebase intialization
        mAuth = FirebaseAuth.getInstance();
        mDatabseRef = FirebaseDatabase.getInstance().getReference();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToSettings();
            }
        });


        initGoogleMap(savedInstanceState);
        getUserDetails();
        openNearby();


    }

    private void openNearby() {

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapMainActivity.this, NearbyActivity.class);
                startActivity(i);
            }
        });
    }


    private void initGoogleMap(Bundle savedInstanceState) {


        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.main_map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


    }

    private void AddressToDatabase(String city) {

        mDatabseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Address").setValue(city);


    }


    private void getDeviceLocation() {
        mDatabseRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String lat = dataSnapshot.child("lat").getValue().toString();
                String lng = dataSnapshot.child("lng").getValue().toString();

                Double latitude = Double.valueOf(lat);
                Double longitude = Double.valueOf(lng);


                moveCamera(new LatLng(latitude, longitude), 15.8f);

                String city = getLocationAddress(latitude, longitude);
                AddressToDatabase(city);

                curr_loc.setText(city);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getLocationAddress(Double latitude, Double longitude) {

        String myAddress = "";

        Geocoder geo = new Geocoder(MapMainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            Log.d("myAdd", addresses.toString());

            myAddress = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myAddress;
    }

    private void moveCamera(LatLng latlng, float zoom) {

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latlng , 16.8f)));
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setBuildingsEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions()
        .position(latlng)
        .title("Current Location"));


    }

    private void getUserDetails() {
        mDatabseRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name_ = dataSnapshot.child("name").getValue().toString();
                String image_ = dataSnapshot.child("image").getValue().toString();

                name.setText(name_);
                Picasso.get().load(image_).into(image);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getDeviceLocation();

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this , R.raw.map_style));




    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void sendToSettings() {

        Intent i = new Intent(MapMainActivity.this , SettingsActivity.class);
        startActivity(i);
    }
}
