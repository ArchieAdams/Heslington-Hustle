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
    private Day day = new Day();
    private Vector2 respawnLocation;
    private GameUI gameUI;
    private boolean playerInBuilding = false;

    private final RenderingManager renderingManager;
    private Rectangle rectangle;

    public GameManager(Stage stage, MapManager mapManager, PlayerManager playerManager, BuildingManager buildingManager, RenderingManager renderManager) {
        this.stage = stage;
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.buildingManager = buildingManager;
        playerManager.setCurrentDay(day);
        this.renderingManager = renderManager;
    }

    public Building checkForBuildingInRange() {
        List<Building> buildings = buildingManager.getCampusBuildings();
        Vector2 position = playerManager.getPosition();
        for (Building building : buildings) {
            if (building.inRange(position)) {
                return building;
            }
        } return null;
    }

    public void interactWithBuilding(Building building,Movement player) {
        Vector2 playerPosition = player.getPosition();
        if (player.getPlayerState().isINTERACTING()) {
            player.getPlayerState().stopInteracting();
            playerManager.getMovement().disableMovement();
            Dialog dialog = createDialog(building);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
                    dialog.setPosition(playerPosition.x - 175, playerPosition.y + 50);
                    dialog.setSize(400, 100);
                }
            }, 0);
        }
    }

    private Dialog createDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
        String buildingToEnter = building.getName();
        playerManager.getMovement().disableMovement();
        Dialog dialog = new Dialog("Enter building?", skin) {
            public void result(Object obj) {
                if ((boolean) obj) {
                    enterBuilding(buildingToEnter);
                }
                playerManager.getMovement().enableMovement();
            }
        };

        dialog.text("Would you like to enter " + buildingToEnter + "?");
        dialog.button("Yes", true);
        dialog.button("No", false);

        return dialog;
    }

    private void enterBuilding(String buildingName) {
        String newMapPath = mapManager.getMapPath(buildingName);
        respawnLocation = new Vector2(playerManager.getPosition());
        playerInBuilding = true;
        mapManager.changeMap(newMapPath);
        buildingManager.makeBuildingsDisappear();
        playerManager.movement.setPosition(new Vector2(400, 100));
    }

    private boolean playerInExitZone(Vector2 position) {
        for (Rectangle exitZone : mapManager.getExitTiles()) {
            if (exitZone.contains(position.x, position.y)) {
                rectangle = exitZone;
                return true;
            }
        }
        return false;
    }

    private void exitBuilding() {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            playerInBuilding = false;
            mapManager.changeMapToCampus();
            playerManager.movement.setPosition(respawnLocation);
            buildingManager.makeBuildingsAppear();
        }
    }

    private ActivityTile playerInActivityZone(Vector2 position) {
        for (ActivityTile activityZone : mapManager.getActivityTiles()) {
            if (activityZone.getRectangle().contains(position.x, position.y)) {
                rectangle = activityZone.getRectangle();
                return activityZone;
            }
        }
        //rectangle = null;
        return null;
    }

    private void askToDoActivity(ActivityTile activityTile) {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            Vector2 playerPosition = playerManager.getPosition();
            Skin skin = new Skin(Gdx.files.internal("assets/skin/default/uiskin.json"));
            Activity activity = activityTile.getActivity();
            playerManager.getMovement().disableMovement();
            Dialog dialog = new Dialog("Activity", skin) {
                @Override
                protected void result(Object object) {
                    boolean choice = (Boolean) object;
                    int energyRequired = activity.getEnergyUsagePercent();
                    int timeRequired = activity.getDurationHours();
                    if (choice && playerManager.canPerformActivity(energyRequired, timeRequired)) {
                        activity.onPerform(playerManager);
                    }

                    playerManager.getMovement().enableMovement();
                }
            };
            String activityName = activity.getName();
            String activityNameCapitalized = activityName.substring(0, 1).toUpperCase() + activityName.substring(1);
            int timeUsed = activity.getDurationHours();
            int energyUsed = activity.getEnergyUsagePercent();
            dialog.text("Would you like to " + activityName + "? \n" +
                    activityNameCapitalized + "ing will take " + timeUsed + " hours and use " + energyUsed + "% of your energy");
            dialog.button("Yes", true);
            dialog.button("No", false);
            dialog.show(stage);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
                    dialog.setPosition(playerPosition.x - 175, playerPosition.y + 50);
                    dialog.setSize(450, 100);
                }
            }, 0);
        }
    }

    public void endGame() {
        buildingManager.makeBuildingsDisappear();
        playerManager.movement.setPosition(new Vector2(900, 700));
        mapManager.displayEndMap();
        renderingManager.getGameUI().dispose();
        renderingManager.hidePlayer();
        playerManager.getMovement().disableMovement();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void update() {
        if (!playerInBuilding) {
            Building building = checkForBuildingInRange();
            if (building!= null) {
                interactWithBuilding(building, playerManager.getMovement());
            }
        }

        if (playerInBuilding) {
            if (!playerInExitZone(playerManager.getPosition()) && !playerInExitZone(playerManager.getPosition())) {
                rectangle = null;
            }
            if (playerInExitZone(playerManager.getPosition())) {
                exitBuilding();
            }
            ActivityTile activityTile = playerInActivityZone(playerManager.getPosition());
            if (activityTile != null) {
                askToDoActivity(activityTile);
                if (playerManager.gameOver()) {
                    endGame();
                }
            }
        }
    }

}
