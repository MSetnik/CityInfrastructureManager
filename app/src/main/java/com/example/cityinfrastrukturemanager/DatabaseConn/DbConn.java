package com.example.cityinfrastrukturemanager.DatabaseConn;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DbConn extends AsyncTask<Void, Void, String> {
    private static final String TAG = "MyApp";

    private String action_id;

    public DbConn (String sActionId)
    {
        action_id = sActionId;
    }
    @Override
    protected String doInBackground(Void... voids) {
        String db_url = "http://student.vsmti.hr/dpersic/PIS_KV/json.php?action="+action_id;


        try {
            URL url = new URL(db_url);
            HttpURLConnection httpURLConnection  = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            //Šaljem action na bazu

            // dohvacam podatke od actiona
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                result+= line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "Greška prilikom dohvaćanja podataka sa servera";
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        String uspjeh ="Uspjeh";

    }
}
