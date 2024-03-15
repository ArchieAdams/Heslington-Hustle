package com.eng1.heslingtonhustle;

public class Energy {
    private int level = 100;

    public Energy() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int energyUsed) {
        int min = 0;
        if (level - energyUsed >= min) {
            this.level = level - energyUsed;
        }
    }

    public void reset() {
        this.level = 100;
    }
}
