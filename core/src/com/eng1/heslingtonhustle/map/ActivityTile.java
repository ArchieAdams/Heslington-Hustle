package com.eng1.heslingtonhustle.map;

import com.badlogic.gdx.math.Rectangle;

public class ActivityTile {
    private final Rectangle rectangle;


    public ActivityTile(Rectangle rectangle) {
        this.rectangle = rectangle;
    }


    public Rectangle getRectangle() {
        return rectangle;
    }
}
