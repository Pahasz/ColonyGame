package com.mygdx.game.component.collider;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mygdx.game.component.Component;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.interfaces.IScalable;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Tags;

/**
 * Created by Paha on 1/9/2015.
 */
public class Collider extends Component implements IScalable{
    @JsonIgnore
    protected Body body;
    @JsonIgnore
    protected Fixture fixture;
    @JsonIgnore
    protected World world;
    @JsonProperty
    protected float originalRadius;

    public Collider(){

    }

    @Override
    @JsonIgnore
    public void save() {

    }

    @Override
    @JsonIgnore
    public void initLoad() {
        super.initLoad();

    }

    @Override
    @JsonIgnore
    public void load() {
        //TODO Under Construction! Apparently changing active state locks everything up.

        if(this.body == null) return;

        ColliderInfo bodyInfo = new ColliderInfo(owner);
        ColliderInfo fixtureInfo = new ColliderInfo(owner);
        fixtureInfo.tags.addTag(Constants.COLLIDER_CLICKABLE);
        fixtureInfo.tags.addTag("entity");
        this.body.setUserData(bodyInfo);
        this.fixture.setUserData(fixtureInfo);

        this.body.setActive(true);
        //this.owner.getComponents().registerScalable(this);
        //this.body.setActive(false);
    }

    @Override
    @JsonIgnore
    public void init() {
        super.init();
    }

    @Override
    @JsonIgnore
    public void start() {
        super.start();
        load();
    }

    @Override
    @JsonIgnore
    public void update(float delta) {
        super.update(delta);
        this.owner.getTransform().setPosition(this.body.getPosition().x, this.body.getPosition().y);
    }

    @Override
    @JsonIgnore
    public void destroy(Entity destroyer) {
        super.destroy(destroyer);

        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);
    }

    @JsonIgnore
    public void setWorld(World world){
        this.world = world;
    }

    @JsonIgnore
    public void setFixture(FixtureDef def){
        if(this.fixture != null) this.body.destroyFixture(this.fixture);
        this.fixture = this.body.createFixture(def);
        ColliderInfo fixtureInfo = new ColliderInfo(owner);
        fixtureInfo.tags.addTag(Constants.COLLIDER_CLICKABLE);
        fixtureInfo.tags.addTag("entity");
        this.fixture.setUserData(fixtureInfo);
    }

    @JsonIgnore
    public void setBodyPosition(Vector2 position, float angle){
        this.setBodyPosition(position.x, position.y, angle);
    }

    @JsonIgnore
    public void setBodyPosition(float x, float y, float angle){
        this.body.setTransform(x, y, angle);
    }

    @JsonIgnore
    public Body getBody(){
        return this.body;
    }

    @JsonIgnore
    public void setBody(BodyDef bodyDef){
        if(this.body != null) world.destroyBody(this.body);

        this.body = world.createBody(bodyDef);
        ColliderInfo bodyInfo = new ColliderInfo(owner);
        this.body.setUserData(bodyInfo);
    }

    @JsonIgnore
    public Fixture getMainFixture(){
        return this.fixture;
    }

    @Override
    @JsonIgnore
    public void scale(float scale) {
        this.fixture.getShape().setRadius(this.originalRadius*scale);
    }

    public static class ColliderInfo{
        public String name;
        public Tags tags = new Tags("collider");
        public Entity owner;

        public ColliderInfo(Entity owner){
            this.owner = owner;
        }
    }
}
