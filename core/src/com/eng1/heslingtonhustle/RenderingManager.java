package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;


public class RenderingManager {

    private static final float SCALE = 5f;
    private final SpriteBatch batch;

    private ShaderProgram shader;
    private final CameraManager cameraManager;
    private final MapManager mapManager;
    private final Stage uiStage;
    private final GameUI gameUI;
    private boolean playerVisible = true;
    private ShaderProgram skyShader;
    private float brightness;
    private float hue;




    public RenderingManager(CameraManager cameraManager, MapManager mapManager,PlayerManager playerManager) {


        ShaderProgram.pedantic = false;
        skyShader = new ShaderProgram(
                Gdx.files.internal("shader/brightness_hue_shader.vert"),
                Gdx.files.internal("shader/brightness_hue_shader.frag")
        );
        if (!skyShader.isCompiled()) {
            Gdx.app.error("Shader", "Shader compilation failed: " + skyShader.getLog());
        }

        brightness = 1.0f; // Initial brightness
        hue = 0.0f; // Initial hue angle in degrees

        this.batch = new SpriteBatch();
        shaderSetup();
        this.cameraManager = cameraManager;
        this.mapManager = mapManager;
        this.uiStage = new Stage(new ScreenViewport(), batch);
        this.gameUI = new GameUI(uiStage,playerManager);
    }

    private void shaderSetup() {
        String vertexShader = Gdx.files.internal("shader/vertexShader.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/fragmentShader.glsl").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Error compiling shader: " + shader.getLog());
            return;
        }

        // Set the texture uniform
        shader.setUniformi("u_texture", 1);
        shader.setUniformi("u_mask",1);
    }

    public void render(List<Building> buildings, PlayerManager playerManager) {
        Movement playerMovement = playerManager.getMovement();
        cameraManager.render(batch, mapManager,playerMovement.getPosition());


        batch.begin();



        renderBuildings(buildings,playerMovement);
        renderPlayer(playerMovement);
        mapManager.renderOverlay(cameraManager.getCamera(), "overlay");

        brightness = calculateBrightness(playerManager.getTime().getTime());
        batch.setColor(1, 1, 1, 1 - brightness);
        batch.draw(ResourceLoader.getOverlay(), playerMovement.getPosition().x-Gdx.graphics.getWidth()/2f,
                playerMovement.getPosition().y-Gdx.graphics.getHeight()/2f
                , Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);

        batch.end();





        gameUI.updateProgressBar();
        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();

    }

    // Function to calculate brightness based on time of day
    private float calculateBrightness(int timeOfDay) {
        // Calculate the rate of change in brightness over time
        // For example, you might want the brightness to decrease by a certain amount per hour
        float initialBrightness = 1.0f; // Initial brightness at time 8 (morning)
        float finalBrightness = 0.2f; // Final brightness at time 24 (night)
        float totalHours = 24 - 8; // Total hours from morning to night
        float hoursElapsed = timeOfDay - 8; // Number of hours passed since morning
        float rateOfChange = (initialBrightness - finalBrightness) / totalHours; // Rate of change per hour
        float currentBrightness = initialBrightness - rateOfChange * hoursElapsed;

        // Ensure the brightness stays within the range [0, 1]
        return Math.max(finalBrightness, Math.min(initialBrightness, currentBrightness));
    }



    private void renderBuildings(List<Building> buildings,Movement player) {

        for (Building building : buildings) {
            if (!building.isVisible()) {
                continue;
            }
            if (building.inRange(player.getPosition())) {

                outlineBuilding(building);
            } else {
                renderBuilding(building);
            }
            boolean DEBUG = false;
            if (DEBUG) {
                Vector2 interactSpot =  building.getInteractSpot();

                batch.draw(ResourceLoader.getDebug(),interactSpot.x,interactSpot.y,32*SCALE,32*SCALE);
            }
        }
    }


    private void renderBuilding(Building building) {
        renderTexture(building.getTextureRegion(), building.getPosition());
    }

    public void outlineBuilding(Building building) {
        TextureRegion textureRegion = building.getTextureRegion();
        Vector2 position = building.getPosition();
        float scaleX = SCALE + (SCALE / 40f);
        float scaleY = SCALE + (SCALE / 20f);

        batch.setShader(shader);
        textureRegion.getTexture().bind(1);
        renderTexture(textureRegion, position, scaleX, scaleY, true);
        textureRegion.getTexture().bind(0);
        batch.setShader(null);
        renderTexture(textureRegion, position, SCALE, SCALE, false);
    }


    private void renderTexture(TextureRegion textureRegion, Vector2 position, float scaleX, float scaleY, boolean outline) {
        float x = position.x;
        float y = position.y;
        float width = textureRegion.getRegionWidth() * scaleX;
        float height = textureRegion.getRegionHeight() * scaleY;

        if (outline) {
            x -= calculateOffset(textureRegion.getRegionWidth(), scaleX);
            y -= calculateOffset(textureRegion.getRegionHeight(), scaleY);
        }

        batch.draw(textureRegion, x, y, width, height);
    }

    private void renderTexture(TextureRegion textureRegion, Vector2 position) {
        renderTexture(textureRegion, position, SCALE, SCALE, false);
    }


    private float calculateOffset(float length, float scale) {
        return length * (scale - SCALE) / 2f;
    }

    private void renderPlayer(Movement playerMovement) {
        if (playerVisible) {
            TextureRegion currentFrame = playerMovement.getCurrentFrame();
            Vector2 playerPosition = playerMovement.getPosition();
            float PLAYER_SIZE = 32*SCALE;
            batch.draw(currentFrame, (playerPosition.x - PLAYER_SIZE / 2f), (playerPosition.y - PLAYER_SIZE / 2f) + 60, PLAYER_SIZE, PLAYER_SIZE);
        }
    }

    GameUI getGameUI() {
        return gameUI;
    }

    public void hidePlayer() {
        playerVisible = false;
    }


}
