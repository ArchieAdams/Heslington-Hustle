package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerManager {
    private int energy;
    public final Movement movement;
    private Set<Building> buildingsInRange;

    private final List<Day> week = new ArrayList<>();

    public PlayerManager(Vector2 position, float speed) {
        movement = new Movement(position, speed);
    }

    public State getState(){
        return movement.getPlayerState();
    }

    public TextureRegion getCurrentFrame() {
        return movement.getCurrentFrame();
    }

    public Vector2 getPosition() {
        return movement.getPosition();
    }

    public Movement getMovement(){
        return movement;
    }

    public void respawn(Vector2 position) {

    }
}
