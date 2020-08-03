package com.example.cityinfrastrukturemanager.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class IspadPrikaz implements Serializable, ClusterItem {
    private int id_ispad;
    private String ime;
    private String prezime;
    private String vrstaIspada;
    private String grad;
    private double lat;
    private double lng;
    private String zupanija;
    private String pocetak_ispada;
    private String kraj_ispada;
    private String opis;
    private String status;

    public IspadPrikaz(int id_ispad, String ime, String prezime, String vrstaIspada, String grad, double lat, double lng, String zupanija, String pocetak_ispada, String kraj_ispada, String opis, String status) {
        this.id_ispad = id_ispad;
        this.ime = ime;
        this.prezime = prezime;
        this.vrstaIspada = vrstaIspada;
        this.grad = grad;
        this.lat = lat;
        this.lng = lng;
        this.zupanija = zupanija;
        this.pocetak_ispada = pocetak_ispada;
        this.kraj_ispada = kraj_ispada;
        this.opis = opis;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getId_ispad() {
        return id_ispad;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getVrstaIspada() {
        return vrstaIspada;
    }

    public String getGrad() {
        return grad;
    }

    public String getZupanija() {
        return zupanija;
    }

    public String getPocetak_ispada() {
        return pocetak_ispada;
    }

    public String getKraj_ispada() {
        return kraj_ispada;
    }

    public String getOpis() {
        return opis;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }
}
