package com.example.cityinfrastrukturemanager.Model;

public class NajbliziIspad {
    IspadPrikaz ispadPrikaz;
    float udaljenost;


    public void setIspadPrikaz(IspadPrikaz ispadPrikaz) {
        this.ispadPrikaz = ispadPrikaz;
    }

    public void setUdaljenost(float udaljenost) {
        this.udaljenost = udaljenost;
    }

    public IspadPrikaz getIspadPrikaz() {
        return ispadPrikaz;
    }

    public float getUdaljenost() {
        return udaljenost;
    }
}
