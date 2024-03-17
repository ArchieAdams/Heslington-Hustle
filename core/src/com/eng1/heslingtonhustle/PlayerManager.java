package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerManager {
    private Energy energy;
    public final Movement movement;

    private final List<Day> week = new ArrayList<>();

    private Day currentDay;

    public PlayerManager(Vector2 position, float speed) {
        movement = new Movement(position, speed);
        energy = new Energy();
    }

    public void setCurrentDay(Day currentDay) {
        this.currentDay = currentDay;
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

    public void increaseStudyScore() {
        currentDay.studied();
    }

    public void eat(){
        currentDay.eaten();
    }

    public void relaxed(){
        currentDay.relaxed();
    }

    public void sleep(){
        week.add(currentDay);
        currentDay = new Day();
        energy.reset();
    }

    public boolean performActivity(int energyCost) {
        return energy.useEnergy(energyCost);
    }

    public Energy getEnergy() {
        return energy;
    }

    public Day getDay() {
        return currentDay;
    }
}
