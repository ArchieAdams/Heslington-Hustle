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
import com.eng1.heslingtonhustle.activities.*;

import java.util.Map;
import java.util.HashMap;

import static com.eng1.heslingtonhustle.Game.SCALE;

public class MapManager {
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Array<Rectangle> collidableTiles = new Array<>();
    private final Map<String, String> mapPaths;
    private final Array<Rectangle> exitTiles = new Array<>();
    private final Array<ActivityTile> activityTiles = new Array<>();
    private final String defaultMapPath = "maps/campus_east.tmx";
    private String currentMapPath = "maps/campus_east.tmx";

    public MapManager() {
        tiledMap = new TmxMapLoader().load(defaultMapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);
        parseCollidableTiles();

        mapPaths = new HashMap<>();
        mapPaths.put("Library", "maps/library.tmx");
        mapPaths.put("Cafe", "maps/cafe.tmx");
        mapPaths.put("Cinema", "maps/cinema.tmx");
        mapPaths.put("Home", "maps/home.tmx");
        mapPaths.put("Computer Science Building", "maps/compSci.tmx");
        mapPaths.put("Campus", "maps/campus_east.tmx");

    }



    private void parseCollidableTiles() {
        if (tiledMap.getLayers().get("collisions") != null) {

            MapObjects objects = tiledMap.getLayers().get("collisions").getObjects();
            for (MapObject object : objects) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    collidableTiles.add(new Rectangle(rect.x * SCALE, rect.y * SCALE, rect.width * SCALE, rect.height * SCALE));
                }
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

    private void parseActivityTiles() {
        if (tiledMap.getLayers().get("activities") != null) {
            MapObjects objects = tiledMap.getLayers().get("activities").getObjects();
            for (MapObject object : objects) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();

                    Rectangle scaledRect = new Rectangle(rect.x * SCALE, rect.y * SCALE, rect.width * SCALE, rect.height * SCALE);

                    Activity activity = createActivityForName(object.getName());

                    if (activity != null) {
                        activityTiles.add(new ActivityTile(scaledRect, activity));
                    }
                }
            }
        }
    }

    private Activity createActivityForName(String name) {
        switch (name) {
            case "eat":
                return new Eat();

            case "study":
                return new Study();

            case "relax":
                return new Relax();

            case "sleep":
                return new Sleep();

            default:
                return null;
        }
    }

    public Array<Rectangle> getCollidableTiles() {
        return collidableTiles;
    }

    public Array<Rectangle> getExitTiles() {
        return exitTiles;
    }

    public Array<ActivityTile> getActivityTiles() {
        return activityTiles;
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void changeMap(String newMapPath) {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        currentMapPath = newMapPath;
        tiledMap = new TmxMapLoader().load(newMapPath);
        mapRenderer.setMap(tiledMap);
        collidableTiles.clear();
        parseCollidableTiles();
        parseExitTiles();
        parseActivityTiles();
    }

    public void changeMapToCampus() {
        if (tiledMap!= null) {
            tiledMap.dispose();
        }
        currentMapPath = defaultMapPath;
        tiledMap = new TmxMapLoader().load(defaultMapPath);
        mapRenderer.setMap(tiledMap);
        collidableTiles.clear();
        activityTiles.clear();
        parseCollidableTiles();
    }

    public String getMapPath(String mapName) {
        return mapPaths.get(mapName);
    }


    public void renderOverlay(OrthographicCamera camera, String layerName) {
        if (currentMapPath.equals(defaultMapPath)) {
            int layerIndex = tiledMap.getLayers().getIndex(layerName);
            mapRenderer.setView(camera);
            mapRenderer.render(new int[] {layerIndex});
        }
    }

    public void displayEndMap() {
        if (tiledMap!= null) {
            tiledMap.dispose();
        }
        currentMapPath = "maps/end_game.tmx";
        tiledMap = new TmxMapLoader().load("maps/end_game.tmx");
        mapRenderer.setMap(tiledMap);
        collidableTiles.clear();

    }
}

