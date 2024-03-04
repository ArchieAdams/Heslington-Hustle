package com.eng1.heslingtonhustle;
public class State {

    private boolean UP;
    private boolean DOWN;
    private boolean LEFT;
    private boolean RIGHT;

    public State() {
        UP = false;
        DOWN = false;
        LEFT = false;
        RIGHT = false;
    }

    public void moveUp() {
        UP = !UP;
    }

    public void moveDown() {
        DOWN = !DOWN;
    }

    public void moveLeft() {
        LEFT = !LEFT;
    }

    public void moveRight() {
        RIGHT = !RIGHT;
    }

    public int getMoveDirectionY() {
        return (UP ? 1 : 0) - (DOWN ? 1 : 0);
    }

    public int getMoveDirectionX() {
        return (RIGHT ? 1 : 0) - (LEFT ? 1 : 0);
    }

}

