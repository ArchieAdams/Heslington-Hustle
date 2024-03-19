package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private final Energy energy = new Energy();
    public final Movement movement;
    public final Time time = new Time();
    private final List<Day> week = new ArrayList<>();


    private Day currentDay;

    public PlayerManager(Vector2 position, float speed) {
        movement = new Movement(position, speed);

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


    public void study(int time) {
        currentDay.studied();
    }

    public void eat(int time){
        currentDay.eaten();
    }

    public void relax(int timeUsed){
        currentDay.relaxed();
    }

    public void sleep(){
        week.add(currentDay);
        currentDay = new Day();
        energy.reset();
        time.nextDay();
    }

    public boolean performActivity(int energyCost, int timeUsed) {
        if (!energy.canUseEnergy(energyCost)){
            System.out.println("not enough energy");
            return false; //not enough energy
        }
        if (!time.canIncreaseTime(timeUsed)){
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

    public Day getDay() {
        return currentDay;
    }

    public Time getTime() {
        return time;
    }

    public boolean gameOver() {
        return time.isWeekOver();
    }


    public void getScore(){
        int studyCount = 0;
        int dayStudiedOnce = 0;
        int dayRelaxedOnce = 0;
        int dayEatenCount = 0;
        int maxScore = 100;
        int score = 0;

        int i=0;
        for (Day day : week) {
            studyCount += day.getStudySessions();
            if (day.getStudySessions() > 1) {
                dayStudiedOnce++;
            }
            if (day.getEaten() >= 3) {
                dayEatenCount++;
            }
            if (day.getRelaxed() > 0) {
                dayRelaxedOnce++;
            }

            System.out.println("Day " + i);
            System.out.println("" + day.getStudySessions());
            System.out.println("" + day.getEaten());
            System.out.println("" + day.getRelaxed());

            i++;
        }

        score = studyCount * 10;

        // Apply penalties
        if (dayStudiedOnce != 7 && (dayStudiedOnce != 6 || studyCount < 7)) {
            score -= 20;
        }

        if (dayEatenCount < 2) {
            score -= 10; // Penalty for not eating enough
        }

        if (dayRelaxedOnce < 7) {
            score -= 10; // Penalty for not relaxing enough
        }

        // Cap the score at maxScore
        score = Math.min(score, maxScore);
        score = Math.max(score, 0);


        System.out.println("Final Score: " + score);


    }
}
