package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Building {

    private final String name;
    private final Vector2 position;
    private final Texture texture;

    private final int width;
    private final int height;

    public Building(String name, Vector2 position, Texture texture) {
        this.name = name;
        this.position = position;
        this.texture = texture;
        // TODO make it get the Scale from GAME or move Scale
        width = texture.getWidth() * 5;
        height = texture.getHeight() * 5;
    }

    //TODO Move to Position object
    private static boolean between(float variable, float minValueInclusive, float maxValueInclusive) {
        return variable >= minValueInclusive && variable <= maxValueInclusive;
    }

    public boolean inRange(Vector2 playerPosition) {
        return between(playerPosition.x, position.x, position.x + width) &&
                between(playerPosition.y, position.y, position.y + height);
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
