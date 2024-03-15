package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Map;
import java.util.HashMap;

import static com.eng1.heslingtonhustle.Game.SCALE;

public class MapManager {
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Array<Rectangle> collidableTiles = new Array<>();
    private final Map<String, String> mapPaths;
    private final Array<Rectangle> exitTiles = new Array<>();

    public MapManager() {
        tiledMap = new TmxMapLoader().load("maps/campus_east.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);
        parseCollidableTiles();

        mapPaths = new HashMap<>();
        mapPaths.put("Library", "assets/maps/library.tmx");
        mapPaths.put("Cafe", "assets/maps/cafe.tmx");
        mapPaths.put("Cinema", "assets/maps/cinema.tmx");
        mapPaths.put("Home", "assets/maps/home.tmx");
        mapPaths.put("Computer Science Building", "assets/maps/compSci.tmx");
        mapPaths.put("Campus", "assets/maps/campus_east.tmx");

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

    private void parseExitTiles() {
        if(tiledMap.getLayers().get("exit") != null) {
            MapObjects objects = tiledMap.getLayers().get("exit").getObjects();
            for (MapObject object : objects) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    exitTiles.add(new Rectangle(rect.x * SCALE, rect.y * SCALE, rect.width * SCALE, rect.height * SCALE));
                }
            }
        }
    }

    public Array<Rectangle> getCollidableTiles() {
        return collidableTiles;
    }

    public Array<Rectangle> getExitTiles() {
        return exitTiles;
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void changeMap(String newMapPath) {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        tiledMap = new TmxMapLoader().load(newMapPath);
        mapRenderer.setMap(tiledMap);
        collidableTiles.clear();
        parseCollidableTiles();
        parseExitTiles();
    }

    public void changeMap() {
        if (tiledMap!= null) {
            tiledMap.dispose();
        }
        tiledMap = new TmxMapLoader().load("maps/campus_east.tmx");
        mapRenderer.setMap(tiledMap);
        collidableTiles.clear();
        parseCollidableTiles();
    }

    public String getMapPath(String mapName) {
        return mapPaths.get(mapName);
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
