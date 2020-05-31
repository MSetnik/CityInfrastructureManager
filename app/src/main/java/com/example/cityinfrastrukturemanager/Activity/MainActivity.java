package com.example.cityinfrastrukturemanager.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.cityinfrastrukturemanager.Adapter.IspadiRecyclerviewAdapter;
import com.example.cityinfrastrukturemanager.DatabaseConn.DbConn;
import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.simple.parser.JSONParser;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    MaterialSearchView searchView;
    FloatingActionButton floatingButton;
    ArrayList<Ispad> lIspadi = new ArrayList<>();
    String json = "";
    private JSONParser jsonParser = new JSONParser();
    private MyViewModel viewModel;
    private DbConn dbConn;
    private RecyclerView recyclerView;
    private IspadiRecyclerviewAdapter ispadiRecyclerviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MyViewModel.class);

        ToolbarSetup();
        GoToMaps();
        //IspisiIspade();
        RecyclerViewBind();
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

       /* Date dtf = new Date();
        dtf.getTime();

        String todayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String date = "2020-05-30 15:49:10.000";


        Log.d(TAG, "RecyclerViewBind: " + todayDate + " ------ " + date);

        Date date1 =stringToDate(todayDate, "yyyy-MM-dd HH:mm:ss.SSS");
        Date date2 = stringToDate(date,"yyyy-MM-dd HH:mm:ss.SSS");

        Log.d(TAG, "RecyclerViewBind: asd1d " + date1 +" -----------" + date2);

        if (date1.compareTo(date2) > 0) {
            Log.d(TAG, "Date1 is after Date2");
        } else if (date1.compareTo(date2) < 0) {
            Log.d(TAG, "Date1 is before Date2");
        } else if (date1.compareTo(date2) == 0) {
            Log.d(TAG, "Date1 is equal to Date2");
        }*/
    }


    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


}
