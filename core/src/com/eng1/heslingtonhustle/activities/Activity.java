package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

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

    public boolean perform(PlayerManager playerManager) {
        if (!playerManager.performActivity(energyUsagePercent,durationHours)) {
            return false;
        }
        onPerform(playerManager);
        return true;
    }


    public abstract void onPerform(PlayerManager playerManager);
}

