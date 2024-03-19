package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Eat extends Activity {
    public Eat(int time, int energy) {
        super("eat",time, energy);
    }


    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.eat(durationHours);
    }


    @Override
    public String toString() {
        return String.format("This will help you survive, please eat.\n%s", super.toString());
    }
}
