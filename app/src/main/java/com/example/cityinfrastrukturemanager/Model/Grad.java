package com.example.cityinfrastrukturemanager.Model;

public class Grad {
    private int ID_grad;
    private int ID_zupanija;
    private String naziv_grad;
    private float lat;
    private float lng;

    public Grad(int ID_grad, int ID_zupanija, String naziv_grad, float lat, float lng) {
        this.ID_grad = ID_grad;
        this.ID_zupanija = ID_zupanija;
        this.naziv_grad = naziv_grad;
        this.lat = lat;
        this.lng = lng;
    }

    public int getID_grad() {
        return ID_grad;
    }

    public void setID_grad(int ID_grad) {
        this.ID_grad = ID_grad;
    }

    public int getID_zupanija() {
        return ID_zupanija;
    }

    public void setID_zupanija(int ID_zupanija) {
        this.ID_zupanija = ID_zupanija;
    }

    public String getNaziv_grad() {
        return naziv_grad;
    }

    public void setNaziv_grad(String naziv_grad) {
        this.naziv_grad = naziv_grad;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
