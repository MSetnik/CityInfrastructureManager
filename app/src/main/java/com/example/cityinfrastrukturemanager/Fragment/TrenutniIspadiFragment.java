package com.example.cityinfrastrukturemanager.Fragment;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cityinfrastrukturemanager.Activity.MapsActivity;
import com.example.cityinfrastrukturemanager.Adapter.IspadiRecyclerviewAdapter;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.Model.SifrarnikVrstaIspada;
import com.example.cityinfrastrukturemanager.Model.Zupanija;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrenutniIspadiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MyApp";
    private ArrayList<IspadPrikaz> lTrenutniIspadi = new ArrayList<>();
    private ArrayList<Zupanija>lZupanije = new ArrayList<>();
    private ArrayList<SifrarnikVrstaIspada> lVrsteIspada = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;
    private IspadiRecyclerviewAdapter recyclerviewAdapter;
    private MyViewModel viewModel;
    SwipeRefreshLayout swipeLayout;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private String searchText = "test";
    private MenuItem searchItem;
    private TextView pocetakPicker;
    private int dateIntHelper;
    private Button filterBtn;
    private Spinner spinnerIspad;
    private Spinner spinnerZupanija;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        this.lTrenutniIspadi = viewModel.DohvatiTrenutneIspade();
        this.lZupanije = viewModel.DohvatiZupanije();
        this.lVrsteIspada = viewModel.DohvatiVrsteIspade();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,final MenuInflater inflater) {

        inflater.inflate(R.menu.menu_list, menu);
        final MenuItem filterItem = menu.findItem(R.id.filterSearch);
        MenuItem infoItem = menu.findItem(R.id.info);

        searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.filter_dialog_trenutni_ispadi, null);
                alertDialog.setView(ispadDetaljiView);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                spinnerZupanija = ispadDetaljiView.findViewById(R.id.spinnerZupanije);
                spinnerIspad = ispadDetaljiView.findViewById(R.id.spinnerIspad);
                ArrayAdapter<Zupanija> adapterSpinnerZupanije = new ArrayAdapter<Zupanija>(getActivity(), android.R.layout.simple_spinner_item, lZupanije);
                ArrayAdapter<SifrarnikVrstaIspada>adapterSpinnerVrstaIspada = new ArrayAdapter<SifrarnikVrstaIspada>(getActivity(), android.R.layout.simple_spinner_item, lVrsteIspada);

                adapterSpinnerZupanije.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterSpinnerVrstaIspada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerZupanija.setAdapter(adapterSpinnerZupanije);
                spinnerIspad.setAdapter(adapterSpinnerVrstaIspada);

                pocetakPicker = ispadDetaljiView.findViewById(R.id.fdPickerPocetak);
                filterBtn = ispadDetaljiView.findViewById(R.id.filterBtn);

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
                        ArrayList<IspadPrikaz> filter = new ArrayList<>();
                        ArrayList<IspadPrikaz> filterHelper = new ArrayList<>();
                        filter.clear();

                        filter = FilterLogic(spinnerZupanija, spinnerIspad, pocetakPicker, lTrenutniIspadi);

                        recyclerviewAdapter = new IspadiRecyclerviewAdapter(getContext(), filter);
                        recyclerView.setAdapter(recyclerviewAdapter);
                        alertDialog.dismiss();

                        OnIspadClick();
                    }
                });

                return false;
            }
        });

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(searchText.equals(""))
                {
                    String text = "";
                    recyclerviewAdapter.getFilter().filter(text);
                }
                else
                {
                    recyclerviewAdapter.getFilter().filter(newText);
                }

                return false;
            }

        });

        infoItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.colors_info, null);
                alertDialog.setView(ispadDetaljiView);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();
                return false;
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_prikaz_ispada, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.IspadiRecyclerView);
        recyclerviewAdapter = new IspadiRecyclerviewAdapter(getContext(), lTrenutniIspadi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerviewAdapter);

        swipeLayout = (SwipeRefreshLayout) viewGroup.findViewById(R.id.refresh);
        swipeLayout.setOnRefreshListener(this);

        OnIspadClick();
        return viewGroup;
    }


    public ArrayList<IspadPrikaz> FilterLogic(Spinner spinnerZupanija, Spinner spinnerVrstaIspada, TextView datumPocetak, ArrayList<IspadPrikaz>lTrenutniIspadi) {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();


        String dateReversePocetak = datumPocetak.getText().toString();


        if (!datumPocetak.getText().toString().equals("Odaberite datum")) {
            String date = datumPocetak.getText().toString();
            String date1 = date.replaceAll("[.]", "");
            dateReversePocetak = ReverseDate(date1);

        }

        for (IspadPrikaz ispadPrikaz : lTrenutniIspadi) {
            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
            pocetakIspada = pocetakIspada.replaceAll("[.]", "");
            String krajIspada;
            Log.d(TAG, "FilterLogic: kraj ispada " + ispadPrikaz.getKraj_ispada());

            if (!datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada()) && Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    Log.d(TAG, "FilterLogic: if 1 ");
                    filter.add(ispadPrikaz);

                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if ( spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReversePocetak) >= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))&& spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if ( spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum")  && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if ( spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum")  && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    filter.add(ispadPrikaz);
                }

            } else {
                filter = lTrenutniIspadi;
            }
        }

        return filter;
    }


    private void ShowDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String ReverseDate(String date)
    {
        String godina = date.substring(4,8);
        String mjesec = date.substring(2,4);
        String dan = date.substring(0,2);
        String strDate = godina+mjesec+dan;//datetime.substring(0,11);
        return strDate;
    }


    @Override
    public void onRefresh() {
        ArrayList<IspadPrikaz> lIspadi = viewModel.DohvatiTrenutneIspade();
        if (lIspadi.isEmpty()) {
            Toast.makeText(getActivity(), "Greška prilikom dohvaćanja podataka", Toast.LENGTH_SHORT).show();
        }
        searchItem.collapseActionView();
        recyclerviewAdapter = new IspadiRecyclerviewAdapter(getContext(), lIspadi);
        recyclerView.setAdapter(recyclerviewAdapter);
        OnIspadClick();
        swipeLayout.setRefreshing(false);
    }


    public void OnIspadClick()
    {
        dialogBuilder = new AlertDialog.Builder(getActivity());

        recyclerviewAdapter.SetOnClickListenerIspadDetalji(new IspadiRecyclerviewAdapter.IspadClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onIspadClick(final IspadPrikaz ispadiPrikaz) {
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.ispad_detalji_cardview, null);
                TextView grad = ispadDetaljiView.findViewById(R.id.gradNaziv_ispad_detalji_CV);
                TextView zupanija = ispadDetaljiView.findViewById(R.id.zupanija_ispad_detalji_CV);
                TextView vrstaIspada = ispadDetaljiView.findViewById(R.id.vrstaIspada_ispad_detalji_CV);
                TextView opisIspada = ispadDetaljiView.findViewById(R.id.opisIspada_ispad_detalji_CV);
                TextView vrijemeIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaVrijeme_ispad_detalji_CV);
                TextView datumIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaDatum_ispad_detalji_CV);
                Button maps = ispadDetaljiView.findViewById(R.id.btnMaps_ispad_detalji_CV);
                Log.d(TAG, "onIspadClick: " + ispadiPrikaz.getStatus());

                grad.setText(ispadiPrikaz.getGrad());
                zupanija.setText(ispadiPrikaz.getZupanija());
                vrstaIspada.setText(ispadiPrikaz.getVrstaIspada());
                opisIspada.setText(ispadiPrikaz.getOpis());

                String pocetakVrijeme = GetTime(ispadiPrikaz.getPocetak_ispada());
                String pocetakDatum = GetDate(ispadiPrikaz.getPocetak_ispada());


                vrijemeIspada.setText(pocetakVrijeme);
                datumIspada.setText(pocetakDatum);
                final ArrayList<IspadPrikaz>lispadi = new ArrayList<>();
                lispadi.add(ispadiPrikaz);
                if(IsServicesOK())
                {
                    maps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), MapsActivity.class);
                            intent.putExtra("ispadi", lispadi);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                }

                dialogBuilder.setView(ispadDetaljiView);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
            }
        });

    }


    public static String GetTime(String datetime) {
        String strTime = datetime.substring(12,16);
        return strTime;
    }

    public static String GetDate(String datetime) {
        String godina = datetime.substring(0,4);
        String mjesec = datetime.substring(5,7);
        String dan = datetime.substring(8,10);
        String strDate = dan+"."+mjesec+"."+godina;
        return strDate;
    }

    public boolean IsServicesOK()
    {
        Log.d(TAG, "IsServicesOK: Checking google services ver.");

        int availible = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(availible == ConnectionResult.SUCCESS)
        {
            //Uspjeh
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availible))
        {
            //problem se pojavio, ali se može popraviti
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), availible, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(getActivity(), "Ne možete pristupiti karti", Toast.LENGTH_SHORT).show();
        }
        return false;
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
        }
    }
    
}