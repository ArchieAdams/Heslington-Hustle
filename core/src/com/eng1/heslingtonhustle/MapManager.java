package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eng1.heslingtonhustle.Game.SCALE;

public class MapManager {
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Array<Rectangle> collidableTiles = new Array<>();

    public MapManager() {
        tiledMap = new TmxMapLoader().load("maps/campus_east.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);
        parseCollidableTiles();
    }

    public List<Building> createBuildings(Map<String, Building> buildingMap) {
        List<Building> buildings = new ArrayList<>();
        for (MapObject buildingCorner : (tiledMap.getLayers().get("buildingCorners").getObjects())){
            String id = (String) buildingCorner.getProperties().get("name");
            if (buildingMap.containsKey(id)){
                float buildingX = Float.parseFloat(buildingCorner.getProperties().get("x").toString()) * SCALE;
                float buildingY =  Float.parseFloat(buildingCorner.getProperties().get("y").toString()) * SCALE;
                Building building = buildingMap.get(id);
                building.setPosition(new Vector2(buildingX,buildingY));
                buildings.add(building);
            }
        }
        return buildings;
    }
    private void parseCollidableTiles() {
        MapObjects objects = tiledMap.getLayers().get("collisions").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                collidableTiles.add(new Rectangle(rect.x * SCALE, rect.y * SCALE, rect.width * SCALE, rect.height * SCALE));
            }
        }
    }

    public Array<Rectangle> getCollidableTiles() {
        return collidableTiles;
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
}
