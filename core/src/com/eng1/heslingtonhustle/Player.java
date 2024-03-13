package com.eng1.heslingtonhustle;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int energy;
    private Movement movement;

    private final List<Day> week = new ArrayList<>();


    public Player() {
        movement = new Movement(new Vector2(0,0),320);
    }
}
