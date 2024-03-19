package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Movement {
    public static final float DIAGONAL_MODIFIER = (float) (Math.sqrt(2) / 2);
    private final Vector2 position;
    private final float speed;
    private final State state;
    private final Animation<TextureRegion> downWalkAnimation = ResourceLoader.getDownWalk();
    private final Animation<TextureRegion> upWalkAnimation = ResourceLoader.getUpWalk();
    private final Animation<TextureRegion> leftWalkAnimation = ResourceLoader.getLeftWalk();
    private final Animation<TextureRegion> rightWalkAnimation = ResourceLoader.getRightWalk();
    private float stateTime;
    private Animation<TextureRegion> currentAnimation;
    private TextureRegion currentFrame;
    private Array<Rectangle> collidableTiles;
    private static final float PLAYER_WIDTH = 16;
    private static final float PLAYER_HEIGHT = 20;
    private boolean movementEnabled = true;


    public void setCollidableTiles(Array<Rectangle> collidableTiles) {
        this.collidableTiles = collidableTiles;
    }


    public Movement(Vector2 position, float speed) {
        this.position = position;
        this.speed = speed;
        this.state = new State();
        currentAnimation = downWalkAnimation;
    }

    public void update(float deltaTime) {
        if (!movementEnabled) {
            return;
        }

        int moveDirectionY = state.getMoveDirectionY();
        int moveDirectionX = state.getMoveDirectionX();

        float speedModifier = 1f;
        if (moveDirectionX != 0 && moveDirectionY != 0) {
            speedModifier = DIAGONAL_MODIFIER;
        }

        float velocity = speedModifier * speed * deltaTime;

        float potentialNewX = position.x + moveDirectionX * velocity;
        float potentialNewY = position.y + moveDirectionY * velocity;

        if (!collidesX(potentialNewX, potentialNewY)) {
            position.x = potentialNewX;
        }
        if (!collidesY(potentialNewX, potentialNewY)) {
            position.y = potentialNewY;
        }


        stateTime += deltaTime;
        updateAnimation();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

    }

    private boolean collidesX(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, PLAYER_WIDTH, 0);
        return collides(playerRect);
    }

    private boolean collidesY(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, 0, PLAYER_HEIGHT);
        return collides(playerRect);
    }

    private boolean collides(Rectangle playerRect) {
        for (Rectangle rect : collidableTiles) {
            if (rect.overlaps(playerRect)) {
                return true;
            }
        }
        return false;
    }



    private void updateAnimation() {
        int moveDirectionY = state.getMoveDirectionY();
        int moveDirectionX = state.getMoveDirectionX();


        if (moveDirectionX == 0 && moveDirectionY == 0) {
            stateTime = 0f;
        } else if (moveDirectionX == 1 && !currentAnimation.equals(rightWalkAnimation)) {
            currentAnimation = rightWalkAnimation;
        } else if (moveDirectionX == -1 && !currentAnimation.equals(leftWalkAnimation)) {
            currentAnimation = leftWalkAnimation;
        } else if (moveDirectionY == 1 && !currentAnimation.equals(upWalkAnimation) && moveDirectionX == 0) {
            currentAnimation = upWalkAnimation;
        } else if (moveDirectionY == -1 && !currentAnimation.equals(downWalkAnimation) && moveDirectionX == 0) {
            currentAnimation = downWalkAnimation;
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

    public void setPosition(Vector2 newPosition) {
        this.position.x = newPosition.x;
        this.position.y = newPosition.y;
    }

    public void disableMovement() {
        movementEnabled = false;
    }
}
