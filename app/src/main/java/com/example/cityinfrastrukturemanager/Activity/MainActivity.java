package com.example.cityinfrastrukturemanager.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityinfrastrukturemanager.Adapter.ViewPagerAdapter;
import com.example.cityinfrastrukturemanager.Fragment.RijeseniIspadiFragment;
import com.example.cityinfrastrukturemanager.Fragment.TrenutniIspadiFragment;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.Model.SifrarnikVrstaIspada;
import com.example.cityinfrastrukturemanager.Model.Zupanija;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "MyApp";
    public MyViewModel viewModel;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ArrayList<Zupanija>lZupanije = new ArrayList<>();
    private ArrayList<SifrarnikVrstaIspada> lVrsteIspada = new ArrayList<>();
    public RijeseniIspadiFragment rijeseniIspadiFragment;
    public TrenutniIspadiFragment trenutniIspadiFragment;

    public MenuItem searchItem;
    public TextView pocetakPicker;
    public TextView krajPicker;
    public int dateIntHelper;
    public Button filterBtn;
    public Button resetBtn;
    public Spinner spinnerIspad;
    public Spinner spinnerZupanija;
    public int zupanija;
    public int vrstaIspada;
    public String datumP = null;
    public String datumK = null;
    private Boolean filterReset = false;
    public MenuItem filterItem;
    public AlertDialog alertDialog;
    public androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MyViewModel.class);


        lZupanije = viewModel.DohvatiZupanije();
        lVrsteIspada = viewModel.DohvatiVrsteIspade();


        ToolbarSetup();
        GoToMaps();
        ViewPagerImplementation();

    }


    private void ToolbarSetup()
    {
        Toolbar toolbar = findViewById(R.id.toolbar_include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.main_activity_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.ToolbarText));

    }


    private void GoToMaps()
    {
        final ArrayList<IspadPrikaz>lTrenutniIspadi = viewModel.DohvatiTrenutneIspade();
        FloatingActionButton floatingButton = findViewById(R.id.MapsFloatingButton);
        if(IsServicesOK())
        {

            floatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("ispadi", lTrenutniIspadi);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean IsServicesOK()
    {
        Log.d(TAG, "IsServicesOK: Checking google services ver.");

        int availible = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(availible == ConnectionResult.SUCCESS)
        {
            //Uspjeh
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availible))
        {
            //problem se pojavio, ali se može popraviti
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, availible, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "Ne možete pristupiti karti", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void ViewPagerImplementation()
    {
        ViewPager viewPager = findViewById(R.id.viewPager);

        ArrayList<Fragment> list = new ArrayList<>();
        ArrayList<String>lFragmentTitle = new ArrayList<>();
        trenutniIspadiFragment = new TrenutniIspadiFragment();
        rijeseniIspadiFragment = new RijeseniIspadiFragment();
        list.add(trenutniIspadiFragment);
        list.add(rijeseniIspadiFragment);

        lFragmentTitle.add("Trenutni ispadi");
        lFragmentTitle.add("Riješeni ispadi");

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), list, lFragmentTitle);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_list, menu);
        filterItem = menu.findItem(R.id.filterSearch);
        MenuItem infoItem = menu.findItem(R.id.info);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        filterReset = false;
        SetFilterDialog();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rijeseniIspadiFragment.recyclerviewAdapterR.getFilter().filter(newText);
                trenutniIspadiFragment.recyclerviewAdapterT.getFilter().filter(newText);
                return false;
            }
        });

        infoItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.colors_info, null);
                alertDialog.setView(ispadDetaljiView);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();
                return false;
            }
        });
        return true;
    }

    public void SetFilterDialog()
    {
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.filter_dialog_rijeseni_ispadi, null, false);
        alertDialog.setView(ispadDetaljiView);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        spinnerZupanija = ispadDetaljiView.findViewById(R.id.spinnerZupanije);
        spinnerIspad = ispadDetaljiView.findViewById(R.id.spinnerIspad);
        ArrayAdapter<Zupanija> adapterSpinnerZupanije = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, lZupanije);
        final ArrayAdapter<SifrarnikVrstaIspada>adapterSpinnerVrstaIspada = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, lVrsteIspada);

        adapterSpinnerZupanije.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerVrstaIspada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZupanija.setAdapter(adapterSpinnerZupanije);
        spinnerIspad.setAdapter(adapterSpinnerVrstaIspada);


        pocetakPicker = ispadDetaljiView.findViewById(R.id.fdPickerPocetak);
        krajPicker = ispadDetaljiView.findViewById(R.id.fdPickerKraj);
        resetBtn = ispadDetaljiView.findViewById(R.id.resetFilterBtn);
        filterBtn = ispadDetaljiView.findViewById(R.id.filterBtn);

        OnMenuClickItem();

    }

    public void OnMenuClickItem()
    {
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                alertDialog.show();
                searchView.setQuery("", true);
                searchItem.collapseActionView();

                if (datumP != null) {
                    pocetakPicker.setText(datumP);
                }

                if (datumK != null) {
                    krajPicker.setText(datumK);
                }

                pocetakPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dateIntHelper = 0;
                        ShowDatePickerDialog();

                    }
                });

                krajPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dateIntHelper = 1;
                        ShowDatePickerDialog();
                    }
                });

                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zupanija = spinnerZupanija.getSelectedItemPosition();
                        vrstaIspada = spinnerIspad.getSelectedItemPosition();

                        rijeseniIspadiFragment.GetFilter(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(), pocetakPicker.getText().toString(), krajPicker.getText().toString());
                        trenutniIspadiFragment.GetFilter(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(), pocetakPicker.getText().toString(), krajPicker.getText().toString());
                        alertDialog.dismiss();
                    }
                });

                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinnerZupanija.setSelection(0);
                        spinnerIspad.setSelection(0);
                        pocetakPicker.setText(R.string.odaberite_datum);
                        krajPicker.setText(R.string.odaberite_datum);
                        datumP = null;
                        datumK = null;

                        zupanija = spinnerZupanija.getSelectedItemPosition();
                        vrstaIspada = spinnerIspad.getSelectedItemPosition();


                        rijeseniIspadiFragment.GetFilter(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(), pocetakPicker.getText().toString(), krajPicker.getText().toString());
                        trenutniIspadiFragment.GetFilter(spinnerZupanija.getSelectedItem().toString(), spinnerIspad.getSelectedItem().toString(), pocetakPicker.getText().toString(), krajPicker.getText().toString());
                        alertDialog.dismiss();


                    }
                });
                spinnerZupanija.setSelection(zupanija);
                spinnerIspad.setSelection(vrstaIspada);
                return false;
            }
        });
    }

    public void ShowDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat df = new DecimalFormat("00");
        int dan = dayOfMonth;
        int mon = month + 1;
        String sDan = df.format(dan);
        String mont = df.format(mon);


        String date = sDan + "." + mont + "."+ year + ".";
        SetDate(date);
    }

    public void SetDate(String date)
    {
        if(dateIntHelper == 0)
        {
            pocetakPicker.setText(date);
            if(filterReset)
            {
                datumP =null;
            }else {
                datumP = date;
            }
        }
        else
        {
            krajPicker.setText(date);
            if(filterReset)
            {
                datumK = null;
            } else{
                datumK = date;
            }
        }
    }
}
