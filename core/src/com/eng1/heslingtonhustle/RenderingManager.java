package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.eng1.heslingtonhustle.activities.Studying;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class RenderingManager {

    private static final float SCALE = 5f;
    private final SpriteBatch batch;
    private final Stage stage;
    private ShaderProgram shader;
    private final Day day = new Day();
    private Dialog dialog;
    private final CameraManager cameraManager;
    private final MapManager mapManager;
    private final Stage uiStage;

    private final PlayerManager playerManager;

    private Vector2 respawnLocation;


    public RenderingManager( Stage stage, CameraManager cameraManager, MapManager mapManager, PlayerManager playerManager ) {
        this.batch = new SpriteBatch();
        this.stage = stage;
        shaderSetup();
        this.cameraManager = cameraManager;
        this.mapManager = mapManager;
        this.uiStage = new Stage(new ScreenViewport(), batch);
        GameUI gameUI = new GameUI(uiStage);
        this.playerManager = playerManager;
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
        shader.setUniformi("u_texture", 1); // Assuming texture unit 0
        shader.setUniformi("u_mask",1);
    }

    public void render(List<Building> buildings, Movement playerMovement) {
        cameraManager.render(batch, mapManager,playerMovement.getPosition());
        batch.begin();
        renderBuildings(buildings,playerMovement);
        renderPlayer(playerMovement);
        batch.end();

        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
        //uiStage.dispose();
    }


    private void renderBuildings(List<Building> buildings,Movement player) {

        for (Building building : buildings) {
            if (building.inRange(player.getPosition())) {
                outlineBuilding(building);
                interactWithBuilding(building,player);
            } else {
                renderBuilding(building);
            }
        }


        for(Building building:buildings){
            Vector2 interactSpot =  building.getInteractSpot();

            batch.draw(SpriteSheet.getDebug(),interactSpot.x,interactSpot.y,32*SCALE,32*SCALE);
        }
    }

    private void createDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
        String buildingToEnter = building.getName();
        dialog = new Dialog("Are you sure you want to go to " + buildingToEnter + "?", skin) {
            public void result(Object obj) {
                System.out.println("result " + obj);
                if ((boolean) obj) {
                    day.addActivity(new Studying());
                    System.out.println(day.getTotalDuration());
                    System.out.println(day.getTotalEnergyUsage());
                    enterBuilding(buildingToEnter);
                }
            }
        };

        dialog.text("It will take X time and use X% of your energy");
        dialog.button("Yes", true);
        dialog.button("No", false);
    }

    private void interactWithBuilding(Building building,Movement player) {
        Vector2 playerPosition = player.getPosition();
        if (player.getPlayerState().isINTERACTING()) {
            player.getPlayerState().stopInteracting();
            createDialog(building);
            System.out.println(building.getName());
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
                    dialog.setPosition(playerPosition.x - 175, playerPosition.y + 50);
                    dialog.setSize(350, 100);
                }
            }, 0);
        }
    }

    private void renderBuilding(Building building) {
        renderTexture(building.getTextureRegion(), building.getPosition());
    }

    private void outlineBuilding(Building building) {
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
        TextureRegion currentFrame = playerMovement.getCurrentFrame();
        Vector2 playerPosition = playerMovement.getPosition();
        float PLAYER_SIZE = 32*SCALE;
        batch.draw(currentFrame, (playerPosition.x - PLAYER_SIZE / 2f), (playerPosition.y - PLAYER_SIZE / 2f) + 60, PLAYER_SIZE, PLAYER_SIZE);
    }

    private void enterBuilding(String buildingName) {
        String newMapPath = mapManager.getMapPath(buildingName);
        mapManager.changeMap(newMapPath);
        respawnLocation = playerManager.getPosition();
        playerManager.movement.setPosition(new Vector2(400, 100));
    }
}
