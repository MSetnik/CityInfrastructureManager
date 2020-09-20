package com.example.cityinfrastrukturemanager.Activity;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.Model.NajbliziIspad;
import com.example.cityinfrastrukturemanager.Model.SifrarnikVrstaIspada;
import com.example.cityinfrastrukturemanager.Model.Zupanija;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MyApp";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Handler h;

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;

    private TextView txtPocetak;
    private TextView pocetakPicker;
    private TextView txtKraj;
    private TextView krajPicker;
    private int dateIntHelper;
    private Button filterBtn;
    private Spinner spinnerIspad;
    private Spinner spinnerZupanija;
    ArrayList<IspadPrikaz> lIspadPrikaz;
    private MyViewModel viewModel;
    private ArrayList<Zupanija>lZupanije;
    private ArrayList<SifrarnikVrstaIspada>lVrsteIspada;
    private Button resetBtn;

    private int zupanija;
    private int vrstaIspada;
    private String datumP = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GetLocationPermission();
        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MyViewModel.class);
        this.lZupanije = viewModel.DohvatiZupanije();
        this.lVrsteIspada = viewModel.DohvatiVrsteIspade();
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

        GetIspadiData();
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if(currentLocation != null)
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 8));

                    }
                    return true;
                }
            });
        }
        else{
            GetLatLng(lIspadPrikaz);
            LatLng Zagreb = new LatLng(45.815399, 15.966568);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Zagreb, 6));
        }
    }

    private void GetIspadiData()
    {
        lIspadPrikaz = (ArrayList<IspadPrikaz>) getIntent().getSerializableExtra("ispadi");

        Button activityFilterButton = findViewById(R.id.button);
        activityFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.filter_dialog_trenutni_ispadi, null);
                alertDialog.setView(ispadDetaljiView);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                spinnerZupanija = ispadDetaljiView.findViewById(R.id.spinnerZupanije);
                spinnerIspad = ispadDetaljiView.findViewById(R.id.spinnerIspad);
                ArrayAdapter<Zupanija> adapterSpinnerZupanije = new ArrayAdapter<Zupanija>(MapsActivity.this , android.R.layout.simple_spinner_item, lZupanije);
                ArrayAdapter<SifrarnikVrstaIspada>adapterSpinnerVrstaIspada = new ArrayAdapter<SifrarnikVrstaIspada>(getApplicationContext(), android.R.layout.simple_spinner_item, lVrsteIspada);

                adapterSpinnerZupanije.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterSpinnerVrstaIspada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerZupanija.setAdapter(adapterSpinnerZupanije);
                spinnerIspad.setAdapter(adapterSpinnerVrstaIspada);


                txtPocetak = ispadDetaljiView.findViewById(R.id.fdTxtPocetak);
                pocetakPicker = ispadDetaljiView.findViewById(R.id.fdPickerPocetak);
                filterBtn = ispadDetaljiView.findViewById(R.id.filterBtn);
                resetBtn = ispadDetaljiView.findViewById(R.id.resetFilterBtn);

                if(datumP != null)
                {
                    pocetakPicker.setText(datumP);
                }

                pocetakPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dateIntHelper = 0;
                        ShowDatePickerDialog();

                    }
                });
                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetLatLng(viewModel.FilterMapsLogic(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(),pocetakPicker.getText().toString(),lIspadPrikaz));
                        zupanija = spinnerZupanija.getSelectedItemPosition();
                        vrstaIspada = spinnerIspad.getSelectedItemPosition();
                        alertDialog.dismiss();
                    }
                });

                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinnerZupanija.setSelection(0);
                        spinnerIspad.setSelection(0);
                        pocetakPicker.setText(R.string.odaberite_datum);

                        zupanija = spinnerZupanija.getSelectedItemPosition();
                        vrstaIspada = spinnerIspad.getSelectedItemPosition();
                        datumP = null;

                        GetLatLng(viewModel.FilterMapsLogic(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(),pocetakPicker.getText().toString(),lIspadPrikaz));
                        alertDialog.dismiss();
                    }
                });
                spinnerZupanija.setSelection(zupanija);
                spinnerIspad.setSelection(vrstaIspada);
            }
        });
    }

    private NajbliziIspad GetClosestMarker(IspadPrikaz ispad) {
        Location markerLocation = new Location("marker");
        markerLocation.setLatitude(ispad.getLat());
        markerLocation.setLongitude(ispad.getLng());

        float udaljenost = currentLocation.distanceTo(markerLocation) / 1000; // u kilometrima
        NajbliziIspad najbliziIspad = new NajbliziIspad();
        najbliziIspad.setIspadPrikaz(ispad);
        najbliziIspad.setUdaljenost(udaljenost);


        return najbliziIspad;
    }


    private void MoveCamera(LatLng latLng) {
        //mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

    }

    private void GetLatLng(ArrayList<IspadPrikaz>lIspadi) {
        mMap.clear();
        double najmanjaUdaljenost = 1E38; // veliki broj
        ArrayList<IspadPrikaz> lIspadPrikaz = lIspadi;
        LatLng latLng;
        NajbliziIspad najbliziIspad = new NajbliziIspad();
        NajbliziIspad ispad2;

        for (IspadPrikaz ispad : lIspadPrikaz)
        {
            Log.d(TAG, "GetLatLng:  sLocation " + ispad.getGrad() + "   " + ispad.getLat());
            double lat = ispad.getLat();
            double lng = ispad.getLng();
            latLng = new LatLng(lat, lng);


            if (ispad.getVrstaIspada().equals("Nestanak električne energije"))
            {
                BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.ic_baseline_power_off_24);

                mMap.addMarker(new MarkerOptions().position(latLng).title(ispad.getVrstaIspada() + " - " + ispad.getOpis()).icon(markerIcon));
            }
            if (ispad.getVrstaIspada().equals("Prekid prometa"))
            {
                BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.ic_baseline_directions_car_24);
                mMap.addMarker(new MarkerOptions().position(latLng).title(ispad.getVrstaIspada() + " - " + ispad.getOpis()).icon(markerIcon));
            }
            if (ispad.getVrstaIspada().equals("Nestanak vode"))
            {
                BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.ic_baseline_waves_24);
                mMap.addMarker(new MarkerOptions().position(latLng).title(ispad.getVrstaIspada() + " - " + ispad.getOpis()).icon(markerIcon));
            }
            if (ispad.getVrstaIspada().equals("Nestanak plina"))
            {
                BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.ic_nestanak_plina);
                mMap.addMarker(new MarkerOptions().position(latLng).title(ispad.getVrstaIspada() + " - " + ispad.getOpis()).icon(markerIcon));
            }

            if (lIspadPrikaz.size() == 1) {
                MoveCamera(latLng);
            }

            if (currentLocation != null)
            {
                ispad2 = GetClosestMarker(ispad);
                if (najmanjaUdaljenost > ispad2.getUdaljenost()) {
                    najmanjaUdaljenost = ispad2.getUdaljenost();
                    najbliziIspad = ispad2;

                }
            }

        }



        if(najbliziIspad.getIspadPrikaz() != null && currentLocation != null)
        {
            MoveCamera(new LatLng(najbliziIspad.getIspadPrikaz().getLat(), najbliziIspad.getIspadPrikaz().getLng()));
            Log.d(TAG, "GetLatLng: najblizi ispad " + najbliziIspad.getIspadPrikaz().getGrad());
        }
        //kamera pokazuje lokaciju najblizeg markera

    }

