package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class Game extends ApplicationAdapter {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int SCALE = 5;

    private static final int PLAYER_SIZE = 32 * SCALE;
    private final List<Building> buildings = new ArrayList<>();
    ShaderProgram shader;
    private SpriteBatch batch;
    private Vector2 playerPosition;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayerMovement playerMovement;

    @Override
    public void create() {
        cameraSetup();
        shaderSetup();
        playerMovement = new PlayerMovement(new Vector2(0, 0), 320);
        inputSetup();

        buildings.add(new Building("School", new Vector2(0, 0), SpriteSheet.getSchool()));
        buildings.add(new Building("Hotel", new Vector2(-500, -500), SpriteSheet.getSchool()));

    }

    private void inputSetup() {
        InputHandler inputHandler = new InputHandler(playerMovement.getPlayerState());
        Gdx.input.setInputProcessor(inputHandler);
    }

    private void cameraSetup() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
    }

    private void shaderSetup() {
        String vertexShader = Gdx.files.internal("vertexShader.glsl").readString();
        String fragmentShader = Gdx.files.internal("fragmentShader.glsl").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        shader.bind();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_mask", 1);
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

        boolean pressedE = (playerMovement.getPlayerState().isINTERACTING());


        for (Building building : buildings) {

            if (building.inRange(playerPosition)) {
                outlineBuilding(building.getTexture(), building.getPosition());
                if (pressedE) {
                    // Building has been interacted with
                    System.out.println(building.getName());
                }
            } else {
                renderTexture(building.getTexture(), building.getPosition());
            }
        }
        playerMovement.getPlayerState().stopInteracting();

        batch.draw(currentFrame, playerPosition.x - PLAYER_SIZE / 2f, playerPosition.y - PLAYER_SIZE / 2f, PLAYER_SIZE, PLAYER_SIZE);
        batch.end();
    }


    private void updateCameraPosition() {
        camera.position.set(playerPosition.x + ((float) PLAYER_SIZE / 2), playerPosition.y + ((float) PLAYER_SIZE / 2), 0);
        camera.update();
    }

    private void renderGameWorld() {
        Texture map = SpriteSheet.getMap();
        batch.draw(map, (float) (-map.getWidth() * SCALE) / 2, (float) (-map.getHeight() * SCALE) / 2, map.getWidth() * SCALE, map.getHeight() * SCALE);
    }

    private void outlineBuilding(Texture texture, Vector2 position) {
        batch.setShader(shader);
        texture.bind(1);
        renderTexture(texture, position, SCALE + (SCALE / 40f), SCALE + (SCALE / 20f), true);
        texture.bind(0);
        batch.setShader(null);
        renderTexture(texture, position);
    }

    //TODO created Render Class
    private void renderTexture(Texture texture, Vector2 position) {
        renderTexture(texture, position, SCALE, SCALE, false);
    }

    private void renderTexture(Texture texture, Vector2 position, float scaleX, float scaleY, boolean alignCenter) {
        // Calculate the scaled width and height of the texture
        float width = texture.getWidth() * scaleX;
        float height = texture.getHeight() * scaleY;

        // Calculate the position based on alignment
        float x = position.x;
        float y = position.y;
        if (alignCenter) {
            x -= calculateOffset(texture.getWidth(), scaleX);
            y -= calculateOffset(texture.getHeight(), scaleY);
        }

        // Draw the texture
        batch.draw(texture, x, y, width, height);
    }

    private float calculateOffset(float length, float scale) {
        return length * (scale - SCALE) / 2f;
    }


    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();
    }
}
