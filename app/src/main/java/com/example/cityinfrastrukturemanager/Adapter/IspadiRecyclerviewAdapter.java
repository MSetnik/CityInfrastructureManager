package com.example.cityinfrastrukturemanager.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;
import com.example.cityinfrastrukturemanager.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IspadiRecyclerviewAdapter extends RecyclerView.Adapter<IspadiRecyclerviewAdapter.IspadiViewHolder> implements Filterable {
    private ArrayList<IspadPrikaz>lIspadPrikaz;
    private ArrayList<IspadPrikaz>lIspadPrikazPun;
    private String datumPocetak = "";
    private String datumKraj = "";
    private static final String TAG = "MyApp";
    private Context context;
    private IspadClickListener ispadClickListener;


    public IspadiRecyclerviewAdapter (Context context,ArrayList<IspadPrikaz> lIspadPrikaz) {
        this.lIspadPrikaz = lIspadPrikaz;
        this.lIspadPrikazPun = new ArrayList<>(lIspadPrikaz);
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


        if(ispadi.getStatus().equals("NIJE RIJEŠENO"))
        {
            holder.stanjeIspada.setText(ispadi.getStatus());
            holder.stanjeIspada.setTextColor(Color.parseColor("#FFCF1B0B"));
        }
        else
        {
            holder.stanjeIspada.setText(ispadi.getStatus());
            holder.stanjeIspada.setTextColor(Color.parseColor("#FF2FBC08"));
        }



        holder.grad.setText(ispadi.getGrad());
        holder.vrstaIspada.setText(ispadi.getVrstaIspada());
        holder.vrijemePocetka.setText(pocetakVrijeme);
        holder.datumPocetka.setText(pocetakDatum);
        switch (ispadi.getVrstaIspada())
        {
            case "Nestanak električne energije":
                holder.vrstaIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.Nestanak_elektricne_energije));
                break;

            case "Nestanak plina":
                holder.vrstaIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.Nestanak_plina));
                break;

            case "Nestanak vode":
                holder.vrstaIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.Nestanak_vode));
                break;

            case "Prekid prometa":
                holder.vrstaIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.Prekid_prometa));
                break;
        }





    }

    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


    public void SetFilterOdabirLista(ArrayList<IspadPrikaz>lFilterIspadPrikaz)
    {
        this.lIspadPrikaz = lFilterIspadPrikaz;
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

    @Override
    public Filter getFilter() {

        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // radi na background threadu
            ArrayList<IspadPrikaz>filtriranPrikaz = new ArrayList<>();

            if(constraint == null ||constraint.length()==0)
            {
                filtriranPrikaz.addAll(lIspadPrikazPun);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(IspadPrikaz prikaz : lIspadPrikazPun)
                {
                    if(prikaz.getGrad().toLowerCase().contains(filterPattern) || prikaz.getVrstaIspada().toLowerCase().contains(filterPattern) || prikaz.getZupanija().toLowerCase().contains(filterPattern))
                    {
                        filtriranPrikaz.add(prikaz);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = filtriranPrikaz;

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lIspadPrikaz.clear();
            lIspadPrikaz.addAll((ArrayList) results.values);

            notifyDataSetChanged();
        }
    };

    class IspadiViewHolder extends RecyclerView.ViewHolder {

        private TextView grad;
        private TextView vrstaIspada;
        private TextView vrijemePocetka;
        private TextView datumPocetka;
        private TextView stanjeIspada;
        private View vrstaIndicator;
        public IspadiViewHolder(@NonNull View itemView) {
            super(itemView);

            grad = itemView.findViewById(R.id.GradNaziv_CardViewTxt);
            vrstaIspada= itemView.findViewById(R.id.VrstaIspada_CardViewTxt);
            vrijemePocetka = itemView.findViewById(R.id.VrijemeIspada_CardViewTxt);
            datumPocetka = itemView.findViewById(R.id.DatumIspada_CardViewTxt);
            stanjeIspada = itemView.findViewById(R.id.StanjeIspada_CardView);
            vrstaIndicator = itemView.findViewById(R.id.vrstaIndicator);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ispadClickListener.onIspadClick(lIspadPrikaz.get(position));
                }
            });
        }
    }

    public interface IspadClickListener{
        void onIspadClick(IspadPrikaz ispadiPrikaz);
    }

    public void SetOnClickListenerIspadDetalji(IspadClickListener ispadClickListener)
    {
        this.ispadClickListener = ispadClickListener;
    }
}
