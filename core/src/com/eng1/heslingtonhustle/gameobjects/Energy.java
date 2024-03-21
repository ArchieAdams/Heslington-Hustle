package com.eng1.heslingtonhustle.gameobjects;

public class Energy {
    private int energy = 100;

    public Energy() {
    }

    public int getEnergyLevel() {
        return energy;
    }


    public void useEnergy(int energyUsed) {
        int min = 0;
        boolean canBeDone = energy - energyUsed >= min;
        if (canBeDone) {
            this.energy = energy - energyUsed;
        }
    }

    public boolean canUseEnergy(int energyUsed){
        final int MIN_ENERGY = 0;
        return (energy - energyUsed >= MIN_ENERGY);
    }

    public void reset() {
        this.energy = 100;
    }
}
