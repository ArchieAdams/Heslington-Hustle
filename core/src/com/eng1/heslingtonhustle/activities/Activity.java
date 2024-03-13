package com.eng1.heslingtonhustle.activities;

public abstract class Activity {
    protected int durationHours;
    protected int energyUsagePercent;


    public Activity(int durationHours, int energyUsagePercent) {
        this.durationHours = durationHours;
        this.energyUsagePercent = energyUsagePercent;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public int getEnergyUsagePercent() {
        return energyUsagePercent;
    }

    public abstract void perform();
}

