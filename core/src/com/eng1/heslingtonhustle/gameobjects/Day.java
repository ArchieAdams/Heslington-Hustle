package com.eng1.heslingtonhustle.gameobjects;

public class Day {
    private int studySessions;
    private int eaten;
    private int relaxed;


    public Day() {
    }

    public void studied() {
        studySessions++;
    }

    public void eaten() {
        eaten++;
    }

    public void relaxed() {
        relaxed++;
    }

    public int getStudySessions() {
        return studySessions;
    }

    public int getEaten() {
        return eaten;
    }

    public int getRelaxed() {
        return relaxed;
    }
}

