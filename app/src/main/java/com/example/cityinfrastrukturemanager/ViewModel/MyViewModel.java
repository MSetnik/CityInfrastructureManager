package com.example.cityinfrastrukturemanager.ViewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.cityinfrastrukturemanager.DatabaseConn.DbConn;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.Model.SifrarnikVrstaIspada;
import com.example.cityinfrastrukturemanager.Model.Zupanija;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyViewModel extends AndroidViewModel {
    private ArrayList<IspadPrikaz> lIspadi = new ArrayList<>();
    private DbConn dbConn;
    private String json ="";
    private ArrayList<IspadPrikaz>lTrenutniIspadi = new ArrayList<>();
    private ArrayList<IspadPrikaz>lSviIspadi = new ArrayList<>();
    private ArrayList<Zupanija>lZupanije = new ArrayList<>();
    private ArrayList<SifrarnikVrstaIspada> lVrsteIspada = new ArrayList<>();
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG = "MyApp";
    private int errorCounter = 0;
    private String greska = "Greška prilikom dohvaćanja podataka sa servera. Provjerite svoju internetsku vezu.";

    ///Context is Application instead context because view model can outlive activity (memory leak)
    public MyViewModel(@NonNull Application application) {
        super(application);
    }


    //poziva se u main activitiju
    public ArrayList<IspadPrikaz> DohvatiTrenutneIspade()
    {
        dbConn = new DbConn("prikazi_trenutne_ispade" );

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
                Toast.makeText(getApplication(), greska, Toast.LENGTH_SHORT).show();
                errorCounter++;
            }
        }
        return lTrenutniIspadi;
    }

    //poziva se u main activitiju
    public ArrayList<IspadPrikaz> DohvatiRijeseneIspade()
    {
        dbConn = new DbConn("prikazi_rijesene_ispade" );

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
        lIspadi.clear();
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
                lIspadi.add(ispadPrikaz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greska, Toast.LENGTH_SHORT).show();
                errorCounter++;
            }
        }
        return lIspadi;
    }


    public ArrayList<IspadPrikaz> DohvatiSveIspade()
    {
        dbConn = new DbConn("prikazi_ispade_ispis" );

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
                Toast.makeText(getApplication(), greska, Toast.LENGTH_LONG).show();
                errorCounter++;
            }
        }
        return lSviIspadi;
    }


    public ArrayList<Zupanija> DohvatiZupanije()
    {
        dbConn = new DbConn("prikazi_zupanije_android" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SpremiZupanije();

    }

    private ArrayList<Zupanija> SpremiZupanije()
    {
        lZupanije.clear();
        Zupanija zupanija = new Zupanija(0, "Sve županije");
        lZupanije.add(zupanija);
        try {
            JSONArray jsonArray=(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int IDzupanije = Integer.parseInt(String.valueOf(jsonObject.get("id_zupanija")));
                String nazivZupanije = String.valueOf(jsonObject.get("naziv_zupanije"));

                Zupanija zupanija2 = new Zupanija(IDzupanije,nazivZupanije);
                lZupanije.add(zupanija2);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greska, Toast.LENGTH_LONG).show();
                errorCounter++;
            }
        }
        return lZupanije;
    }



    public ArrayList<SifrarnikVrstaIspada> DohvatiVrsteIspade()
    {
        dbConn = new DbConn("prikazi_vrste_ispada_android" );

        try {
            json = dbConn.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SpremiVrsteIspada();

    }

    private ArrayList<SifrarnikVrstaIspada> SpremiVrsteIspada()
    {
        lVrsteIspada.clear();
        SifrarnikVrstaIspada sifrarnikVrstaIspada = new SifrarnikVrstaIspada(0, "Svi ispadi");
        lVrsteIspada.add(sifrarnikVrstaIspada);
        try {
            JSONArray jsonArray=(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int vrstaIspadaID = Integer.parseInt(jsonObject.get("id_vrsta_ispada").toString());
                String vrstaIspada = String.valueOf(jsonObject.get("vrsta_ispada"));

                SifrarnikVrstaIspada sifrarnikVrstaIspada2 = new SifrarnikVrstaIspada(vrstaIspadaID,vrstaIspada );
                lVrsteIspada.add(sifrarnikVrstaIspada2);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            if (errorCounter == 0)
            {
                Toast.makeText(getApplication(), greska, Toast.LENGTH_LONG).show();
                errorCounter++;
            }
        }
        return lVrsteIspada;
    }


    public ArrayList<IspadPrikaz> FilterMapsLogic(String spinnerZupanija, String spinnerVrstaIspada, String datumPocetak, ArrayList<IspadPrikaz>lIspadPrikaz) {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();
        String dateReversePocetak = datumPocetak;

        if (!datumPocetak.equals("Odaberite datum")) {
            String date = datumPocetak.replaceAll("[.]", "");
            dateReversePocetak = ReverseDate(date);
        }


        for (IspadPrikaz ispadPrikaz : lIspadPrikaz) {
            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
            pocetakIspada = pocetakIspada.replaceAll("[.]", "");

            if (!datumPocetak.equals("Odaberite datum")  && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada()) && Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {

                    Log.d(TAG, "FilterLogic: if 1 ");
                    filter.add(ispadPrikaz);

                }
            } else if (datumPocetak.equals("Odaberite datum")  && !spinnerZupanija.toString().equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if ( spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.toString().equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }

            } else if (!datumPocetak.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada)) && spinnerZupanija.equals(ispadPrikaz.getZupanija()) && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))  && spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum") && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))&& spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum")  && !spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if (spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada()) && spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum")  && spinnerZupanija.equals("Sve županije") && !spinnerVrstaIspada.equals("Svi ispadi")) {
                if ( spinnerVrstaIspada.equals(ispadPrikaz.getVrstaIspada())) {
                    filter.add(ispadPrikaz);
                }
            } else if (datumPocetak.equals("Odaberite datum")  && !spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if ( spinnerZupanija.equals(ispadPrikaz.getZupanija())) {
                    filter.add(ispadPrikaz);
                }
            } else if (!datumPocetak.equals("Odaberite datum")  && spinnerZupanija.equals("Sve županije") && spinnerVrstaIspada.equals("Svi ispadi")) {
                if (Integer.parseInt(dateReversePocetak) <= Integer.parseInt(ReverseDate(pocetakIspada))) {
                    filter.add(ispadPrikaz);
                }
            } else {
                filter = lIspadPrikaz;
            }
        }
        return filter;
    }


    public String GetDate(String datetime) {
        Log.d(TAG, "GetDate: " + datetime);
        String godina = datetime.substring(0,4);
        String mjesec = datetime.substring(5,7);
        String dan = datetime.substring(8,10);
        String strDate = dan+"."+mjesec+"."+godina;
        return strDate;
    }


    private String ReverseDate(String date)
    {
        String godina = date.substring(4,8);
        String mjesec = date.substring(2,4);
        String dan = date.substring(0,2);
        String strDate = godina+mjesec+dan;//datetime.substring(0,11);
        return strDate;
    }

    public String GetTime(String datetime) {
        String strTime = datetime.substring(11,16);
        return strTime;
    }


    public ArrayList<IspadPrikaz> FilterLogic(String spinnerZupanija, String spinnerVrstaIspada, String datumPocetak, String datumKraj, ArrayList<IspadPrikaz>lIspadPrikaz) {
        ArrayList<IspadPrikaz> filter = new ArrayList<>();
        filter.clear();
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

        for (IspadPrikaz ispadPrikaz : lIspadPrikaz) {
            String pocetakIspada = GetDate(ispadPrikaz.getPocetak_ispada());
            String krajIspada = "99999999";
            if(!ispadPrikaz.getKraj_ispada().equals(""))
            {
                krajIspada = GetDate(ispadPrikaz.getKraj_ispada());
                krajIspada = krajIspada.replaceAll("[.]", "");
            }
            Log.d(TAG, "FilterLogic: ispad prikaz filter "  + pocetakIspada + " " + krajIspada);
            pocetakIspada = pocetakIspada.replaceAll("[.]", "");


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
                filter = lIspadPrikaz;
            }
        }
        return filter;
    }

}
