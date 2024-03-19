package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Eat extends Activity {
    public Eat() {
        super("eat", 1, 10);
    }


    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.eat(durationHours, energyUsagePercent);
    }
}
