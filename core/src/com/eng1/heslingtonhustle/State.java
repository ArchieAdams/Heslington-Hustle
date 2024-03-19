package com.eng1.heslingtonhustle;
public class State {

    private boolean UP;
    private boolean DOWN;
    private boolean LEFT;
    private boolean RIGHT;

    private boolean INTERACTING;
    private boolean IN_MENU;


    public State() {
        UP = false;
        DOWN = false;
        LEFT = false;
        RIGHT = false;
        INTERACTING = false;
        IN_MENU = false;
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

    public void interacting() {
        if (!IN_MENU)
            INTERACTING = true;
    }

    public void stopInteracting(){
        INTERACTING = false;
    }

    public boolean isINTERACTING() {
        return INTERACTING;
    }

    public int getMoveDirectionY() {
        if (IN_MENU){
            return 0;
        }
        return (UP ? 1 : 0) - (DOWN ? 1 : 0);
    }

    public int getMoveDirectionX() {
        if (IN_MENU){
            return 0;
        }
        return (RIGHT ? 1 : 0) - (LEFT ? 1 : 0);
    }

    public void inMenu(){
        IN_MENU = true;
    }

    public void leftMenu(){
        IN_MENU = false;
    }

}

