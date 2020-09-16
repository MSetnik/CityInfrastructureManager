package com.example.cityinfrastrukturemanager.Fragment;

import android.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityinfrastrukturemanager.Activity.MainActivity;
import com.example.cityinfrastrukturemanager.Activity.MapsActivity;
import com.example.cityinfrastrukturemanager.Adapter.IspadiRecyclerviewAdapter;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;


public class RijeseniIspadiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyApp";
    private ArrayList<IspadPrikaz> lRijeseniIspadi = new ArrayList<>();
    public RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;
    public IspadiRecyclerviewAdapter recyclerviewAdapterR;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private MyViewModel viewModel;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity()!= null)
        {
            viewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        }
        this.lRijeseniIspadi = viewModel.DohvatiRijeseneIspade();
    }

    public void GetFilter(String zupanija, String vrstaIspada, String datumPocetak, String datumKraj)
    {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();
        filter.clear();



        filter =  FilterLogic(zupanija, vrstaIspada, datumPocetak, datumKraj, lRijeseniIspadi);

        recyclerviewAdapterR = new IspadiRecyclerviewAdapter(getContext(), filter);
        recyclerView.setAdapter(recyclerviewAdapterR);

        OnIspadClick();
    }

    public ArrayList<IspadPrikaz> FilterLogic(String spinnerZupanija, String spinnerVrstaIspada, String datumPocetak, String datumKraj, ArrayList<IspadPrikaz>lRijeseniIspadi) {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();
        String dateReversePocetak = datumPocetak;

        String dateReverseKraj = datumKraj;
        if (!datumPocetak.equals("Odaberite datum")) {
            String date1 = datumPocetak.replaceAll("[.]", "");
            dateReversePocetak = ReverseDate(date1);

        }

        if (!datumKraj.equals("Odaberite datum")) {
            // datumi jednakim odabranim krajem se ne prikazuju workaround
            String dateKrajString = datumKraj.replaceAll("[.]", "");

            String reverseDate = ReverseDate(dateKrajString);
            int dateInt = Integer.parseInt(reverseDate);

            dateInt = dateInt+1;

            String dateDayplusOne = String.valueOf(dateInt);
            dateReverseKraj = dateDayplusOne;
        }


        Log.d(TAG, "FilterLogic: date reverse pocetak " + dateReversePocetak);

        for (IspadPrikaz ispadPrikaz : lRijeseniIspadi) {
            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
            pocetakIspada = pocetakIspada.replaceAll("[.]", "");
            String krajIspada = GetDate(ispadPrikaz.getKraj_ispada());
            krajIspada = krajIspada.replaceAll("[.]", "");


            if (!datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada()) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    filter.add(ispadPrikaz);

                }
            } else if (datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }

            } else if (!datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada()) && spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    Log.d(TAG, "FilterLogic:  pocetak ispada ");
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada))) {
                    filter.add(ispadPrikaz);
                }
            }else if (!datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && !datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReverseKraj) >= Integer.parseInt(ReverseDate(krajIspada))) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum") && datumKraj.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else {
                filter = lRijeseniIspadi;
            }


        }
        return filter;
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
        recyclerView =  viewGroup.findViewById(R.id.IspadiRecyclerViewR);
        recyclerviewAdapterR = new IspadiRecyclerviewAdapter(getContext(), lRijeseniIspadi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerviewAdapterR);

        swipeLayout =  viewGroup.findViewById(R.id.refreshRijeseniIspadi);
        swipeLayout.setOnRefreshListener(this);

        OnIspadClick();
        return viewGroup;
    }


    @Override
    public void onRefresh() {
        ArrayList<IspadPrikaz> lIspadiR =  viewModel.DohvatiRijeseneIspade();
        if(getActivity() != null)
        {
            swipeLayout.setRefreshing(true);
            if (lIspadiR.isEmpty())
            {
                Toast.makeText(getActivity(), "Greška prilikom dohvaćanja podataka", Toast.LENGTH_SHORT).show();
            }

            ClearFilter();
            RefreshIspadiR();
            ((MainActivity)getActivity()).trenutniIspadiFragment.RefreshIspadiT();
            swipeLayout.setRefreshing(false);
        }
    }

    public void RefreshIspadiR(){
        ArrayList<IspadPrikaz> lIspadiR =  viewModel.DohvatiRijeseneIspade();
        recyclerviewAdapterR = new IspadiRecyclerviewAdapter(getContext(), lIspadiR);
        recyclerView.setAdapter(recyclerviewAdapterR);
        OnIspadClick();
    }

    private void ClearFilter()
    {
        if(getActivity()!= null)
        {
            ((MainActivity)getActivity()).spinnerZupanija.setSelection(0);
            ((MainActivity)getActivity()).spinnerIspad.setSelection(0);
            ((MainActivity)getActivity()).pocetakPicker.setText(R.string.odaberite_datum);
            ((MainActivity)getActivity()).krajPicker.setText(R.string.odaberite_datum);
        }

    }

    public void OnIspadClick()
    {
        dialogBuilder = new AlertDialog.Builder(getActivity());

        recyclerviewAdapterR.SetOnClickListenerIspadDetalji(new IspadiRecyclerviewAdapter.IspadClickListener() {
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
        String strTime = datetime.substring(11,16);
        return strTime;
    }

    public static String GetDate(String datetime) {
        String godina = datetime.substring(0,4);
        String mjesec = datetime.substring(5,7);
        String dan = datetime.substring(8,10);
        String strDate = dan+"."+mjesec+"."+godina+".";//datetime.substring(0,11);
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

}

