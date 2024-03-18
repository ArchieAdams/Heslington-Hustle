package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Relax extends Activity {
    public Relax() {
        super("relax", 1, 5);
    }

    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.relax(durationHours);
    }
}
