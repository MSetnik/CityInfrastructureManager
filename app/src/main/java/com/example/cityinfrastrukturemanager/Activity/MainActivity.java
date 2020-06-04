package com.example.cityinfrastrukturemanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.cityinfrastrukturemanager.Adapter.IspadiRecyclerviewAdapter;
import com.example.cityinfrastrukturemanager.DatabaseConn.DbConn;
import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    MaterialSearchView searchView;
    FloatingActionButton floatingButton;
    ArrayList<Ispad> lIspadi = new ArrayList<>();
    private MyViewModel viewModel;
    private RecyclerView recyclerView;
    private IspadiRecyclerviewAdapter ispadiRecyclerviewAdapter;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MyViewModel.class);

        ToolbarSetup();
        GoToMaps();
        //IspisiIspade();
        RecyclerViewBind();
        OnIspadClick();

        dialog = new Dialog(this);
    }
    
    
    private void ToolbarSetup()
    {
        Toolbar toolbar = findViewById(R.id.toolbar_include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.search);
        toolbar.setTitleTextColor(getResources().getColor(R.color.ToolbarText));

        searchView = findViewById(R.id.Search_mainActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    private void GoToMaps()
    {
        floatingButton = findViewById(R.id.MapsFloatingButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });
    }


    private ArrayList<IspadPrikaz> DohvatiIspade()
    {
        ArrayList<IspadPrikaz> lIspadi = viewModel.SpremiIspade();
        return lIspadi;

    }

    private void RecyclerViewBind()
    {
        recyclerView = findViewById(R.id.IspadiRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ispadiRecyclerviewAdapter = new IspadiRecyclerviewAdapter(this, DohvatiIspade());
        recyclerView.setAdapter(ispadiRecyclerviewAdapter);
    }

    public void OnIspadClick()
    {
        dialogBuilder = new AlertDialog.Builder(this);

        ispadiRecyclerviewAdapter.SetOnClickListenerIspadDetalji(new IspadiRecyclerviewAdapter.IspadClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onIspadClick(IspadPrikaz ispadiPrikaz) {
                final View ispadDetaljiView = getLayoutInflater().inflate(R.layout.ispad_detalji_cardview, null);
                TextView grad = ispadDetaljiView.findViewById(R.id.gradNaziv_ispad_detalji_CV);
                TextView zupanija = ispadDetaljiView.findViewById(R.id.zupanija_ispad_detalji_CV);
                TextView vrstaIspada = ispadDetaljiView.findViewById(R.id.vrstaIspada_ispad_detalji_CV);
                TextView opisIspada = ispadDetaljiView.findViewById(R.id.opisIspada_ispad_detalji_CV);
                TextView vrijemeIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaVrijeme_ispad_detalji_CV);
                TextView datumIspada = ispadDetaljiView.findViewById(R.id.prijavaProblemaDatum_ispad_detalji_CV);
                Button maps = ispadDetaljiView.findViewById(R.id.btnMaps_ispad_detalji_CV);

               /* Bundle bundle =  getIntent().getExtras();
                ispadPrikaz = (IspadPrikaz) bundle.getSerializable("ispad");*/

                grad.setText(ispadiPrikaz.getGrad());
                zupanija.setText(ispadiPrikaz.getZupanija());
                vrstaIspada.setText(ispadiPrikaz.getVrstaIspada());
                opisIspada.setText(ispadiPrikaz.getOpis());

                String pocetakVrijeme = GetTime(ispadiPrikaz.getPocetak_ispada());
                String pocetakDatum = GetDate(ispadiPrikaz.getPocetak_ispada());

                vrijemeIspada.setText(pocetakVrijeme);
                datumIspada.setText(pocetakDatum);
                /*Intent intent = new Intent(MainActivity.this, IspadDetaljiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ispad", (Serializable) ispadiPrikaz);
                intent.putExtras(bundle);
                startActivity(intent);*/
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

}
