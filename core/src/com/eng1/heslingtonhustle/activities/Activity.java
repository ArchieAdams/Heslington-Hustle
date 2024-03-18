package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public abstract class Activity {
    protected int durationHours;
    protected int energyUsagePercent;
    private String name;


    public Activity(String name, int durationHours, int energyUsagePercent) {
        this.name = name;
        this.durationHours = durationHours;
        this.energyUsagePercent = energyUsagePercent;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public int getEnergyUsagePercent() {
        return energyUsagePercent;
    }

    public boolean perform(PlayerManager playerManager) {
        if (!playerManager.performActivity(energyUsagePercent)) {
            return false;
        }
        onPerform(playerManager);
        return true;
    }

    public String getName() {
        return name;
    }


    public abstract void onPerform(PlayerManager playerManager);
}

