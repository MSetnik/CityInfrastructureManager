package com.example.cityinfrastrukturemanager.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.cityinfrastrukturemanager.DatabaseConn.DbConn;
import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyViewModel extends AndroidViewModel {
    private DbConn dbConn;
    private String json ="";
    private ArrayList<IspadPrikaz>lIspadi = new ArrayList<>();
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG = "MyApp";

    ///Context is Application instead context because view model can outlive activity (memory leak)
    public MyViewModel(@NonNull Application application) {
        super(application);

        DohvatiIspade();
    }

    private void DohvatiIspade()
    {
        dbConn = new DbConn(getApplication(),"prikazi_ispade_ispis" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<IspadPrikaz> SpremiIspade()
    {
        try {
            JSONArray jsonArray=(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int ispadID = Integer.parseInt(jsonObject.get("id_ispad").toString());
                String ime = String.valueOf(jsonObject.get("ime"));
                String prezime = String.valueOf(jsonObject.get("prezime"));
                String vrstaIspada = String.valueOf(jsonObject.get("vrsta_ispada"));
                String grad = String.valueOf(jsonObject.get("grad"));
                String zupanija = String.valueOf(jsonObject.get("zupanija"));
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada = jsonObject.get("kraj_ispada").toString();
                String opis = jsonObject.get("opis").toString();

                IspadPrikaz ispadPrikaz = new IspadPrikaz(ispadID, ime, prezime, vrstaIspada, grad, zupanija, pocetakIspada, krajIspada, opis);
                lIspadi.add(ispadPrikaz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lIspadi;
    }


}
