package com.example.cityinfrastrukturemanager.Model;

import androidx.annotation.NonNull;

public class Zupanija {
  private int ID_zupanija;
  private String naziv_zupanija;

  public Zupanija(int ID_zupanija, String naziv_zupanija) {
    this.ID_zupanija = ID_zupanija;
    this.naziv_zupanija = naziv_zupanija;
  }

  public int getID_zupanija() {
    return ID_zupanija;
  }

  public void setID_zupanija(int ID_zupanija) {
    this.ID_zupanija = ID_zupanija;
  }

  public String getNaziv_zupanija() {
    return naziv_zupanija;
  }

  public void setNaziv_zupanija(String naziv_zupanija) {
    this.naziv_zupanija = naziv_zupanija;
  }

  @NonNull
  @Override
  public String toString() {
    return naziv_zupanija;
  }
}