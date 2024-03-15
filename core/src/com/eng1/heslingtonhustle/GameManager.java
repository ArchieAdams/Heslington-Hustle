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
import com.eng1.heslingtonhustle.activities.Studying;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameManager {
    private final Stage stage;
    private final CameraManager cameraManager;
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final BuildingManager buildingManager;
    private Dialog dialog;
    private final Day day = new Day();
    private Vector2 respawnLocation;

    public GameManager(Stage stage, CameraManager cameraManager, MapManager mapManager, PlayerManager playerManager, BuildingManager buildingManager) {
        this.stage = stage;
        this.cameraManager = cameraManager;
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
            createDialog(building);
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

    private void createDialog(Building building) {
        Skin skin = new Skin(Gdx.files.internal("assets/skin/default/uiskin.json"));
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

    private void enterBuilding(String buildingName) {
        String newMapPath = mapManager.getMapPath(buildingName);
        respawnLocation = new Vector2(playerManager.getPosition());
        System.out.println("current location" + playerManager.getPosition().toString());
        mapManager.changeMap(newMapPath);
        System.out.println("Respawn location: " + respawnLocation);
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
        if (playerManager.getState().isINTERACTING() && playerInExitZone(playerManager.getPosition())) {
            playerManager.getState().stopInteracting();
            mapManager.changeMap();
            playerManager.movement.setPosition(respawnLocation);
            buildingManager.makeBuildingsAppear();
            System.out.println("Respawn location: %s%n" + respawnLocation);
            System.out.println("Current location given" + playerManager.getPosition());
        }
    }


    public void update(float deltaTime) {
        Building building = checkForBuildingInRange();
        if (checkForBuildingInRange() != null) {
            interactWithBuilding(building, playerManager.getMovement());
        }

        if (playerInExitZone(playerManager.getPosition())) {
            exitBuilding();
        }
    }
}
