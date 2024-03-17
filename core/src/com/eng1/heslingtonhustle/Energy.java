package com.eng1.heslingtonhustle;

public class Energy {
    private int level = 100;

    public Energy() {
    }

    public int getLevel() {
        return level;
    }


    public boolean useEnergy(int energyUsed) {
        int min = 0;
        boolean canBeDone = level - energyUsed >= min;
        if (canBeDone) {
            this.level = level - energyUsed;
        }
        return canBeDone;
    }

    public void reset() {
        this.level = 100;
    }
}
