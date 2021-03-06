package com.mygdx.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.interfaces.IDestroyable;
import com.mygdx.game.util.gui.GUI;

/**
 * Created by Paha on 2/10/2015.
 */
public class FloatingText implements IDestroyable{
    private static GUI.GUIStyle style;

    static{
        style = new GUI.GUIStyle();
    }

    private Vector2 startPos, currPos, endPos;
    private String text;
    private double diff, counter;
    private float percent, fadingDiff, fadePercent;
    private TextureRegion image;
    private boolean destroyed = false;
    private Color color = new Color(1, 1, 1, 1);

    public FloatingText(TextureRegion image, String text, Vector2 start, Vector2 end, float time, float fadePercent){
        this(text, start, end, time, fadePercent);
        this.image = image;
    }

    /**
     * Creates a floating text object.
     * @param start The starting Vector2 of this text.
     * @param end The ending Vector2 of this text.
     * @param time The amount of time (in seconds, ie: 0.5f) for this text to be alive
     * @param fadePercent The percent at which the text should begin fading.
     */
    public FloatingText(String text, Vector2 start, Vector2 end, float time, float fadePercent){
        this.startPos = new Vector2(start);
        this.currPos = new Vector2(start);
        this.endPos = new Vector2(end);
        this.text = text;

        double startTime = System.currentTimeMillis()*1000;
        double endTime = startTime + time;
        diff = endTime - startTime;

        this.fadePercent = fadePercent;
        this.fadingDiff = 1 - fadePercent;

        ColonyGame.instance.listHolder.addFloatingText(this);
    }

    public void update(float delta){

        Vector2 tmpPos = new Vector2(this.startPos);

        counter+=delta;
        this.percent = (float)(counter/diff);
        this.currPos = tmpPos.lerp(this.endPos, this.percent);
        //System.out.println("diff: "+diff+" counter: "+counter+" counter/diff: "+(counter/diff));

        if((float)(this.counter/this.diff) >= 1){
            this.destroy();
        }
    }

    @Override
    public void destroy() {

        this.destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void render(float delta, SpriteBatch batch){
        if(this.percent >= this.fadePercent){
            float percent = 1 - ((this.percent - this.fadePercent)/this.fadingDiff);
            color.set(1, 1, 1, percent);
        }else{
            color.set(1, 1, 1, 1);
        }

        style.font.setColor(color);
        batch.setProjectionMatrix(ColonyGame.instance.UICamera.combined);
        Vector3 pos = ColonyGame.instance.camera.project(new Vector3(currPos.x, currPos.y, 0));
        GUI.Text(this.text, batch, pos.x, pos.y, true, style);
    }
}
