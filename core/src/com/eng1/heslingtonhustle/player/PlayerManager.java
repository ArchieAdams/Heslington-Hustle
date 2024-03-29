package com.eng1.heslingtonhustle.player;

import com.badlogic.gdx.math.Vector2;
import com.eng1.heslingtonhustle.gameobjects.Day;
import com.eng1.heslingtonhustle.gameobjects.Energy;
import com.eng1.heslingtonhustle.gameobjects.Time;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    public final Movement movement;
    public final Time time = new Time();
    private final Energy energy = new Energy();
    private final List<Day> week = new ArrayList<>();


    private Day currentDay;

    public PlayerManager(Vector2 position, float speed) {
        movement = new Movement(position, speed);

    }

    public void setCurrentDay(Day currentDay) {
        this.currentDay = currentDay;
    }

    public State getState() {
        return movement.getPlayerState();
    }

    public Vector2 getPosition() {
        return movement.getPosition();
    }

    public Movement getMovement() {
        return movement;
    }


    public void study() {
        currentDay.studied();
    }

    public void eat() {
        currentDay.eaten();
    }

    public void relax() {
        currentDay.relaxed();
    }

    public void sleep() {
        week.add(currentDay);
        currentDay = new Day();
        energy.reset();
        time.nextDay();
    }

    public boolean performActivity(int energyCost, int timeUsed) {
        if (!energy.canUseEnergy(energyCost)) {
            System.out.println("not enough energy");
            return false; //not enough energy
        }
        if (!time.canIncreaseTime(timeUsed)) {
            System.out.println("not enough time");
            return false; //not enough time
        }
        energy.useEnergy(energyCost);
        time.increaseTime(timeUsed);
        return true;
    }

    public Energy getEnergy() {
        return energy;
    }

    public Time getTime() {
        return time;
    }

    public boolean gameOver() {
        return time.isWeekOver();
    }


    public List<Day> getWeek() {
        return week;
    }
}
