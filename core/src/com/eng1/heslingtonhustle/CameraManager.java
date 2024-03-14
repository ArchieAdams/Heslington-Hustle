package com.eng1.heslingtonhustle;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CameraManager {

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    public CameraManager() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
    }

    private void updateCameraPosition(Vector2 playerPosition) {
        int PLAYER_SIZE = Game.PLAYER_SIZE;
        camera.position.set(playerPosition.x + (PLAYER_SIZE / 2f),
                playerPosition.y + (PLAYER_SIZE / 2f), 0);
        camera.update();
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public void render(SpriteBatch batch, MapManager mapManager, Vector2 playerPosition) {
        updateCameraPosition(playerPosition);
        camera.update();
        mapManager.render(camera);

        batch.setProjectionMatrix(camera.combined);
    }
}
