package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Study extends Activity {
    public Study() {
        super("study",2, 20);
    }

    @Override
    public void onPerform(PlayerManager playerManager){
        playerManager.study(durationHours, energyUsagePercent);
    }
}
