package com.eng1.heslingtonhustle;

import com.eng1.heslingtonhustle.activities.Activity;

import java.util.ArrayList;
import java.util.List;

public class Day {
    private final List<Activity> activities;
    private int studySessions;
    private int eaten;
    private int relaxed;


    public Day() {
        activities = new ArrayList<>();
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public int getTotalDuration() {
        int totalDuration = 0;
        for (Activity activity : activities) {
            totalDuration += activity.getDurationHours();
        }
        return totalDuration;
    }

    public int getTotalEnergyUsage() {
        int totalEnergyUsage = 0;
        for (Activity activity : activities) {
            totalEnergyUsage += activity.getEnergyUsagePercent();
        }
        return totalEnergyUsage;
    }

    public void studied() {
        studySessions++;
    }

    public void eaten() {
        eaten++;
    }

    public void relaxed() {
        relaxed++;
    }

    public int getStudySessions() {
        return studySessions;
    }

    public int getEaten() {
        return eaten;
    }

    public int getRelaxed() {
        return relaxed;
    }
}

