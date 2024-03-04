package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends ApplicationAdapter {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int PLAYER_SIZE = 320;

    private SpriteBatch batch;
    private Vector2 playerPosition;
    private OrthographicCamera camera;
    private Viewport viewport;

    private PlayerMovement playerMovement;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);

        playerMovement = new PlayerMovement(new Vector2(0, 0), 320);
        InputHandler inputHandler = new InputHandler(playerMovement.getPlayerState());
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        playerMovement.update(Gdx.graphics.getDeltaTime());
        playerPosition = playerMovement.getPosition();
        TextureRegion currentFrame = playerMovement.getCurrentFrame();

        updateCameraPosition();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderGameWorld();
        batch.draw(currentFrame, playerPosition.x, playerPosition.y, PLAYER_SIZE, PLAYER_SIZE);
        batch.end();
    }


    private void updateCameraPosition() {
        camera.position.set(playerPosition.x + ((float) PLAYER_SIZE / 2), playerPosition.y + ((float) PLAYER_SIZE / 2), 0);
        camera.update();
    }

    private void renderGameWorld() {
        Texture map = SpriteSheet.getMap();
        batch.draw(map, -map.getWidth() * 5, -map.getHeight() * 5, map.getWidth() * 10, map.getHeight() * 10);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
