package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Building {

    private final String name;

    private Vector2 position;
    private final TextureRegion textureRegion;

    private boolean isVisible = true;


    public Building(BuildingInfo buildingInfo) {
        this.name = buildingInfo.name;
        this.textureRegion = SpriteSheet.getBuildingTextureRegion(
                buildingInfo.textureStartX, buildingInfo.textureStartY,
                buildingInfo.textureWidth, buildingInfo.textureHeight);
   }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    private static boolean between(float variable, float minValueInclusive, float maxValueInclusive) {
        return variable >= minValueInclusive && variable <= maxValueInclusive;
    }

    public boolean inRange(Vector2 playerPosition) {

        return between(playerPosition.x, getInteractSpot().x, getInteractSpot().x+32*5) &&
                between(playerPosition.y, getInteractSpot().y, getInteractSpot().y+32*5);
    }


    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public Vector2 getInteractSpot() {
        float X  = position.x + ((float) (textureRegion.getRegionWidth() - 32) / 2) * 5;
        float Y = position.y - 32*5;
        if (name.equals("Home")){
            X = position.x + ((float) (textureRegion.getRegionWidth() - 80) / 2) * 5;
        }
        return new Vector2(X,Y);
    }

    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

}
