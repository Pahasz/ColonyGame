package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.component.GridComponent;
import com.mygdx.game.component.Interactable;
import com.mygdx.game.component.Resource;
import com.mygdx.game.component.collider.Collider;
import com.mygdx.game.helpers.Constants;
import com.mygdx.game.helpers.managers.ResourceManager;

/**
 * Created by Paha on 1/29/2015.
 */
public class TreeEnt extends Entity{
    public TreeEnt(Vector2 position, float rotation, Texture graphic, SpriteBatch batch, int drawLevel) {
        super(position, rotation, graphic, batch, drawLevel);

        this.addTag(Constants.ENTITY_RESOURCE);

        float rand = MathUtils.random();
        if(rand < 0.2) this.addComponent(ResourceManager.getResourceByname("redwood"));
        else this.addComponent(ResourceManager.getResourceByname("pine"));

        this.addComponent(new Interactable("resource"));
        this.addComponent(new GridComponent(Constants.GRIDSTATIC, ColonyGame.worldGrid, -1));

        this.makeCollider();
    }

    private void makeCollider(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(10f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0;

        //Add the Collider Component and an Interactable Component.
        this.addComponent(new Collider(ColonyGame.world, bodyDef, fixtureDef));

        circle.dispose();
    }
}
