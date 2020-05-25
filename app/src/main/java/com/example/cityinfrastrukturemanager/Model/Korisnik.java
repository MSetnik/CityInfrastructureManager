package com.example.cityinfrastrukturemanager.Model;

public class Korisnik {
    private int ID_korisnik;
    private String korisnicko_ime;
    private String lozinka;
    private String ime;
    private String prezime;

    public Korisnik(int ID_korisnik, String korisnicko_ime, String lozinka, String ime, String prezime) {
        this.ID_korisnik = ID_korisnik;
        this.korisnicko_ime = korisnicko_ime;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
    }

    public int getID_korisnik() {
        return ID_korisnik;
    }

    public void setID_korisnik(int ID_korisnik) {
        this.ID_korisnik = ID_korisnik;
    }

    public String getKorisnicko_ime() {
        return korisnicko_ime;
    }

    public void setKorisnicko_ime(String korisnicko_ime) {
        this.korisnicko_ime = korisnicko_ime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }
}
