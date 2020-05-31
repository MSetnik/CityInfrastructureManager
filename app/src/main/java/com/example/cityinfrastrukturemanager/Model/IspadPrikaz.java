package com.example.cityinfrastrukturemanager.Model;

public class IspadPrikaz {
    private int id_ispad;
    private String ime;
    private String prezime;
    private String vrstaIspada;
    private String grad;
    private String zupanija;
    private String pocetak_ispada;
    private String kraj_ispada;
    private String opis;

    public IspadPrikaz(int id_ispad, String ime, String prezime, String vrstaIspada, String grad, String zupanija, String pocetak_ispada, String kraj_ispada, String opis) {
        this.id_ispad = id_ispad;
        this.ime = ime;
        this.prezime = prezime;
        this.vrstaIspada = vrstaIspada;
        this.grad = grad;
        this.zupanija = zupanija;
        this.pocetak_ispada = pocetak_ispada;
        this.kraj_ispada = kraj_ispada;
        this.opis = opis;
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
}
