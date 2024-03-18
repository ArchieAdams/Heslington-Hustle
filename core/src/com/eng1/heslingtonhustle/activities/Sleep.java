package com.eng1.heslingtonhustle.activities;

import com.eng1.heslingtonhustle.PlayerManager;

public class Sleep extends Activity {
    public Sleep() {
        super("sleep",0, 0);
    }

    @Override
    public void onPerform(PlayerManager playerManager) {
        playerManager.sleep();
    }
}
