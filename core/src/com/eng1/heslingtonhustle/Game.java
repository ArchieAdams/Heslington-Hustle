package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.eng1.heslingtonhustle.building.Building;
import com.eng1.heslingtonhustle.building.BuildingManager;
import com.eng1.heslingtonhustle.graphics.CameraManager;
import com.eng1.heslingtonhustle.graphics.RenderingManager;
import com.eng1.heslingtonhustle.map.MapManager;
import com.eng1.heslingtonhustle.player.InputHandler;
import com.eng1.heslingtonhustle.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class Game extends ApplicationAdapter {

    public static final int SCALE = 5;
    public static final int PLAYER_SIZE = 32 * SCALE;
    private List<Building> buildings = new ArrayList<>();
    private PlayerManager playerManager;
    private Stage stage;
    private RenderingManager renderingManager;
    private CameraManager cameraManager;
    private GameManager gameManager;


    @Override
    public void create() {
        Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bgtrack.mp3"));
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(.5f);
        cameraManager = new CameraManager();
        MapManager mapManager = new MapManager();
        BuildingManager buildingManager = new BuildingManager();
        stage = new Stage(cameraManager.getViewport());

        Vector2 spawn = new Vector2(4608, 960);
        playerManager = new PlayerManager(spawn, 320*2);
        playerManager.getMovement().setCollidableTiles(mapManager.getCollidableTiles());

        inputSetup();

        buildings = buildingManager.getCampusBuildings();
        renderingManager = new RenderingManager(cameraManager, mapManager, playerManager);
        gameManager = new GameManager(stage, mapManager, playerManager, buildingManager, renderingManager);
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

        float deltaTime = Gdx.graphics.getDeltaTime();

        playerManager.getMovement().update(deltaTime);
        gameManager.update();
        renderingManager.render(buildings, playerManager);
        stage.act();
        stage.draw();

    }




}
