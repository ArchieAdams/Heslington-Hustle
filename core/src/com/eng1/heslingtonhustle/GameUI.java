package com.eng1.heslingtonhustle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;


public class GameUI {
    private final Stage uiStage;
    private final Texture xpBackground;
    private final Texture xpFill;
    private final ProgressBar progressBar;

    private final Energy energy;


    public GameUI(Stage uiStage) {
        this.uiStage = uiStage;
        this.energy = new Energy();
        xpBackground = new Texture(Gdx.files.internal("assets/skin/craftacular/raw/xp-bg.png"));
        xpFill = new Texture(Gdx.files.internal("assets/skin/craftacular/raw/xp.png"));
        progressBar = new ProgressBar(0, 100, 0.01f, false, new ProgressBar.ProgressBarStyle());

        initUI();
    }

    private void initUI() {
        Skin skin = new Skin(Gdx.files.internal("assets/skin/craftacular/skin/craftacular-ui.json"));

        Label energyLabel = new Label("Energy: ", skin);

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

        uiStage.addActor(table);
    }

    public void updateProgressBar() {
        progressBar.setValue(energy.getLevel());
    }

    public void dispose() {
        uiStage.dispose();
        xpBackground.dispose();
        xpFill.dispose();
    }
}