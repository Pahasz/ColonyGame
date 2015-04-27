package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.helpers.GH;
import com.mygdx.game.helpers.ListHolder;
import com.mygdx.game.ui.MainMenuInterface;
import com.mygdx.game.ui.UI;

/**
 * Created by Paha on 1/10/2015.
 */
public class MainMenuScreen implements Screen {
    private ColonyGame game;

    public MainMenuScreen(ColonyGame game) {
        super();

        this.game = game;
    }

    @Override
    public void show() {
        new MainMenuInterface(ColonyGame.batch, this.game);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setDisplayMode(width, height, false);

        ColonyGame.camera.setToOrtho(false, GH.toMeters(width), GH.toMeters(height));
        ColonyGame.UICamera.setToOrtho(false, width, height);

        //Resizes all the GUI elements of the game (hopefully!)
        Array<UI> list = ListHolder.getGUIList();
        for(int i=0;i< list.size;i++){
            list.get(i).resize(width, height);
        }
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
