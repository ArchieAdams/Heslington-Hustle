package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.eng1.heslingtonhustle.activities.Activity;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameManager {
    private final Stage stage;
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final BuildingManager buildingManager;
    private final RenderingManager renderingManager;
    private Vector2 respawnLocation;
    private boolean playerInBuilding = false;
    private Building currentBuilding;

    public GameManager(Stage stage, MapManager mapManager, PlayerManager playerManager, BuildingManager buildingManager, RenderingManager renderManager) {
        this.stage = stage;
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.buildingManager = buildingManager;
        Day day = new Day();
        playerManager.setCurrentDay(day);
        this.renderingManager = renderManager;
    }


    private Building checkForBuildingInRange() {
        List<Building> buildings = buildingManager.getCampusBuildings();
        Vector2 position = playerManager.getPosition();
        for (Building building : buildings) {
            if (building.inRange(position)) {
                return building;
            }
        }
        return null;
    }

    private void interactWithBuilding(Building building) {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            enterBuilding(building);
        }
    }


    private void showErrorDialog(String label) {
        Dialog dialog = createDialog(label);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                dialog.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.1f, Interpolation.fade), Actions.delay(.25f), Actions.fadeOut(0.1f, Interpolation.fade)));
            }
        }, 0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                playerManager.getState().leftMenu();
            }
        }, .45f);
    }

    private Dialog createDialog(String label) {
        Skin skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
        Dialog dialog = new Dialog("Can't do activity.", skin);
        dialog.text(label);
        dialog.setSize(200, 100);
        dialog.setPosition(playerManager.getPosition().x - 100, playerManager.getPosition().y + 50);
        return dialog;
    }

    private void enterBuilding(Building building) {
        playerManager.getState().inMenu();
        String newMapPath = mapManager.getMapPath(building.getName());
        respawnLocation = new Vector2(playerManager.getPosition());
        playerInBuilding = true;
        currentBuilding = building;
        mapManager.changeMap(newMapPath);
        buildingManager.makeBuildingsDisappear();
        playerManager.movement.setPosition(new Vector2(400, 150));
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                playerManager.getState().leftMenu();
            }
        }, .05f);
    }

    private boolean playerInExitZone(Vector2 position) {
        for (Rectangle exitZone : mapManager.getExitTiles()) {
            if (exitZone.contains(position.x, position.y)) {
                return true;
            }
        }
        return false;
    }

    private void exitBuilding() {
        if (playerManager.getState().isINTERACTING() && playerInExitZone(playerManager.getPosition())) {
            playerManager.getState().stopInteracting();
            playerInBuilding = false;
            currentBuilding = null;
            mapManager.changeMapToCampus();
            playerManager.movement.setPosition(respawnLocation);
            buildingManager.makeBuildingsAppear();
        }
    }

    private ActivityTile playerInActivityZone(Vector2 position) {
        for (ActivityTile activityZone : mapManager.getActivityTiles()) {
            if (activityZone.getRectangle().contains(position.x, position.y)) {
                return activityZone;
            }
        }
        return null;
    }



    private void handleActivity(Activity activity) {
        boolean performed = activity.perform(playerManager);
        if (!performed) {
            showErrorDialog("Can't perform activity.");
        } else {
            playerManager.getState().leftMenu();
            if (playerManager.gameOver()) {
                endGame();
            }
        }
    }

    public void endGame() {
        mapManager.displayEndMap();
        buildingManager.makeBuildingsDisappear();
        playerManager.movement.setPosition(new Vector2(900, 1900));
        mapManager.displayEndMap();
        renderingManager.hidePlayer();
        playerManager.getMovement().disableMovement();
        playerManager.getState().inMenu();
        renderingManager.getGameUI().showScore(playerManager.getWeek());
    }

    public void update() {
        Building building = checkForBuildingInRange();
        if (checkForBuildingInRange() != null) {
            interactWithBuilding(building);
        }

        if (playerInBuilding) {
            if (playerInExitZone(playerManager.getPosition())) {
                exitBuilding();
                return;
            }
            ActivityTile activityTile = playerInActivityZone(playerManager.getPosition());
            if (activityTile != null) {
                askToDoActivity(currentBuilding.getActivity());
                renderingManager.getGameUI().updateProgressBar();
            }
        }
    }

    private void askToDoActivity(Activity activity) {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            playerManager.getState().inMenu();
            Vector2 playerPosition = playerManager.getPosition();
            Skin skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
            Dialog dialog = new Dialog("Activity", skin) {
                @Override
                protected void result(Object object) {
                    boolean choice = (Boolean) object;
                    if (!choice) {
                        playerManager.getState().leftMenu();
                        return;
                    }
                    handleActivity(activity);
                }
            };
            dialog.text(activity.toString());
            dialog.button("Yes", true);
            dialog.button("No", false);
            dialog.show(stage);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
                    dialog.setPosition(playerPosition.x - 225, playerPosition.y + 50);
                    dialog.setSize(450, 100);
                }
            }, 0);
        }
    }
}
