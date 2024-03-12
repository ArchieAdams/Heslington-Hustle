package com.eng1.heslingtonhustle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Game extends ApplicationAdapter {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int SCALE = 5;
    private static final int PLAYER_SIZE = 32 * SCALE;
    private final List<Building> buildings = new ArrayList<>();
    private ShaderProgram shader;
    private SpriteBatch batch;
    private Vector2 playerPosition;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayerMovement playerMovement;
    private Dialog dialog;
    private Stage stage;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<Rectangle> collidableTiles = new Array<>();


    @Override
    public void create() {
        cameraSetup();
        shaderSetup();
        playerMovement = new PlayerMovement(new Vector2(0, 0), 320);
        playerMovement.setCollidableTiles(collidableTiles);
        stage = new Stage(viewport);

        inputSetup();

        tiledMap = new TmxMapLoader().load("assets/maps/campus_east.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);

        parseCollidableTiles();


        buildings.add(new Building("School", new Vector2(0, 0), SpriteSheet.getSchool()));
        buildings.add(new Building("Hotel", new Vector2(-500, -500), SpriteSheet.getSchool()));
    }

    private void parseCollidableTiles() {
        collidableTiles.clear(); // Clear previous data if any
        // Identify all collidable tiles and handle bridges
        for (TiledMapTileLayer layer : tiledMap.getLayers().getByType(TiledMapTileLayer.class)) {
            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    // Add tiles with collisions to array
                    if (cell != null && cell.getTile() != null) {
                        if (cell.getTile().getProperties().containsKey("collidable") && cell.getTile().getProperties().get("collidable", Boolean.class)) {
                            collidableTiles.add(new Rectangle(x * layer.getTileWidth() * SCALE, y * layer.getTileHeight() * SCALE, layer.getTileWidth() * SCALE, layer.getTileHeight() * SCALE));
                        }
                        // Remove tiles from collision array, where a bridge goes over a collidable tile
                        else if (cell.getTile().getProperties().containsKey("bridge")) {
                            final float posX = x * layer.getTileWidth() * SCALE;
                            final float posY = y * layer.getTileHeight() * SCALE;
                            for (int i = collidableTiles.size - 1; i >= 0; i--) {
                                Rectangle rect = collidableTiles.get(i);
                                if (Math.abs(rect.x - posX) < 0.0001 && Math.abs(rect.y - posY) < 0.0001) {
                                    collidableTiles.removeIndex(i);
                                    break; // Assuming only one collidable tile can exist at any position
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void creatDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        dialog = new Dialog("Are you sure you want to go to " + building.getName() + "?", skin) {
            public void result(Object obj) {
                System.out.println("result " + obj);
                if ((boolean) obj) {
                    Gdx.app.exit();
                    System.exit(-1);
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

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        updateCameraPosition();
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
                            dialog.setPosition(playerPosition.x-175, playerPosition.y+50);
                            dialog.setSize(350, 100);
                        }
                    }, 0);
                }
            } else {
                renderTexture(building.getTexture(), building.getPosition());
            }
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
        Texture texture = building.getTexture();
        Vector2 position = building.getPosition();
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
        tiledMap.dispose();
        mapRenderer.dispose();
    }
}
