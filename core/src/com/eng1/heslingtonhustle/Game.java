package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends ApplicationAdapter {

    static final int SCALE = 5;
    static final int PLAYER_SIZE = 32 * SCALE;
    private List<Building> buildings = new ArrayList<>();
    private PlayerManager playerManager;
    private Stage stage;
    private RenderingManager renderingManager;
    private CameraManager cameraManager;

    private static Map<String, Building> loadBuildingInfo() {
        Gson gson = new Gson();
        Map<String, Building> buildingMap = new HashMap<>();
        try (FileReader reader = new FileReader("buildings.json")) {
            BuildingInfo[] buildingInfos = gson.fromJson(reader, BuildingInfo[].class);
            for (BuildingInfo buildingInfo : buildingInfos) {
                buildingMap.put(buildingInfo.id, new Building(buildingInfo));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildingMap;
    }

    @Override
    public void create() {
        cameraManager = new CameraManager();
        MapManger mapManger = new MapManger();
        stage = new Stage(cameraManager.getViewport());
        renderingManager = new RenderingManager(stage, cameraManager, mapManger);

        Vector2 spawn = new Vector2(4608, 960);
        playerManager = new PlayerManager(spawn, 320);
        playerManager.getMovement().setCollidableTiles(mapManger.getCollidableTiles());

        inputSetup();

        Map<String, Building> buildingMap = loadBuildingInfo();
        buildings = mapManger.createBuildings(buildingMap);
    }


    private void inputSetup() {
        InputHandler inputHandler = new InputHandler(playerManager.getState());
        InputMultiplexer inputMultiplexer = new InputMultiplexer(inputHandler, stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }


    @Override
    public void resize(int width, int height) {
        cameraManager.getViewport().update(width, height, true);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        playerManager.getMovement().update(Gdx.graphics.getDeltaTime());

        renderingManager.render(buildings, playerManager.getMovement());
        stage.act();
        stage.draw();
    }


}
