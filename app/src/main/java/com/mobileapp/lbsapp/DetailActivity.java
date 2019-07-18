package com.mobileapp.lbsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mobileapp.lbsapp.model.Tblplaces;

import java.io.File;
import java.net.URI;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fabDirection;
    TextView textViewTitle, textViewCoordinat, textViewAddress, textViewCategory;
    ImageView imageViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_detail_place));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewDetail = findViewById(R.id.img_location);
        textViewTitle = findViewById(R.id.txt_title);
        textViewCoordinat = findViewById(R.id.txt_lat_lon);
        textViewCategory = findViewById(R.id.txt_category);
        textViewAddress = findViewById(R.id.txt_address);
        fabDirection = findViewById(R.id.fab_direction);

        String place_id = getIntent().getStringExtra("place_id");
        List<Tblplaces> tblplaces = Tblplaces.findWithQuery(Tblplaces.class,"SELECT * FROM tblplaces WHERE uuid = '" +place_id+"'");

        textViewTitle.setText(tblplaces.get(0).getLocationname());
        textViewCoordinat.setText(tblplaces.get(0).getLatitude() + "," + tblplaces.get(0).getLongitude());
        textViewCategory.setText(tblplaces.get(0).getCategory()==1?"Kuliner":"Wisata");
        textViewAddress.setText(tblplaces.get(0).getLocationaddress());

        File imagedir = new File(Environment.getExternalStorageDirectory(), "smdplaces" + "/" + tblplaces.get(0).getImagePath());

        if(!imagedir.exists()){
            imageViewDetail.setImageResource(R.drawable.ic_image_default);
        }else{
            Glide.with(this).load(imagedir).into(imageViewDetail);
        }

        fabDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+tblplaces.get(0).getLatitude()+","+tblplaces.get(0).getLongitude()));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
