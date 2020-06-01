package com.example.cityinfrastrukturemanager.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityinfrastrukturemanager.Model.Ispad;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;

import org.w3c.dom.Text;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class IspadiRecyclerviewAdapter extends RecyclerView.Adapter<IspadiRecyclerviewAdapter.IspadiViewHolder> {
    private ArrayList<IspadPrikaz>lIspadPrikaz;
    private Context context;

    public IspadiRecyclerviewAdapter (Context context,ArrayList<IspadPrikaz> lIspadPrikaz) {
        this.lIspadPrikaz = lIspadPrikaz;
        this.context = context;
    }

    @NonNull
    @Override
    public IspadiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_cardview_layout, parent, false);
        IspadiViewHolder holder = new IspadiViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IspadiViewHolder holder, int position) {
        IspadPrikaz ispadi = lIspadPrikaz.get(position);

        String pocetakVrijeme = GetTime(ispadi.getPocetak_ispada());
        String pocetakDatum = GetDate(ispadi.getPocetak_ispada());

        String todayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String date = ispadi.getKraj_ispada();


        Date date1 =stringToDate(todayDate, "yyyy-MM-dd HH:mm:ss.SSS");
        Date date2 = stringToDate(date,"yyyy-MM-dd HH:mm:ss.SSS");

        if (date1.compareTo(date2) > 0) {
            holder.stanjeIspada.setText("Rijeseno");
            holder.stanjeIspada.setTextColor(ContextCompat.getColor(context,R.color.rijeseno));
        }
        else
        {
            holder.stanjeIspada.setText("Nije rijeseno");
            holder.stanjeIspada.setTextColor(ContextCompat.getColor(context,R.color.nijerijeseno));
        }



        holder.grad.setText(ispadi.getGrad());
        holder.vrstaIspada.setText(ispadi.getVrstaIspada());
        holder.vrijemePocetka.setText(pocetakVrijeme);
        holder.datumPocetka.setText(pocetakDatum);



    }

    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    public static Date GetTodayDate()
    {
        Date dtf = new Date();
        dtf.getTime();

        return dtf;
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

    @Override
    public int getItemCount() {
        return lIspadPrikaz.size();
    }

    class IspadiViewHolder extends RecyclerView.ViewHolder {

        private TextView grad;
        private TextView vrstaIspada;
        private TextView vrijemePocetka;
        private TextView datumPocetka;
        private TextView stanjeIspada;
        public IspadiViewHolder(@NonNull View itemView) {
            super(itemView);

            grad = itemView.findViewById(R.id.GradNaziv_CardViewTxt);
            vrstaIspada= itemView.findViewById(R.id.VrstaIspada_CardViewTxt);
            vrijemePocetka = itemView.findViewById(R.id.VrijemeIspada_CardViewTxt);
            datumPocetka = itemView.findViewById(R.id.DatumIspada_CardViewTxt);
            stanjeIspada = itemView.findViewById(R.id.StanjeIspada_CardView);
        }
    }
}
