package com.example.cityinfrastrukturemanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cityinfrastrukturemanager.Adapter.ViewPagerAdapter;
import com.example.cityinfrastrukturemanager.Fragment.RijeseniIspadiFragment;
import com.example.cityinfrastrukturemanager.Fragment.TrenutniIspadiFragment;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;
import com.example.cityinfrastrukturemanager.ViewModel.MyViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    private FloatingActionButton floatingButton;
    private ArrayList<IspadPrikaz> lSviIspadi = new ArrayList<>();
    private MyViewModel viewModel;
    private Dialog dialog;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MyViewModel.class);

        ToolbarSetup();
        GoToMaps();
        ViewPagerImplementation();
        dialog = new Dialog(this);
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
        lSviIspadi = viewModel.DohvatiSveIspade();
        final ArrayList<IspadPrikaz>lTrenutniIspadi = viewModel.DohvatiTrenutneIspade();
        floatingButton = findViewById(R.id.MapsFloatingButton);
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

        list.add(new TrenutniIspadiFragment());
        list.add(new RijeseniIspadiFragment());

        lFragmentTitle.add("Trenutni ispadi");
        lFragmentTitle.add("Riješeni ispadi");

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), list, lFragmentTitle);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
