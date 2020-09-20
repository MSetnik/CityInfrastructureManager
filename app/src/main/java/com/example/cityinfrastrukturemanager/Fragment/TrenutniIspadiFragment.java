package com.example.cityinfrastrukturemanager.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cityinfrastrukturemanager.Activity.MainActivity;
import com.example.cityinfrastrukturemanager.Activity.MapsActivity;
import com.example.cityinfrastrukturemanager.Adapter.IspadiRecyclerviewAdapter;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class TrenutniIspadiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyApp";
    private ArrayList<IspadPrikaz> lTrenutniIspadi = new ArrayList<>();
    public RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;
    public IspadiRecyclerviewAdapter recyclerviewAdapterT;
    private MyViewModel viewModel;
    private SwipeRefreshLayout swipeLayout;
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null)
        {
            viewModel =((MainActivity)getActivity()).viewModel;
        }
        this.lTrenutniIspadi = viewModel.DohvatiTrenutneIspade();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_trenutni_ispadi, container, false);
        recyclerView =  viewGroup.findViewById(R.id.IspadiRecyclerView);
        recyclerviewAdapterT = new IspadiRecyclerviewAdapter(getContext(), lTrenutniIspadi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerviewAdapterT);

        swipeLayout =  viewGroup.findViewById(R.id.refresh);
        swipeLayout.setOnRefreshListener(this);
        OnIspadClick();
        return viewGroup;
    }

    public void GetFilter(String zupanija, String vrstaIspada, String datumPocetak, String datumKraj)
    {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();
        filter.clear();

        filter =  viewModel.FilterLogic(zupanija, vrstaIspada, datumPocetak, datumKraj, lTrenutniIspadi);

        recyclerviewAdapterT = new IspadiRecyclerviewAdapter(getContext(), filter);
        recyclerView.setAdapter(recyclerviewAdapterT);

        OnIspadClick();
    }


    @Override
    public void onRefresh() {
        ArrayList<IspadPrikaz> lIspadiT = viewModel.DohvatiTrenutneIspade();
        if(getActivity() != null)
        {
            if (lIspadiT.isEmpty()) {
                Toast.makeText(getActivity(), "Greška prilikom dohvaćanja podataka", Toast.LENGTH_SHORT).show();
            }

            ClearFilter();

            RefreshIspadiT();
            ((MainActivity)getActivity()).rijeseniIspadiFragment.RefreshIspadiR();
            swipeLayout.setRefreshing(false);
        }

    }

    public void RefreshIspadiT()
    {
        ArrayList<IspadPrikaz> lIspadiT = viewModel.DohvatiTrenutneIspade();
        recyclerviewAdapterT = new IspadiRecyclerviewAdapter(getContext(), lIspadiT);
        recyclerView.setAdapter(recyclerviewAdapterT);
        OnIspadClick();
    }

    private void ClearFilter()
    {
        if(getActivity() != null)
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

        recyclerviewAdapterT.SetOnClickListenerIspadDetalji(new IspadiRecyclerviewAdapter.IspadClickListener() {
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

                grad.setText(ispadiPrikaz.getGrad());
                zupanija.setText(ispadiPrikaz.getZupanija());
                vrstaIspada.setText(ispadiPrikaz.getVrstaIspada());
                opisIspada.setText(ispadiPrikaz.getOpis());

                String pocetakVrijeme = viewModel.GetTime(ispadiPrikaz.getPocetak_ispada());
                String pocetakDatum = viewModel.GetTime(ispadiPrikaz.getPocetak_ispada());


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
}