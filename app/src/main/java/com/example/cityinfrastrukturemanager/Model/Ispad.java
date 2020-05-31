package com.example.cityinfrastrukturemanager.Model;

public class Ispad {
    private int id_ispad;
    private int id_korisnik;
    private int id_vrstaIspada;
    private int id_grad;
    private String pocetak_ispada;
    private String kraj_ispada;
    private String opis;

    public Ispad(int id_ispad, int id_korisnik, int id_vrstaIspada, int id_grad, String pocetak_ispada, String kraj_ispada, String opis) {
        this.id_ispad = id_ispad;
        this.id_korisnik = id_korisnik;
        this.id_vrstaIspada = id_vrstaIspada;
        this.id_grad = id_grad;
        this.pocetak_ispada = pocetak_ispada;
        this.kraj_ispada = kraj_ispada;
        this.opis = opis;
    }

    public int getId_ispad() {
        return id_ispad;
    }

    public void setId_ispad(int id_ispad) {
        this.id_ispad = id_ispad;
    }

    public int getId_korisnik() {
        return id_korisnik;
    }

    public void setId_korisnik(int id_korisnik) {
        this.id_korisnik = id_korisnik;
    }

    public int getId_vrstaIspada() {
        return id_vrstaIspada;
    }

    public void setId_vrstaIspada(int id_vrstaIspada) {
        this.id_vrstaIspada = id_vrstaIspada;
    }

    public int getId_grad() {
        return id_grad;
    }

    public void setId_grad(int id_grad) {
        this.id_grad = id_grad;
    }

    public String getPocetak_ispada() {
        return pocetak_ispada;
    }

    public void setPocetak_ispada(String pocetak_ispada) {
        this.pocetak_ispada = pocetak_ispada;
    }

    public String getKraj_ispada() {
        return kraj_ispada;
    }

    public void setKraj_ispada(String kraj_ispada) {
        this.kraj_ispada = kraj_ispada;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
