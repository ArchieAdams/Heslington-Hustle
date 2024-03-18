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
import com.eng1.heslingtonhustle.activities.Study;


import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameManager {
    private final Stage stage;
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final BuildingManager buildingManager;
    private final Day day = new Day();
    private Vector2 respawnLocation;
    private boolean playerInBuilding = false;

    public GameManager(Stage stage, MapManager mapManager, PlayerManager playerManager, BuildingManager buildingManager) {
        this.stage = stage;
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.buildingManager = buildingManager;
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
            Dialog dialog = createDialog(building);
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

    private Dialog createDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("assets/skin/default/uiskin.json"));
        String buildingToEnter = building.getName();
        Dialog dialog = new Dialog("Are you sure you want to go to " + buildingToEnter + "?", skin) {
            public void result(Object obj) {
                System.out.println("result " + obj);
                if ((boolean) obj) {
                    day.addActivity(new Study());
                    System.out.println(day.getTotalDuration());
                    System.out.println(day.getTotalEnergyUsage());
                    enterBuilding(buildingToEnter);
                }
            }
        };

        dialog.text("It will take X time and use X% of your energy");
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
                return true;
            }
        }
        return false;
    }

    private void exitBuilding() {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            playerInBuilding = false;
            mapManager.changeMap();
            playerManager.movement.setPosition(respawnLocation);
            buildingManager.makeBuildingsAppear();
        }
    }

    private ActivityTile playerInActivityZone(Vector2 position) {
        for (ActivityTile activityZone : mapManager.getActivityTiles()) {
            if (activityZone.getRectangle().contains(position.x, position.y)) {
                System.out.println("in zone");
                return activityZone;
            }
        }
        return null;
    }

    private void askToDoActivity(ActivityTile activityTile) {
        if (playerManager.getState().isINTERACTING()) {
            playerManager.getState().stopInteracting();
            Vector2 playerPosition = playerManager.getPosition();
            Skin skin = new Skin(Gdx.files.internal("assets/skin/default/uiskin.json"));
            String activityName = activityTile.getActivity().getName();
            Dialog dialog = new Dialog("Would you like to..." + activityName, skin) {
                @Override
                protected void result(Object object) {
                    System.out.println("Choice" + object);
                }
            };

            dialog.button("Yes", true);
            dialog.button("No", false);
            dialog.show(stage);

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


    public void update() {
        Building building = checkForBuildingInRange();
        if (checkForBuildingInRange() != null) {
            interactWithBuilding(building, playerManager.getMovement());
        }

        if (playerInBuilding) {
            if (playerInExitZone(playerManager.getPosition())) {
                exitBuilding();
            }
            ActivityTile activityTile = playerInActivityZone(playerManager.getPosition());
            if (activityTile != null) {
                System.out.println("Running");
                askToDoActivity(activityTile);
            }
        }
    }
}
