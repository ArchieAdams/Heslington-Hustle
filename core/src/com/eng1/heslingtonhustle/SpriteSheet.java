package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheet {

    public static final Texture walkSheet = new Texture(Gdx.files.internal("character2.png"));
    public static final Texture buildings = new Texture(Gdx.files.internal("images/town.png"));
    public static final Texture debug = new Texture(Gdx.files.internal("images/debug.png"));



    public static Animation<TextureRegion> getDownWalk() {
        return getTextureRegionByRow(0);
    }
    public static Animation<TextureRegion> getUpWalk() {
        return getTextureRegionByRow(1);
    }

    public static Animation<TextureRegion> getRightWalk() {
        return getTextureRegionByRow(2);
    }

    public static Animation<TextureRegion> getLeftWalk() {
        return getTextureRegionByRow(3);
    }

    private static Animation<TextureRegion> getTextureRegionByRow(int x) {
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);
        TextureRegion[] out = new TextureRegion[8];
        System.arraycopy(tmp[x], 0, out, 0, 8);
        return new Animation<>(0.1f, out);
    }


    public static TextureRegion getBuildingTextureRegion(int startX, int startY, int width, int height){
        return new TextureRegion(buildings,startX,startY,16*width,16*height);
    }

    public static Texture getDebug() {
        return debug;
    }
}