//    public ArrayList<IspadPrikaz> FilterLogic(Spinner spinnerZupanija, Spinner spinnerVrstaIspada, TextView datumPocetak, ArrayList<IspadPrikaz>lIspadPrikaz) {
//        ArrayList<IspadPrikaz> filter = new ArrayList<>();
//
//
//        String dateReversePocetak = datumPocetak.getText().toString();
//
//
//        if (!datumPocetak.getText().toString().equals("Odaberite datum")) {
//            String date = datumPocetak.getText().toString();
//            String date1 = date.replaceAll("[.]", "");
//            dateReversePocetak = ReverseDate(date1);
//
//        }
//
//
//        for (IspadPrikaz ispadPrikaz : lIspadPrikaz) {
//            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
//            pocetakIspada = pocetakIspada.replaceAll("[.]", "");
//
//            if (!datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada()) && Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
//
//                    Log.d(TAG, "FilterLogic: if 1 ");
//                    filter.add(ispadPrikaz);
//
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if ( spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    filter.add(ispadPrikaz);
//                }
//
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReversePocetak) >= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))&& spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada()) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if ( spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if ( spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    Log.d(TAG, "FilterLogic:  pocetak ispada ");
//                    filter.add(ispadPrikaz);
//                }
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (!datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
//                if (spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
//                    filter.add(ispadPrikaz);
//                }
//            } else {
//                filter = lIspadPrikaz;
//            }
//
//        }
//
//        return filter;
//    }

    private void ShowDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

//    private String ReverseDate(String date)
//    {
//        String godina = date.substring(4,8);
//        String mjesec = date.substring(2,4);
//        String dan = date.substring(0,2);
//        String strDate = godina+mjesec+dan;//datetime.substring(0,11);
//        return strDate;
//    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    public static String GetDate(String datetime) {
//        String godina = datetime.substring(0,4);
//        String mjesec = datetime.substring(5,7);
//        String dan = datetime.substring(8,10);
//        String strDate = dan+"."+mjesec+"."+godina;
//        return strDate;
//    }


    private void InitMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


    }

    public void GetMyLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();

                            GetLatLng(lIspadPrikaz);
                            if (currentLocation != null) {

                            } else {
                                LatLng Zagreb = new LatLng(45.815399, 15.966568);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Zagreb, 6));
                                Toast.makeText(MapsActivity.this, "Uključite GPS kako bi dohvatili vašu lokaciju", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Greška prilikom dohvaćanja lokacije", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    //provjeravaju se dozvole za pristup gpsu
    public void GetLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                InitMap();
                GetMyLocation();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {

            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = true;
                            GetLocationPermission();
                            return;
                        }
                    }
                }

                mLocationPermissionGranted = false;
                InitMap();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        DecimalFormat df = new DecimalFormat("00");
        int dan = day;
        int mon = month + 1;
        String sDan = df.format(dan);
        String mont = df.format(mon);


        String date = sDan + "." + mont + "."+ year;
        SetDate(date);
    }

    public void SetDate(String date)
    {
        if(dateIntHelper == 0)
        {
            pocetakPicker.setText(date);
            datumP = date;
        }
        else
        {
            krajPicker.setText(date);
        }
    }
}
