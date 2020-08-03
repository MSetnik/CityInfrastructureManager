package com.example.cityinfrastrukturemanager.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


public class RijeseniIspadiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MyApp";
    private ArrayList<IspadPrikaz> lRijeseniIspadi = new ArrayList<>();
    private ArrayList<Zupanija>lZupanije = new ArrayList<>();
    private ArrayList<SifrarnikVrstaIspada> lVrsteIspada = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;
    private IspadiRecyclerviewAdapter recyclerviewAdapter;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private String title;
    private MyViewModel viewModel;
    private SwipeRefreshLayout swipeLayout;
    private MenuItem searchItem;
    private TextView txtPocetak;
    private TextView pocetakPicker;
    private TextView txtKraj;
    private TextView krajPicker;
    private int dateIntHelper;
    private Button filterBtn;
    private Spinner spinnerIspad;
    private Spinner spinnerZupanija;
    boolean ifOdabir;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        this.lRijeseniIspadi = viewModel.DohvatiRijeseneIspade();
        this.lZupanije = viewModel.DohvatiZupanije();
        this.lVrsteIspada = viewModel.DohvatiVrsteIspade();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        final MenuItem filterItem = menu.findItem(R.id.filterSearch);
        MenuItem infoItem = menu.findItem(R.id.info);

        searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.filter_dialog_rijeseni_ispadi, null);
                alertDialog.setView(ispadDetaljiView);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                spinnerZupanija = ispadDetaljiView.findViewById(R.id.spinnerZupanije);
                spinnerIspad = ispadDetaljiView.findViewById(R.id.spinnerIspad);
                ArrayAdapter<Zupanija>adapterSpinnerZupanije = new ArrayAdapter<Zupanija>(getActivity(), android.R.layout.simple_spinner_item, lZupanije);
                ArrayAdapter<SifrarnikVrstaIspada>adapterSpinnerVrstaIspada = new ArrayAdapter<SifrarnikVrstaIspada>(getActivity(), android.R.layout.simple_spinner_item, lVrsteIspada);

                adapterSpinnerZupanije.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterSpinnerVrstaIspada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerZupanija.setAdapter(adapterSpinnerZupanije);
                spinnerIspad.setAdapter(adapterSpinnerVrstaIspada);


                 txtPocetak = ispadDetaljiView.findViewById(R.id.fdTxtPocetak);
                 pocetakPicker = ispadDetaljiView.findViewById(R.id.fdPickerPocetak);
                 txtKraj= ispadDetaljiView.findViewById(R.id.fdTxtKraj);
                 krajPicker = ispadDetaljiView.findViewById(R.id.fdPickerKraj);
                 filterBtn = ispadDetaljiView.findViewById(R.id.filterBtn);

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
                        ArrayList<IspadPrikaz> filter = new ArrayList<>();
                        ArrayList<IspadPrikaz> filterHelper = new ArrayList<>();
                        filter.clear();


                        filter = FilterLogic(spinnerZupanija, spinnerIspad, pocetakPicker, krajPicker, lRijeseniIspadi);


                        recyclerviewAdapter = new IspadiRecyclerviewAdapter(getContext(), filter);
                        recyclerView.setAdapter(recyclerviewAdapter);
                        alertDialog.dismiss();

                        OnIspadClick();
                    }
                });

                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerviewAdapter.getFilter().filter(newText);
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

    public ArrayList<IspadPrikaz> FilterLogic(Spinner spinnerZupanija, Spinner spinnerVrstaIspada, TextView datumPocetak, TextView datumKraj, ArrayList<IspadPrikaz>lRijeseniIspadi) {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();


        String dateReversePocetak = datumPocetak.getText().toString();

        String dateReverseKraj = datumKraj.getText().toString();

        if (!datumPocetak.getText().toString().equals("Odaberite datum")) {
            String date = datumPocetak.getText().toString();
            String date1 = date.replaceAll("[.]", "");
            dateReversePocetak = ReverseDate(date1);

        }
        if (!datumKraj.getText().toString().equals("Odaberite datum")) {
//            String dateKraj = datumKraj.getText().toString();
//            String dateKraj1 = dateKraj.replaceAll("[.]", "");
//            dateReverseKraj = ReverseDate(dateKraj1);
            String dateKraj = datumKraj.getText().toString();
            // datumi jednakim odabranim krajem se ne prikazuju workaround
            String dateKrajString = dateKraj.replaceAll("[.]", "");

            String reverseDate = ReverseDate(dateKrajString);
            int dateInt = Integer.parseInt(reverseDate);

            dateInt = dateInt+1;

            String dateDayplusOne = String.valueOf(dateInt);
            dateReverseKraj = dateDayplusOne;
//            String dateKraj1 = dateDayplusOne.replaceAll("[.]", "");
            Log.d(TAG, "FilterLogic: datereverse kra " + dateDayplusOne);
        }

        //        if (!datumKraj.getText().toString().equals("Odaberite datum")) {
//            String dateKraj = datumKraj.getText().toString();
//            // datumi jednakim odabranim krajem se ne prikazuju workaround
//            String dateKrajString = dateKraj.replaceAll("[.]", "");
//            int dateInt = Integer.parseInt(dateKrajString);
//
//
//            dateInt = dateInt+1;
//
//            String dateDayplusOne = String.valueOf(dateInt);
////            String dateKraj1 = dateDayplusOne.replaceAll("[.]", "");
//            dateReverseKraj = ReverseDate(dateDayplusOne);
//        }

        for (IspadPrikaz ispadPrikaz : lRijeseniIspadi) {
            ifOdabir = false;
            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
            pocetakIspada = pocetakIspada.replaceAll("[.]", "");
            String krajIspada = GetDate(ispadPrikaz.getKraj_ispada());
            krajIspada = krajIspada.replaceAll("[.]", "");


            if (!datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada()) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(ispadPrikaz.getKraj_ispada())) && Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(ispadPrikaz.getPocetak_ispada()))) {

                    Log.d(TAG, "FilterLogic: if 1 ");
                    ifOdabir = true;
                    filter.add(ispadPrikaz);

                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }

            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReversePocetak) >= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada()) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    Log.d(TAG, "FilterLogic:  pocetak ispada ");
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && !datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada))) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && !spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (spinnerZupanija.getSelectedItem().toString().equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.getText().toString().equals("Odaberite datum") && datumKraj.getText().toString().equals("Odaberite datum") && spinnerZupanija.getSelectedItem().toString().equals("Sve županije") && !spinnerVrstaIspada.getSelectedItem().toString().equals("Svi ispadi")) {
                if (spinnerVrstaIspada.getSelectedItem().toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else {
                filter = lRijeseniIspadi;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rijeseni_ispadi, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.IspadiRecyclerViewR);
        recyclerviewAdapter = new IspadiRecyclerviewAdapter(getContext(), lRijeseniIspadi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerviewAdapter);

        swipeLayout = (SwipeRefreshLayout) viewGroup.findViewById(R.id.refreshRijeseniIspadi);
        swipeLayout.setOnRefreshListener(this);

        OnIspadClick();
        return viewGroup;
    }


    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        ArrayList<IspadPrikaz> lIspadi =  viewModel.DohvatiRijeseneIspade();
        if (lIspadi.isEmpty())
        {
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
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.ispad_rijesen_detalji_cardview, null);
                TextView grad = ispadDetaljiView.findViewById(R.id.gradNaziv_ispad_detalji_CV);
                TextView zupanija = ispadDetaljiView.findViewById(R.id.zupanija_ispad_detalji_CV);
                TextView vrstaIspada = ispadDetaljiView.findViewById(R.id.vrstaIspada_ispad_detalji_CV);
                TextView opisIspada = ispadDetaljiView.findViewById(R.id.opisIspada_ispad_detalji_CV);
                TextView vrijemeIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaVrijeme_ispad_detalji_CV);
                TextView datumIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaDatum_ispad_detalji_CV);
                TextView vrijemeZavrsetkaIspada = ispadDetaljiView.findViewById(R.id.krajProblemaVrijeme_ispad_detalji_CV);
                TextView datumZavrsetkaIspada = ispadDetaljiView.findViewById(R.id.krajProblemaDatum_ispad_detalji_CV);
                Button maps = ispadDetaljiView.findViewById(R.id.btnMaps_ispad_detalji_CV);

                grad.setText(ispadiPrikaz.getGrad());
                zupanija.setText(ispadiPrikaz.getZupanija());
                vrstaIspada.setText(ispadiPrikaz.getVrstaIspada());
                opisIspada.setText(ispadiPrikaz.getOpis());

                String pocetakVrijeme = GetTime(ispadiPrikaz.getPocetak_ispada());
                String pocetakDatum = GetDate(ispadiPrikaz.getPocetak_ispada());
                String krajVrijeme = GetTime(ispadiPrikaz.getKraj_ispada());
                String krajDatum = GetDate(ispadiPrikaz.getKraj_ispada());

                vrijemeIspada.setText(pocetakVrijeme);
                datumIspada.setText(pocetakDatum);
                vrijemeZavrsetkaIspada.setText(krajVrijeme);
                datumZavrsetkaIspada.setText(krajDatum);

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
        String strDate = dan+"."+mjesec+"."+godina;//datetime.substring(0,11);
        return strDate;
    }

    public boolean IsServicesOK()
    {
        //provjera google play servisa

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
        else
        {
            krajPicker.setText(date);
        }
    }
}