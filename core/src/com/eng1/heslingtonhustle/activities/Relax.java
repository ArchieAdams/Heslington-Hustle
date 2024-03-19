package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Relax extends Activity {
    public Relax(int time, int energy) {
        super("relax",time, energy);
    }

    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.relax(durationHours);
    }

    @Override
    public String toString() {
        return String.format("This may help you relax.\n%s", super.toString());
    }
}
