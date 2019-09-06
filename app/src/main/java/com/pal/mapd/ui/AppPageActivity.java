package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pal.mapd.R;

import java.util.Objects;


public class AppPageActivity extends AppCompatActivity {


    private boolean mIsPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


    }


    private void getLastKnownLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location loc = task.getResult();
                assert loc != null;
                String lat = String.valueOf(loc.getLatitude());
                String lng = String.valueOf(loc.getLongitude());
                mDatabase.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("lat").setValue(lat);
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("lng").setValue(lng);

            }
        });
    }









    @Override
    protected void onResume() {
        super.onResume();

        if(checkMapsServices()){

            if(mIsPermissionGranted){
                getLastKnownLocation();
                sendToMaps();

            }else{
                getLocationPermission();
            }
        }

    }

    public boolean checkMapsServices(){
        if(isServicesOk()){

            return isMapEnabled();
        }
        return false;
    }




    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AppPageActivity.this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AppPageActivity.this , available ,Constants.ERROR_DIALOGUE_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(AppPageActivity.this, "You cannot access Maps", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    public boolean isMapEnabled(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGPS() {

        AlertDialog.Builder builder=  new AlertDialog.Builder(this);
        builder.setMessage("The application requires GPS , Do you want to enable it ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent enableGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGPS , Constants.PERMISSION_REQUEST_ENABLE_GPS);

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PERMISSION_REQUEST_ENABLE_GPS) {
            if (mIsPermissionGranted) {
                getLastKnownLocation();
                sendToMaps();


            } else {
                getLocationPermission();
            }
        }
    }

    private void getLocationPermission() {


        if(ContextCompat.checkSelfPermission(this.getApplicationContext() , Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mIsPermissionGranted=true;
            getLastKnownLocation();
            sendToMaps();
        }else{

            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void sendToMaps() {

        Intent maps = new Intent(AppPageActivity.this , MapMainActivity.class);
        startActivity(maps);
        finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mIsPermissionGranted = false;
        if (requestCode == Constants.PERMISSION_REQUEST_ENABLE_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIsPermissionGranted = true;
            }
        }
    }
}
