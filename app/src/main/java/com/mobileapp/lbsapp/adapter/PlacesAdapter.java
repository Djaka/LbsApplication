package com.mobileapp.lbsapp.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mobileapp.lbsapp.DetailActivity;
import com.mobileapp.lbsapp.ListPlaces;
import com.mobileapp.lbsapp.R;
import com.mobileapp.lbsapp.model.Tblplaces;

import java.io.File;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    List<Tblplaces> listPlaces;
    Context context;

    public PlacesAdapter(List<Tblplaces> listPlaces) {
        this.listPlaces = listPlaces;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_location, viewGroup, false);
        context = viewGroup.getContext();
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder placesViewHolder, int position) {
        Tblplaces places = listPlaces.get(position);
        placesViewHolder.location_name.setText(places.getLocationname());
        placesViewHolder.location_latitude_longitude.setText(places.getLatitude() +"," + places.getLongitude());
        placesViewHolder.location_address.setText(places.getLocationaddress());

        File imagedir = new File(Environment.getExternalStorageDirectory(), "smdplaces" + "/" + places.getImagePath());
        
        if(!imagedir.exists()){
            placesViewHolder.imagePlaces.setImageResource(R.drawable.ic_image_default);
        }else{
            Glide.with(context).load(imagedir).into(placesViewHolder.imagePlaces);
        }

        placesViewHolder.linearLayoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("place_id",places.getUuid());
                context.startActivity(intent);
            }
        });

        placesViewHolder.linearLayoutLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are You Sure to delete this places?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tblplaces.executeQuery("DELETE FROM Tblplaces WHERE uuid = '" + places.getUuid() +"'");
                        ((ListPlaces)context).refreshListPlaces();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPlaces.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayoutLocation;
        TextView location_name, location_latitude_longitude, location_address;
        ImageView imagePlaces;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePlaces = itemView.findViewById(R.id.location_img);
            linearLayoutLocation = itemView.findViewById(R.id.lin_location);
            location_name = itemView.findViewById(R.id.location_name);
            location_latitude_longitude = itemView.findViewById(R.id.location_latitude_longitude);
            location_address = itemView.findViewById(R.id.location_address);
        }
    }
}
