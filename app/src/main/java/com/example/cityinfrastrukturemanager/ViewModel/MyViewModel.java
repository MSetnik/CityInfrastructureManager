package com.example.cityinfrastrukturemanager.ViewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
    private ArrayList<IspadPrikaz>lRijeseniIspadi = new ArrayList<>();
    private DbConn dbConn;
    private String json ="";
    private ArrayList<IspadPrikaz>lTrenutniIspadi = new ArrayList<>();
    private ArrayList<IspadPrikaz>lSviIspadi = new ArrayList<>();
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG = "MyApp";
    private int errorCounter = 0;
    private String greška = "Greška prilikom dohvaćanja podataka sa servera. Provjerite svoju internetsku vezu.";

    ///Context is Application instead context because view model can outlive activity (memory leak)
    public MyViewModel(@NonNull Application application) {
        super(application);

        //DohvatiRijeseneIspade();
        //DohvatiTrenutneIspade();
        //DohvatiSveIspade();
    }


    //poziva se u main activitiju
    public ArrayList<IspadPrikaz> DohvatiTrenutneIspade()
    {
        dbConn = new DbConn(getApplication(),"prikazi_trenutne_ispade" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SpremiTrenutneIspade();
    }

    private ArrayList<IspadPrikaz> SpremiTrenutneIspade()
    {
        lTrenutniIspadi.clear();
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
                float lat =Float.parseFloat(jsonObject.get("lat").toString());
                float lng = Float.parseFloat(jsonObject.get("lng").toString());
                String zupanija = String.valueOf(jsonObject.get("zupanija"));
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada;
                if(jsonObject.get("kraj_ispada") == null)
                {
                    krajIspada = "";
                }
                else
                {
                    krajIspada = jsonObject.get("kraj_ispada").toString();
                }
                String opis = jsonObject.get("opis").toString();
                String status = jsonObject.get("status").toString();

                IspadPrikaz ispadPrikaz = new IspadPrikaz(ispadID, ime, prezime, vrstaIspada, grad, lat, lng ,zupanija, pocetakIspada, krajIspada, opis,status );
                lTrenutniIspadi.add(ispadPrikaz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greška, Toast.LENGTH_SHORT).show();
                errorCounter++;
            }
        }
        return lTrenutniIspadi;
    }

    //poziva se u main activitiju
    public ArrayList<IspadPrikaz> DohvatiRijeseneIspade()
    {
        dbConn = new DbConn(getApplication(),"prikazi_rijesene_ispade" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SpremiRijeseneIspade();

    }

    private ArrayList<IspadPrikaz> SpremiRijeseneIspade()
    {
        lRijeseniIspadi.clear();
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
                float lat =Float.parseFloat(jsonObject.get("lat").toString());
                float lng = Float.parseFloat(jsonObject.get("lng").toString());
                String zupanija = String.valueOf(jsonObject.get("zupanija"));
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada;
                if(jsonObject.get("kraj_ispada") == null)
                {
                    krajIspada = "";
                }
                else
                {
                    krajIspada = jsonObject.get("kraj_ispada").toString();
                }
                String opis = jsonObject.get("opis").toString();
                String status = jsonObject.get("status").toString();

                IspadPrikaz ispadPrikaz = new IspadPrikaz(ispadID, ime, prezime, vrstaIspada, grad, lat, lng ,zupanija, pocetakIspada, krajIspada, opis,status );
                lRijeseniIspadi.add(ispadPrikaz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greška, Toast.LENGTH_SHORT).show();
                errorCounter++;
            }
        }
        return lRijeseniIspadi;
    }


    public ArrayList<IspadPrikaz> DohvatiSveIspade()
    {
        dbConn = new DbConn(getApplication(),"prikazi_ispade_ispis" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SpremiSveIspade();

    }

    private ArrayList<IspadPrikaz> SpremiSveIspade()
    {
        lSviIspadi.clear();
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
                float lat =Float.parseFloat(jsonObject.get("lat").toString());
                float lng = Float.parseFloat(jsonObject.get("lng").toString());
                String zupanija = String.valueOf(jsonObject.get("zupanija"));
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada;
                if(jsonObject.get("kraj_ispada") == null)
                {
                    krajIspada = "";
                }
                else
                {
                    krajIspada = jsonObject.get("kraj_ispada").toString();
                }
                String opis = jsonObject.get("opis").toString();
                String status = jsonObject.get("status").toString();

                IspadPrikaz ispadPrikaz = new IspadPrikaz(ispadID, ime, prezime, vrstaIspada, grad, lat, lng ,zupanija, pocetakIspada, krajIspada, opis,status );
                lSviIspadi.add(ispadPrikaz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greška, Toast.LENGTH_LONG).show();
                errorCounter++;
            }
        }
        return lSviIspadi;
    }




}
