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
import com.eng1.heslingtonhustle.activities.Study;

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

    public GameManager(Stage stage, MapManager mapManager, PlayerManager playerManager, BuildingManager buildingManager) {
        this.stage = stage;
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.buildingManager = buildingManager;
        playerManager.setCurrentDay(day);
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
        Skin skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
        String buildingToEnter = building.getName();
        Dialog dialog = new Dialog("Are you sure you want to go to " + buildingToEnter + "?", skin) {
            public void result(Object obj) {
                System.out.println("result " + obj);
                if ((boolean) obj) {
                    Activity buildingActivity = building.getActivity();
                    boolean perform = buildingActivity.perform(playerManager);
                    if(perform){
                        day = playerManager.getDay();
                        day.addActivity(buildingActivity);
                    }
                    System.out.println(day.getTotalDuration());
                    System.out.println(day.getTotalEnergyUsage());
                    System.out.println(day.getStudySessions());
                    //enterBuilding(buildingToEnter);
                }
            }
        };

        dialog.text("It will take "+building.getActivity().getDurationHours()+" hours and use "+building.getActivity().getEnergyUsagePercent()+"% of your energy");
        dialog.button("Yes", true);
        dialog.button("No", false);
        return dialog;
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


    public void update() {
        Building building = checkForBuildingInRange();
        if (checkForBuildingInRange() != null) {
            interactWithBuilding(building, playerManager.getMovement());
        }

        if (playerInExitZone(playerManager.getPosition())) {
            exitBuilding();
        }
    }
}
