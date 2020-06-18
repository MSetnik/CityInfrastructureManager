package com.example.cityinfrastrukturemanager.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.Model.NajbliziIspad;
import com.example.cityinfrastrukturemanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.jar.Pack200;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MyApp";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean singleMarker = false;
    private Location currentLocation;
    private Marker mClosestMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        GetLocationPermission();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            GetMyLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private NajbliziIspad GetClosestMarker(IspadPrikaz ispad)
    {
        Location markerLocation = new Location("marker");
        markerLocation.setLatitude(ispad.getLat());
        markerLocation.setLongitude(ispad.getLng());

        float udaljenost = currentLocation.distanceTo(markerLocation)/1000;
        NajbliziIspad najbliziIspad = new NajbliziIspad();
        najbliziIspad.setIspadPrikaz(ispad);
        najbliziIspad.setUdaljenost(udaljenost);

        return najbliziIspad;
    }


    private void MoveCamera( LatLng latLng)
    {
        //mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

    }

    private void GetLatLng()
    {
        double najmanjaUdaljenost = 1E38; //
        ArrayList<IspadPrikaz>lIspadPrikaz = (ArrayList<IspadPrikaz>) getIntent().getSerializableExtra("ispadi");
        LatLng latLng;
        NajbliziIspad najbliziIspad = new NajbliziIspad();

        for(IspadPrikaz ispad : lIspadPrikaz)
        {
            double lat = ispad.getLat();
            double lng = ispad.getLng();

            latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(ispad.getVrstaIspada() + " - "  + ispad.getOpis()));
            if (lIspadPrikaz.size() == 1)
            {
                singleMarker=true;
                MoveCamera(latLng);
            }

            NajbliziIspad ispad2 = GetClosestMarker(ispad);
            if (najmanjaUdaljenost>ispad2.getUdaljenost())
            {
                najmanjaUdaljenost = ispad2.getUdaljenost();
                najbliziIspad = ispad2;

            }
        }
        //kamera pokazuje lokaciju najblizeg markera
        MoveCamera(new LatLng(najbliziIspad.getIspadPrikaz().getLat(), najbliziIspad.getIspadPrikaz().getLng()));
        Log.d(TAG, "GetLatLng: najblizi ispad " + najbliziIspad.getIspadPrikaz().getGrad());
    }


    private void InitMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void GetMyLocation()
    {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            if(mLocationPermissionGranted)
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                         if(task.isSuccessful())
                         {
                             Log.d(TAG, "onComplete: found location!");
                             currentLocation = (Location) task.getResult();

                             if(currentLocation !=null && singleMarker == false)
                             {
                                 GetLatLng();

                                 // Kamera pokazuje moju trenutnu lokaciju
                                 //MoveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                             }
                             else if(currentLocation == null)
                             {
                                 Toast.makeText(MapsActivity.this, "Uključite GPS kako bi dohvatili vašu lokaciju", Toast.LENGTH_SHORT).show();
                             }

                         }
                         else
                         {
                             Log.d(TAG, "onComplete: current location is null");
                             Toast.makeText(MapsActivity.this, "Greška prilikom dohvaćanja lokacije", Toast.LENGTH_SHORT).show();
                         }
                    }
                });
            }
        } catch(SecurityException e)
        {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void GetLocationPermission()
    {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),  FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
                InitMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE :
            {
                if(grantResults.length > 0)
                {
                    for(int i = 0;i<grantResults.length;i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                }
                mLocationPermissionGranted = true;

                InitMap();
            }
        }
    }
}
