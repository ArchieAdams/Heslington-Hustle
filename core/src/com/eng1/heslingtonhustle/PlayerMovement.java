package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PlayerMovement {
    private final Vector2 position;
    private final float speed;
    private final State state;

    private float stateTime;

    private final Animation<TextureRegion> leftWalkAnimation = SpriteSheet.getLeftWalk();
    private final Animation<TextureRegion> rightWalkAnimation = SpriteSheet.getRightWalk();
    private Animation<TextureRegion> currentAnimation;

    private TextureRegion currentFrame;


    public PlayerMovement(Vector2 position, float speed) {
        this.position = position;
        this.speed = speed;
        this.state = new State();
        currentAnimation = rightWalkAnimation;
    }

    public void update(float deltaTime) {
        int moveDirectionY = state.getMoveDirectionY();
        int moveDirectionX = state.getMoveDirectionX();
        position.y += moveDirectionY * speed * deltaTime;
        position.x += moveDirectionX * speed * deltaTime;

        stateTime += deltaTime;
        updateAnimation();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);
    }

    private void updateAnimation() {
        int moveDirectionY = state.getMoveDirectionY();
        int moveDirectionX = state.getMoveDirectionX();

        if (moveDirectionX == 0 && moveDirectionY == 0) {
            stateTime = 0f;
        } else if (moveDirectionX == 1 && !currentAnimation.equals(rightWalkAnimation)) {
            stateTime = 0f;
            currentAnimation = rightWalkAnimation;
        } else if (moveDirectionX == -1 && !currentAnimation.equals(leftWalkAnimation)) {
            stateTime = 0f;
            currentAnimation = leftWalkAnimation;
        }
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public Vector2 getPosition() {
        return position;
    }

    public State getPlayerState() {
        return state;
    }
}
