package com.mobileapp.lbsapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.mobileapp.lbsapp.model.Tblplaces;
import com.mobileapp.lbsapp.utils.GPSTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static com.mobileapp.lbsapp.utils.GeneralFunction.compressbitmap;


public class AddPlace extends AppCompatActivity implements LocationListener {

    Toolbar toolbar;
    private String name;
    private String coordinate = "0.0,0.0";
    private double longitude;
    private double latitude;
    private String address;
    private String imagePath;
    private int category;
    String provider;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    EditText editTextName;
    EditText editTextCoordinat;
    EditText editTextaddress;
    Spinner spinCategory;
    Button buttonSaveLocation;
    GPSTracker gpsTracker;
    LocationManager locationManager;
    Location location;
    ImageView imgAdd;
    ImageView imgResult;
    Image image;
    private String[] arrCategory = {"Kuliner", "Wisata"};

    public static String TAG = AddPlace.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_place);

        double lat = getIntent().getDoubleExtra("EXTRA_LATITUDE",0);
        double lon = getIntent().getDoubleExtra("EXTRA_LONGITUDE",0);

        setLatitude(lat);
        setLongitude(lon);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_add_place));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = findViewById(R.id.edt_place_name);
        editTextCoordinat = findViewById(R.id.edt_coordinate);
        editTextaddress = findViewById(R.id.edt_address);
        buttonSaveLocation = findViewById(R.id.btn_save_location);
        imgAdd = findViewById(R.id.img_add);
        imgResult = findViewById(R.id.img_result);
        spinCategory = findViewById(R.id.spin_category);

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrCategory);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapterCat);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCategory(position==0?1:2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextCoordinat.setText(getLatitude() + "," + getLongitude());

        buttonSaveLocation.setOnClickListener(view -> {
            setName(editTextName.getText().toString());
            setLongitude(getLongitude());
            setLatitude(getLatitude());
            setAddress(editTextaddress.getText().toString());

            saveLocation();
        });

        imgAdd.setOnClickListener(view->{

            ImagePicker.create(AddPlace.this)
                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .single() // single mode
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                    .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        });

        imgResult.setOnClickListener(view->{
            ImagePicker.create(AddPlace.this)
                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .single() // single mode
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                    .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            image = ImagePicker.getFirstImageOrNull(data);
            Glide.with(this).load(image.getPath()).into(imgResult);
            imgResult.setVisibility(View.VISIBLE);
            imgAdd.setVisibility(View.GONE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveLocation() {

        Tblplaces tblplaces = new Tblplaces();
        tblplaces.setUuid(UUID.randomUUID().toString());
        tblplaces.setLocationname(getName());
        tblplaces.setLongitude(getLongitude());
        tblplaces.setLatitude(getLatitude());
        tblplaces.setLocationaddress(getAddress());
        tblplaces.setCategory(getCategory());

        if (image != null) {
            File imagedir = new File(Environment.getExternalStorageDirectory(), "/smdplaces/");
            if (!imagedir.exists()) {
                imagedir = new File(Environment.getExternalStorageDirectory(), "/smdplaces/");
                imagedir.mkdir();
            }

            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
            Bitmap empPhoto = compressbitmap(bitmap, false);

            String uuidImage = UUID.randomUUID().toString();
            File fileImg = new File(imagedir, uuidImage + ".jpeg");
            OutputStream outImage = null;
            try {
                outImage = new FileOutputStream(fileImg, true);
                empPhoto.compress(Bitmap.CompressFormat.JPEG, 75, outImage);
                outImage.flush();
                outImage.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setImagePath(uuidImage + ".jpeg");
        } else {
            setImagePath(null);
        }

        tblplaces.setImagePath(getImagePath());

        tblplaces.save();
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
