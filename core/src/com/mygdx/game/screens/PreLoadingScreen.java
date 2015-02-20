package com.mygdx.game.screens;

import com.badlogic.gdx.Screen;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.helpers.DataBuilder;

/**
 * Created by Paha on 2/19/2015.
 */
public class PreLoadingScreen implements Screen{
    private DataBuilder builder;
    private ColonyGame game;

    public PreLoadingScreen(ColonyGame game){
        this.game = game;
    }

    @Override
    public void show() {
        builder = new DataBuilder(this.game.assetManager);
    }

    @Override
    public void render(float delta) {
        if(builder.update(delta))
            this.game.setScreen(new MainMenuScreen(this.game));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}