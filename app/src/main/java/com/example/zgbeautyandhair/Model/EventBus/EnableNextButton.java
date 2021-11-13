package com.example.zgbeautyandhair.Model.EventBus;


import com.example.zgbeautyandhair.Model.Barber;
import com.example.zgbeautyandhair.Model.Salon;

public class EnableNextButton {
    private int step;
    private Barber barber;
    private Salon salon;
    private int timeSlot;
    private boolean button = false;

    public EnableNextButton(int step, int timeSlot, boolean enabledButton) {
        this.step = step;
        this.timeSlot = timeSlot;
        this.button = enabledButton;
    }

    public EnableNextButton(int step, Salon salon) {
        this.step = step;
        this.salon = salon;
    }

    public EnableNextButton(int step, Barber barber) {
        this.step = step;
        this.barber = barber;
    }

    public boolean isButton() {
        return button;
    }

    public void setButton(boolean button) {
        this.button = button;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Barber getBarber() {
        return barber;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

}
