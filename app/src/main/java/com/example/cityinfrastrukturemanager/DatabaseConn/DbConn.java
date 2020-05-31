package com.example.cityinfrastrukturemanager.DatabaseConn;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.cityinfrastrukturemanager.Model.Grad;
import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.Korisnik;
import com.example.cityinfrastrukturemanager.Model.SifrarnikVrstaIspada;
import com.example.cityinfrastrukturemanager.Model.Zupanija;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DbConn extends AsyncTask<Void, Void, String> {
    private static final String TAG = "MyApp";

    private AlertDialog alertDialog;
    private Context context;
    private String action_id;
    private ArrayList<Ispad> lIspad = new ArrayList<>();
    private ArrayList<Korisnik> lKorisnici = new ArrayList<>();
    private ArrayList<Grad> lGradovi = new ArrayList<>();
    private ArrayList<Zupanija> lZupanije = new ArrayList<>();
    private ArrayList<SifrarnikVrstaIspada> lSifrarnikVrsteIspada = new ArrayList<>();
    private JSONParser jsonParser = new JSONParser();
    private String json = "";

    public DbConn (Context ctx, String sActionId)
    {
        action_id = sActionId;
        context = ctx;
    }
    @Override
    protected String doInBackground(Void... voids) {
        //String db_url = "http://student.vsmti.hr/dpersic/PIS_KV/dbc.php";
        String db_url = "http://student.vsmti.hr/dpersic/PIS_KV/json.php?action="+action_id;
        //String type = strings[0];

        try {
            URL url = new URL(db_url);
            HttpURLConnection httpURLConnection  = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            //Šaljem action na bazu
           /* OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("req", "UTF-8")+"="+URLEncoder.encode(action_id, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();*/
            // dohvacam podatke od actiona
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line ="";
            while ((line = bufferedReader.readLine()) != null)
            {
                result+= line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            json = result;
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        String uspjeh ="Uspjeh";
        //SpremiIspade();
        //SpremiIspade(result);
        //listener.OnFinish(lIspad);
        //Log.d(TAG, "SpremiUListu: On post execute" + result);
        //ArrayList<Ispad>ISPADI = SpremiIspade(result);

        //listener.OnFinish(ISPADI);
        /*for (Ispad ispad : ISPADI)
        {
            Log.d(TAG, "SpremiUListu: "+ispad.getId_grad() + " " + ispad.getId_korisnik());
        }*/
        /*alertDialog.setMessage(result);
        alertDialog.show();*/

    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    public ArrayList<Ispad> SpremiIspade()
    {
        try {
            JSONArray jsonArray=(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int ispadID = Integer.parseInt(jsonObject.get("id_ispad").toString());
                int korisnikID =  Integer.parseInt(jsonObject.get("id_korisnik").toString());
                int vrstaIspadaID = Integer.parseInt(jsonObject.get("id_vrsta_ispada").toString());
                int gradID =  Integer.parseInt(jsonObject.get("id_grad").toString());
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada = jsonObject.get("kraj_ispada").toString();
                String opis = jsonObject.get("opis").toString();

                Ispad ispad = new Ispad(ispadID, korisnikID, vrstaIspadaID, gradID, pocetakIspada, krajIspada, opis);
                lIspad.add(ispad);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return lIspad;
    }

    public ArrayList<Ispad> DohvatiIspade()
    {
        return lIspad;
    }

    public List<Korisnik> SpremiKorisnike()
    {

        try {
            JSONArray jsonArray =(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int korisnikID =Integer.parseInt(jsonObject.get("id_ispad").toString());
                String korisnicko_ime = jsonObject.get("id_korisnik").toString();
                String lozinka = jsonObject.get("id_vrsta_ispada").toString();
                String ime =  jsonObject.get("id_grad").toString();
                String prezime = jsonObject.get("pocetak_ispada").toString();


                Korisnik korisnik = new Korisnik(korisnikID, korisnicko_ime, lozinka, ime, prezime);
                lKorisnici.add(korisnik);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lKorisnici;
    }

    /*public List<Ispad> SpremiGradove()
    {

        try {
            JSONArray jsonArray =(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int ispadID = Integer.parseInt(jsonObject.get("id_ispad").toString());
                int korisnikID =  Integer.parseInt(jsonObject.get("id_korisnik").toString());
                int vrstaIspadaID = Integer.parseInt(jsonObject.get("id_vrsta_ispada").toString());
                int gradID =  Integer.parseInt(jsonObject.get("id_grad").toString());
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada = jsonObject.get("kraj_ispada").toString();
                String opis = jsonObject.get("opis").toString();

                Ispad ispad = new Ispad(ispadID, korisnikID, vrstaIspadaID, gradID, pocetakIspada, krajIspada, opis);
                lIspad.add(ispad);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lIspad;
    }

    public List<Ispad> SpremiZupanije()
    {

        try {
            JSONArray jsonArray =(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int ispadID = Integer.parseInt(jsonObject.get("id_ispad").toString());
                int korisnikID =  Integer.parseInt(jsonObject.get("id_korisnik").toString());
                int vrstaIspadaID = Integer.parseInt(jsonObject.get("id_vrsta_ispada").toString());
                int gradID =  Integer.parseInt(jsonObject.get("id_grad").toString());
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada = jsonObject.get("kraj_ispada").toString();
                String opis = jsonObject.get("opis").toString();

                Ispad ispad = new Ispad(ispadID, korisnikID, vrstaIspadaID, gradID, pocetakIspada, krajIspada, opis);
                lIspad.add(ispad);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lIspad;
    }

    public List<Ispad> SpremiSifrarnikIspada()
    {

        try {
            JSONArray jsonArray =(JSONArray) jsonParser.parse(json);

            for (int i=0;i<jsonArray.size();i++)
            {
                //korištena druga biblioteka (json-simple) pa je zato dugacak naziv
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonArray.get(i);
                int ispadID = Integer.parseInt(jsonObject.get("id_ispad").toString());
                int korisnikID =  Integer.parseInt(jsonObject.get("id_korisnik").toString());
                int vrstaIspadaID = Integer.parseInt(jsonObject.get("id_vrsta_ispada").toString());
                int gradID =  Integer.parseInt(jsonObject.get("id_grad").toString());
                String pocetakIspada = jsonObject.get("pocetak_ispada").toString();
                String krajIspada = jsonObject.get("kraj_ispada").toString();
                String opis = jsonObject.get("opis").toString();

                Ispad ispad = new Ispad(ispadID, korisnikID, vrstaIspadaID, gradID, pocetakIspada, krajIspada, opis);
                lIspad.add(ispad);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lIspad;
    }
*/
}
