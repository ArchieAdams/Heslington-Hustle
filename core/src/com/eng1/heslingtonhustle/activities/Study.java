package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Study extends Activity {
    public Study(int time, int energy) {
        super("study",time, energy);
    }

    @Override
    public void onPerform(PlayerManager playerManager){
        playerManager.study(durationHours);
    }

    @Override
    public String toString() {
        return String.format("This will help increase your grades.\n%s", super.toString());
    }
}
