package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheet {

    public static final Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
    public static final Texture map = new Texture(Gdx.files.internal("map.png"));

    public static Animation<TextureRegion> getRightWalk() {
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);
        TextureRegion[] rightWalkFrames = new TextureRegion[8];
        System.arraycopy(tmp[0], 0, rightWalkFrames, 0, 8);
        return new Animation<>(0.1f, rightWalkFrames);
    }

    public static Animation<TextureRegion> getLeftWalk() {
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);
        TextureRegion[] leftWalkFrames = new TextureRegion[8];
        System.arraycopy(tmp[1], 0, leftWalkFrames, 0, 8);
        return new Animation<>(0.1f, leftWalkFrames);
    }

    public static Texture getMap() {
        return map;
    }
}
