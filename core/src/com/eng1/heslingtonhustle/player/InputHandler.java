package com.eng1.heslingtonhustle.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    private final State playerState;


    public InputHandler(State state) {
        this.playerState = state;
    }

    @Override
    public boolean keyDown(int keycode) {
        handleInput(keycode);
        if (keycode == Input.Keys.E) {
            playerState.interacting();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        handleInput(keycode);
        if (keycode == Input.Keys.E) {
            playerState.stopInteracting();
        }
        return true;
    }

    public void handleInput(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                playerState.moveUp();
                break;
            case Input.Keys.S:
                playerState.moveDown();
                break;
            case Input.Keys.A:
                playerState.moveLeft();
                break;
            case Input.Keys.D:
                playerState.moveRight();
                break;
        }
    }

}
