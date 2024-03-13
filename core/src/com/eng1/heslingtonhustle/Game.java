package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.eng1.heslingtonhustle.activities.Studying;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Game extends ApplicationAdapter {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int SCALE = 5;
    private static final int PLAYER_SIZE = 32 * SCALE;
    private final List<Building> buildings = new ArrayList<>();
    private final Array<Rectangle> collidableTiles = new Array<>();
    private final Day day = new Day();
    private ShaderProgram shader;
    private SpriteBatch batch;
    private Vector2 playerPosition;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Movement playerMovement;
    private Dialog dialog;
    private Stage stage;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;


    @Override
    public void create() {
        cameraSetup();
        shaderSetup();
        Vector2 spawn = new Vector2(4608,960);
        playerMovement = new Movement(spawn, 3200);
        playerMovement.setCollidableTiles(collidableTiles);
        stage = new Stage(viewport);

        inputSetup();

        tiledMap = new TmxMapLoader().load("maps/campus_east.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);

        parseCollidableTiles();
        System.out.println(tiledMap.getLayers().get("buildingCorners").getObjects().get(1).getProperties().get("name"));


        Gson gson = new Gson();
        Map<String, Building> buildingMap = new HashMap<>();
        try (FileReader reader = new FileReader("buildings.json")) {
            BuildingInfo[] buildingInfos = gson.fromJson(reader, BuildingInfo[].class);
            for(BuildingInfo buildingInfo: buildingInfos){
                buildingMap.put(buildingInfo.id,new Building(buildingInfo));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


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


    private void creatDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        dialog = new Dialog("Are you sure you want to go to " + building.getName() + "?", skin) {
            public void result(Object obj) {
                System.out.println("result " + obj);
                if ((boolean) obj) {
                    day.addActivity(new Studying());
                    System.out.println(day.getTotalDuration());
                    System.out.println(day.getTotalEnergyUsage());
                    System.out.println(playerPosition);
                }
            }
        };

        dialog.text("It will take X time and use X% of your energy");
        dialog.button("Yes", true);
        dialog.button("No", false);
    }

    private void inputSetup() {
        InputHandler inputHandler = new InputHandler(playerMovement.getPlayerState());
        InputMultiplexer inputMultiplexer = new InputMultiplexer(inputHandler, stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void cameraSetup() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
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
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();




        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //renderGameWorld();

        boolean pressedE = (playerMovement.getPlayerState().isINTERACTING());

        boolean buildingOutlined = false;
        for (Building building : buildings) {

            if (building.inRange(playerPosition) && !buildingOutlined) {
                outlineBuilding(building);
                buildingOutlined = true;
                if (pressedE) {
                    playerMovement.getPlayerState().stopInteracting();
                    pressedE = false;
                    creatDialog(building);
                    // Building has been interacted with

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
            } else {
                renderTexture(building.getTextureRegion(), building.getPosition());
            }
        }
        for(Building building:buildings){
            Vector2 interactSpot =  building.getInteractSpot();

            batch.draw(SpriteSheet.getDebug(),interactSpot.x,interactSpot.y,32*SCALE,32*SCALE);
        }

        batch.draw(currentFrame, (playerPosition.x - PLAYER_SIZE / 2f), (playerPosition.y - PLAYER_SIZE / 2f) + 60, PLAYER_SIZE, PLAYER_SIZE);
        batch.end();

        stage.act();
        stage.draw();
    }


    private void updateCameraPosition() {
        camera.position.set(playerPosition.x + ((float) PLAYER_SIZE / 2), playerPosition.y + ((float) PLAYER_SIZE / 2), 0);
        camera.update();
    }

    // Method no longer used, as tmx file is used instead of texture
    //private void renderGameWorld() {
    //    Texture map = SpriteSheet.getMap();
    //    batch.draw(map, (float) (-map.getWidth() * SCALE) / 2, (float) (-map.getHeight() * SCALE) / 2, map.getWidth() * SCALE, map.getHeight() * SCALE);
    //}



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

    //TODO created Render Class
    private void renderTexture(TextureRegion textureRegion, Vector2 position) {
        renderTexture(textureRegion, position, SCALE, SCALE, false);
    }


    private float calculateOffset(float length, float scale) {
        return length * (scale - SCALE) / 2f;
    }


    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();
        tiledMap.dispose();
        mapRenderer.dispose();
    }
}
