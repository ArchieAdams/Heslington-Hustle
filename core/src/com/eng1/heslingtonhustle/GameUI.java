package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.eng1.heslingtonhustle.activities.Activity;

import java.util.List;


public class GameUI {
    private final Stage uiStage;
    private final Texture xpBackground;
    private final Texture xpFill;
    private final ProgressBar progressBar;
    private Label timeLabel;
    private Label dayLabel;
    private final PlayerManager playerManager;
    private final Time time;
    private Table scoreTable;
    private Skin skin;


    public GameUI(Stage uiStage, PlayerManager playerManager) {
        this.uiStage = uiStage;
        this.playerManager = playerManager;
        //this.energy = playerManager.getEnergy();
        this.time = playerManager.getTime();
        xpBackground = new Texture(Gdx.files.internal("skin/craftacular/raw/xp-bg.png"));
        xpFill = new Texture(Gdx.files.internal("skin/craftacular/raw/xp.png"));
        progressBar = new ProgressBar(0, 100, 0.01f, false, new ProgressBar.ProgressBarStyle());
        skin = new Skin(Gdx.files.internal("skin/craftacular/skin/craftacular-ui.json"));
        initUI();
    }

    private void initUI() {

        Label energyLabel = new Label("Energy: ", skin);
        dayLabel = new Label("Day: ", skin);
        timeLabel = new Label("Time: ", skin);


        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(xpBackground);
        backgroundDrawable.setMinWidth(400);
        backgroundDrawable.setMinHeight(50);

        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(xpFill);
        fillDrawable.setMinWidth(400);
        fillDrawable.setMinHeight(50);

        progressBar.getStyle().background = backgroundDrawable;
        progressBar.getStyle().knobBefore = fillDrawable;

        updateProgressBar();


        Table table = new Table();
        table.setFillParent(true);
        table.top().right();

        float padTop = 40f;
        float padRight = 30f;

        table.add(energyLabel).padTop(padTop).padRight(5);
        table.add(progressBar).width(400).height(50).padTop(padTop).padRight(padRight);
        table.row();
        table.add(dayLabel).padTop(padTop).padRight(5);
        table.row();
        table.add(timeLabel).padTop(padTop).padRight(5);


        uiStage.addActor(table);
    }

    public void updateProgressBar() {
        progressBar.setValue(playerManager.getEnergy().getEnergyLevel());
        timeLabel.setText("Time: "+time.toString());
        dayLabel.setText("Day: "+time.getDay());
    }

    public void showScore(List<Day> week){
        uiStage.clear();
        scoreTable = new Table();
        scoreTable.setFillParent(true);
        uiStage.addActor(scoreTable);

        int score = calculateScore(week);
        int highScore = ScoreManager.loadHighScore();

        if (score > highScore) {
            highScore = score;
            ScoreManager.saveHighScore(highScore);
        }

        scoreTable.add(new Label("Day", skin)).expandX().center().bottom();
        scoreTable.add(new Label("Study", skin)).expandX().center().bottom();
        scoreTable.add(new Label("Eaten", skin)).expandX().center().bottom();
        scoreTable.add(new Label("Relaxed", skin)).expandX().center().bottom();
        int index = 0;
        for (Day day : week) {
            addDayStatsLabel(time.getDay(index), day.getStudySessions(), day.getEaten(), day.getRelaxed());
            index++;
        }

        Label scoreLabel = new Label("Final Score: " + score, skin);
        scoreLabel.setAlignment(Align.center);

        scoreTable.row().expandY().bottom();
        scoreTable.add(scoreLabel).expandX().center().colspan(4).padTop(20).bottom();
        Label highScoreLabel = new Label("High Score: " + highScore, skin);
        highScoreLabel.setAlignment(Align.center);
        scoreTable.add(highScoreLabel).expandX().center().colspan(4).padTop(10).bottom();


        scoreTable.row().pad(10).bottom();

    }



    private void addDayStatsLabel(String dayLabel, int studySessions, int eaten, int relaxed) {
        scoreTable.row().pad(10);
        scoreTable.add(new Label(dayLabel, skin)).expandX().center().bottom();
        scoreTable.add(new Label(String.valueOf(studySessions), skin)).expandX().center().bottom();
        scoreTable.add(new Label(String.valueOf(eaten), skin)).expandX().center().bottom();
        scoreTable.add(new Label(String.valueOf(relaxed), skin)).expandX().center().bottom();
    }

    private int calculateScore(List<Day> week) {
        int studyCount = 0;
        int dayStudiedOnce = 0;
        int dayRelaxedOnce = 0;
        int dayEatenCount = 0;
        int maxScore = 100;
        int score;
        for (Day day : week) {
            studyCount += day.getStudySessions();
            if (day.getStudySessions() >= 1) {
                dayStudiedOnce++;
            }
            if (day.getEaten() >= 2) {
                dayEatenCount++;
            }
            if (day.getRelaxed() > 0) {
                dayRelaxedOnce++;
            }
        }

        score = studyCount * 10;
        score = Math.min(score, maxScore);

        // Apply penalties
        if (dayStudiedOnce != 7 && (dayStudiedOnce != 6 || studyCount < 7)) {
            score = dayStudiedOnce*10;
            score = Math.min(score, 50);
        }

        if (dayEatenCount < 7) {
            score -= 10; // Penalty for not eating enough
        }

        if (dayRelaxedOnce < 7) {
            score -= 10; // Penalty for not relaxing enough
        }

        // Cap the score at maxScore

        score = Math.max(score, 0);
        return score;
    }



    public void dispose() {
        uiStage.dispose();
        xpBackground.dispose();
        xpFill.dispose();
    }

}