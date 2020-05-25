package com.example.cityinfrastrukturemanager.DatabaseConn;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.cityinfrastrukturemanager.Model.Ispad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbConn extends AsyncTask<String, Void, String> {
    AlertDialog alertDialog;
    private Context context;
    private String action_id;

    public DbConn (Context ctx, String sActionId)
    {
        action_id = sActionId;
        context = ctx;
    }
    @Override
    protected String doInBackground(String... strings) {
        //String db_url = "http://student.vsmti.hr/dpersic/PIS_KV/dbc.php";
        String db_url = "http://student.vsmti.hr/dpersic/PIS_KV/json.php?action="+action_id;
        String type = strings[0];

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
            return result;
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
        alertDialog.setMessage(result);
        alertDialog.show();
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
