package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pal.mapd.R;
import com.pal.mapd.api.PlacesAPI;
import com.pal.mapd.apiModels.nearby_models.NearbyList;
import com.pal.mapd.apiModels.nearby_models.Result;
import com.pal.mapd.adapter.PlacesAdapter;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NearbyActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView ;
    private Result result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView = findViewById(R.id.nearby_places_of_interest_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String lat = dataSnapshot.child("lat").getValue().toString();
                String lng = dataSnapshot.child("lng").getValue().toString();

                String location = lat + "," + lng;





                Call<NearbyList> nearbyListCall = PlacesAPI.getPlacesService().getResult("cafe" , "3000" , location);

                nearbyListCall.enqueue(new Callback<NearbyList>() {

                    @Override

                    public void onResponse(Call<NearbyList> call, Response<NearbyList> response) {


                        NearbyList list = response.body();
                        if (list != null) {
                            recyclerView.setAdapter(new PlacesAdapter(NearbyActivity.this , list.getResults()));

                        }else{
                            Toast.makeText(NearbyActivity.this , "Could not find a place" , Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<NearbyList> call, Throwable t) {

                        Toast.makeText(NearbyActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }






}
