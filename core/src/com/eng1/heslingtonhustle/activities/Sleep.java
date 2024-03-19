package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Sleep extends Activity {
    public Sleep(int time, int energy) {
        super("sleep",time, energy);
    }

    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.sleep();
    }

    @Override
    public String toString() {
        return "This will make you skip to the next day.";
    }
}
