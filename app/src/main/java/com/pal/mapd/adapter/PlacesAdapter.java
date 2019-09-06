package com.pal.mapd.adapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.pal.mapd.api.PlacesAPI;
import com.pal.mapd.apiModels.nearby_models.Result;
import com.pal.mapd.R;
import com.pal.mapd.apiModels.places_models.PlacesList;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    private Context ctx;
    private List<Result> nearbyList;
    OkHttpClient client ;








    public PlacesAdapter(Context ctx, List<Result> nearbyList) {
        this.ctx = ctx;
        this.nearbyList = nearbyList;
        client=new OkHttpClient();
        }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.single_places_layout, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlacesViewHolder holder, final int position) {
        Result list = nearbyList.get(position);

        holder.places_name.setText(list.getName());
        holder.places_address.setText(list.getVicinity());
        String pref = list.getPhotos().get(0).getPhotoReference();
        String maxwidth="200";
        String maxheight="200";
        String key = "AIzaSyBwejucEA4uHmcE1JKEPbtXR1YG8vnFWqQ";
        String url = "https://maps.googleapis.com/maps/api/place/photo?key="+key+"&maxwidth="+maxwidth+"&maxheight="+maxheight+"&photoreference="+pref;





        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ctx , "Loading error" , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(Call call, Response response) {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());


                ((Activity)ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.places_picture.setImageBitmap(bitmap);

                    }
                });

            }
        });

















    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {
        ImageView places_picture;
        TextView places_name;
        TextView places_address;


        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            places_picture = itemView.findViewById(R.id.places_pic);
            places_name = itemView.findViewById(R.id.places_name);
            places_address = itemView.findViewById(R.id.places_address);


        }
    }








}
