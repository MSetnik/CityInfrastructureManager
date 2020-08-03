package com.example.cityinfrastrukturemanager.Model;

import androidx.annotation.NonNull;

public class SifrarnikVrstaIspada {
    private int ID_vrsta_ispada;
    private String vrsta_ispada;

    public SifrarnikVrstaIspada(int ID_vrsta_ispada, String vrsta_ispada) {
        this.ID_vrsta_ispada = ID_vrsta_ispada;
        this.vrsta_ispada = vrsta_ispada;
    }

    public int getID_vrsta_ispada() {
        return ID_vrsta_ispada;
    }

    public void setID_vrsta_ispada(int ID_vrsta_ispada) {
        this.ID_vrsta_ispada = ID_vrsta_ispada;
    }

    public String getVrsta_ispada() {
        return vrsta_ispada;
    }

    public void setVrsta_ispada(String vrsta_ispada) {
        this.vrsta_ispada = vrsta_ispada;
    }

    @NonNull
    @Override
    public String toString() {
        return vrsta_ispada;
    }
}