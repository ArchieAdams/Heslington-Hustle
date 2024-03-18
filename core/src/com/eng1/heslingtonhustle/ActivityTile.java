package com.eng1.heslingtonhustle;

import com.badlogic.gdx.math.Rectangle;
import com.eng1.heslingtonhustle.activities.Activity;

public class ActivityTile {
    private final Rectangle rectangle;
    private final Activity activity;

    public ActivityTile(Rectangle rectangle, Activity activity) {
        this.rectangle = rectangle;
        this.activity = activity;
    }


    public Rectangle getRectangle() {
        return rectangle;
    }

    public Activity getActivity() {
        return activity;
    }

    }
