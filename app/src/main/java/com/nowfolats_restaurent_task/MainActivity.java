package com.nowfolats_restaurent_task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Movie;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.nowfolats_restaurent_task.Model.RestaurentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener
{
    private static String lat = "";
    private static String lng = "";
    private String API_KEY = "AIzaSyAFXum2RgOSqu9CzSzD-zXtpx3J1lPv_nQ";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    RequestQueue requestQueue;
    private RecyclerView recycler;
    public  String name = "";
    public  String icon = "";
    public  String vicinity = "";
    ArrayList<RestaurentData> proSearch = new ArrayList<RestaurentData>();
    private RestaurentAdapter rvAdapter;
    ArrayList<RestaurentData> storeModelArrayList = new ArrayList<>();

    public EditText store_search_query;
    public InputMethodManager imm;

    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
        setContentView(R.layout.activity_main);
        store_search_query = findViewById(R.id.store_search_query);
        requestQueue = Volley.newRequestQueue(this);
        recycler = findViewById(R.id.recycler);
        recycler.setVisibility(View.VISIBLE);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        sharedPref = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        }
        else
        {
            checkGPS();

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favourites, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);
        item.setActionView(R.layout.show_protected_switch);
        Switch switchAB = item
                .getActionView().findViewById(R.id.switchAB);
        switchAB.setChecked(false);

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    recycler.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplication(), "ON", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    recycler.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplication(), "OFF", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return true;
    }


    private void checkGPS()
    {
        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            PendingResult result = LocationServices.SettingsApi .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback() {
                @Override
                public void onResult(@NonNull Result result) {
                    final Status status = result.getStatus();
                    //final LocationSettingsStates state = result .getLocationSettingsStates();
                    switch (status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.e("gps enabled","");
                            getLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                Log.e("gps resolution","");
                                status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("gps unavbble","");
                            break;

                    }
                }
            });
        }
    }

   public void getLocation()
   {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1,this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, (LocationListener) this);

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());
        Log.d("location",lat+"=="+lng);
        editor.putString("latitude",lat).commit();
        editor.putString("longitude",lng).commit();
        getServerData();
        if(locationManager != null)
        {
            locationManager.removeUpdates((LocationListener) MainActivity.this);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        //  Toast.makeText(context, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        if(locationManager != null){
            locationManager.removeUpdates((LocationListener) this);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        try
        {
            if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == 0)
            {
                Log.e("location", ":deny");
                Toast.makeText(getApplicationContext(),"GPS Access denied",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.e("location", ":allowed");
                getLocation();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getServerData()
    {
        final ProgressDialog progDilog = new ProgressDialog(this);
        progDilog.setMessage("Loading ...");
        progDilog.show();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+sharedPref.getString("latitude","")+","+sharedPref.getString("longitude","")+
                "&radius=2500&type=restaurant&keyword=cruise&key="+API_KEY;

        /*String urlGetServerData = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=17.4447,78.4664"+
                "&radius=2500&type=restaurant&keyword=cruise&key=AIzaSyAFXum2RgOSqu9CzSzD-zXtpx3J1lPv_nQ";*/
        Log.d("API",url);
        proSearch.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                        progDilog.dismiss();
                        System.out.println(response);
                        try {
                            Gson gson = new Gson();
                            JSONArray jsonArray = response.getJSONArray("results");
                            JSONObject jsonObject = null;
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                name = jsonObject.getString("name");
                                icon = jsonObject.getString("icon");
                                vicinity = jsonObject.getString("vicinity");
                                Log.d("results", jsonObject.toString());
                                Log.d("name", name + " " + icon + " " + vicinity);


                                proSearch.add(new RestaurentData(name,vicinity,icon));

                            }

                            rvAdapter = new RestaurentAdapter(MainActivity.this, proSearch);
                            recycler.setNestedScrollingEnabled(false);
                            recycler.setAdapter(rvAdapter);

                            store_search_query.addTextChangedListener(new TextWatcher()
                            {

                                public void afterTextChanged(Editable s)
                                {
                                    Log.e("cahr",s.toString().toLowerCase());
                                    if (rvAdapter != null)
                                        rvAdapter.getFilter().filter(s.toString().toLowerCase());
                                }

                                public void beforeTextChanged(CharSequence s, int start,
                                                              int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }
                            });


                            // rvAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


}

