package com.mobileapp.lbsapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobileapp.lbsapp.adapter.PlacesAdapter;
import com.mobileapp.lbsapp.api.APIDirection;
import com.mobileapp.lbsapp.interfaces.IAppDirection;
import com.mobileapp.lbsapp.model.Tblplaces;
import com.mobileapp.lbsapp.response.ResponseRoute;

import java.util.List;

import retrofit2.Call;

public class ListPlaces extends AppCompatActivity {

    private String locationName = null;
    private int category;
    Toolbar toolbar;
    RecyclerView recyclerViewPlaces;
    PlacesAdapter placesAdapter;
    SwipeRefreshLayout refreshLayout;
    LinearLayout lin_no_places;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_kuliner));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewPlaces = findViewById(R.id.recycler_location);
        refreshLayout = findViewById(R.id.swipe_refresh_location);
        lin_no_places = findViewById(R.id.lin_no_places);

        listPlaces(recyclerViewPlaces);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                setLocationName(null);
                setCategory(0);
                listPlaces(recyclerViewPlaces);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_place, menu);

        MenuItem item = menu.findItem(R.id.mn_search);
        SearchView search = (SearchView) item.getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                setLocationName(s);
//                Toast.makeText(ListPlaces.this, s, Toast.LENGTH_SHORT).show();
                listPlaces(recyclerViewPlaces);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_filter:
                openDialogFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshListPlaces();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void refreshListPlaces(){
        listPlaces(recyclerViewPlaces);
    }

    private void listPlaces(RecyclerView recyclerView) {
        List<Tblplaces> places;

        if(getLocationName() != null && getCategory() != 0)
        {
            places = Tblplaces.findWithQuery(Tblplaces.class, "SELECT * FROM tblplaces WHERE locationname LIKE '%" + getLocationName() + "%' AND category = '" + getCategory() + "'");
        }
        else if(getLocationName() != null){
            places = Tblplaces.findWithQuery(Tblplaces.class, "SELECT * FROM tblplaces WHERE locationname LIKE '%" + getLocationName() + "%'");
        }
        else if(getCategory() != 0){
            places = Tblplaces.findWithQuery(Tblplaces.class, "SELECT * FROM tblplaces WHERE category LIKE '%" + getCategory() + "%'");
        }
        else{
            places = Tblplaces.findWithQuery(Tblplaces.class, "SELECT * FROM tblplaces");
        }

        if(places.size()>0){
            lin_no_places.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            placesAdapter = new PlacesAdapter(places);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListPlaces.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(placesAdapter);
        }else{
            lin_no_places.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    private void openDialogFilter() {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_category);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView buttonOke = dialog.findViewById(R.id.btn_oke);
        TextView buttonCancel = dialog.findViewById(R.id.btn_cancel);

        RadioGroup radioGroupCategorySchedule = dialog.findViewById(R.id.rg_category);
        radioGroupCategorySchedule.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.rb_kuliner:
                                setCategory(1);
                                break;
                            case R.id.rb_wisata:
                                setCategory(2);
                                break;
                            case R.id.rb_all:
                                setCategory(0);
                                break;
                        }
                    }
                });

        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialog;
        dialog.show();

        buttonOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPlaces(recyclerViewPlaces);
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
