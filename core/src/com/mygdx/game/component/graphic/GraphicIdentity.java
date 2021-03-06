package com.mygdx.game.component.graphic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.component.Component;
import com.mygdx.game.component.Effects;
import com.mygdx.game.component.Enterable;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.GH;
import com.mygdx.game.util.Grid;
import gnu.trove.map.hash.TLongObjectHashMap;

public class GraphicIdentity extends Component {
    public String spriteTextureName = "";
    public String atlasName = "";
    private Sprite sprite;
    private Effects effects;
    private Enterable enterable;
    @JsonProperty
    private Vector2 anchor;
    @JsonProperty
    private int currVisibility=0;

    public GraphicIdentity(){
        this.setActive(true);
        this.anchor = new Vector2(0.5f, 0.5f);
    }

    @Override
    public void save() {

    }

    @Override
    public void initLoad(TLongObjectHashMap<Entity> entityMap, TLongObjectHashMap<Component> compMap) {
        super.initLoad(entityMap, compMap);

        if(this.sprite != null) {
            Vector2 pos = this.owner.getTransform().getPosition();
            this.sprite.setPosition(pos.x - (getSprite().getWidth() * this.anchor.x), pos.y - (getSprite().getHeight() * this.anchor.y));
        }

        if(this.getSprite() == null) setSprite(this.spriteTextureName, this.atlasName);
    }

    @Override
    public void load(TLongObjectHashMap<Entity> entityMap, TLongObjectHashMap<Component> compMap) {
        super.load(entityMap, compMap);
        this.effects = this.getComponent(Effects.class);
        this.enterable = this.getComponent(Enterable.class);
    }

    @Override
    public void init() {
        super.init();
        this.initLoad(null, null);
    }

	@Override
	public void start() {
        super.start();
        if(getSprite() == null) return;
        this.load(null, null);
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        super.render(delta, batch);

        if(getSprite() != null) {
            Grid.GridInstance grid = ColonyGame.instance.worldGrid;
            Vector2 pos = this.owner.getTransform().getPosition(); //Cache the owner's position.

            if (!isWithinBounds()) return;

            Grid.Node node = grid.getNode(this.owner);
            if(node != null) {
                int visibility = grid.getVisibilityMap()[node.getX()][node.getY()].getVisibility();
                if (visibility == Constants.VISIBILITY_UNEXPLORED)
                    return;

                this.changeVisibility(visibility);
            }

            this.getSprite().setRotation(this.owner.getTransform().getRotation());
            this.getSprite().setScale(this.owner.getTransform().getScale());

            this.sprite.setPosition(pos.x - (getSprite().getWidth()*this.anchor.x), pos.y - (getSprite().getHeight()*this.anchor.y));
            this.getSprite().draw(batch);

            if(effects != null){
                float size = GH.toMeters(24);
                int num = effects.getActiveEffects().size;
                float startX = pos.x - num*(size/2);
                float startY = pos.y + sprite.getHeight()/2;
                for(Effects.Effect effect : effects.getActiveEffects()){
                    batch.draw(effect.getIcon(), startX, startY, size, size);
                    startX+=size;
                }
            }else if(this.enterable != null){
                float size = GH.toMeters(24);
                int num = this.enterable.getOccupants().size;
                float startX = pos.x - num*(size/2);
                float startY = pos.y + sprite.getHeight()/2;
                for(Entity entity : this.enterable.getOccupants()){
                    batch.draw(ColonyGame.instance.assetManager.get("colonist", Texture.class), startX, startY, size, size);
                    startX+=size;
                }
            }
        }
    }

    public boolean isWithinBounds(){
        Vector2 pos = this.owner.getTransform().getPosition(); //Cache the owner's position.
        return ColonyGame.instance.camera.frustum.boundsInFrustum(pos.x, pos.y, 0, getSprite().getWidth(), getSprite().getHeight(), 0);
    }

    private void changeVisibility(int visibility){
        if(this.currVisibility == visibility)
            return;

        this.currVisibility = visibility;
        if(this.currVisibility == Constants.VISIBILITY_EXPLORED)
            this.getSprite().setColor(Constants.COLOR_EXPLORED);
        else if(this.currVisibility == Constants.VISIBILITY_VISIBLE)
            this.getSprite().setColor(Constants.COLOR_VISIBILE);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(String textureName, String atlasName){
        this.setSprite(textureName, atlasName, -1, -1);
    }

    /**
     * Sets the sprite via texture name and atlas name. If the atlas name does not apply (empty or null), the asset manager will be searched for the image.
     * @param textureName The name of the texture.
     * @param atlasName The name of the atlas (if applicable) where the texture is.
     */
    @JsonIgnore
    public void setSprite(String textureName, String atlasName, int width, int height){
        if(textureName == null || textureName.equals("")) return;
        this.spriteTextureName = textureName;

        //If the atlas name is applicable, get the texture atlas from the asset manager and then get the texture by name.
        if(atlasName != null && !atlasName.isEmpty()) {
            this.atlasName = atlasName;
            TextureRegion region = ColonyGame.instance.assetManager.get(atlasName, TextureAtlas.class).findRegion(textureName);

            if(region == null)
                GH.writeErrorMessage("TextureRegion is null when creating sprite in GraphicIdentity. Texture: "+textureName+", atlasName: "+atlasName+". Does it exist?");

            if (this.getSprite() == null) this.setSprite(new Sprite(region));
            else this.getSprite().setRegion(region);

        //If no atlas is used, get it normally.
        }else {
            Texture texture = ColonyGame.instance.assetManager.get(textureName, Texture.class);
            if(texture == null) return;
            if (this.getSprite() == null) this.setSprite(new Sprite(texture));
            else this.getSprite().setTexture(texture);
        }

        this.configureSprite(this.getSprite(), width, height);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Call this method when a change to the sprite occurs (change in size, texture, etc...). This will adjust the size/dimensions and such.
     * @param sprite The sprite to configure
     * @param width The width to make the sprite.
     * @param height The height to make the sprite.
     */
    @JsonIgnore
    protected void configureSprite(Sprite sprite, float width, float height){
        if(sprite == null) return;

        if(width == -1 || height == -1) {
            sprite.setSize(GH.toMeters(sprite.getRegionWidth()), GH.toMeters(sprite.getRegionHeight()));
            sprite.setOrigin(sprite.getWidth() * this.anchor.x, sprite.getHeight() * this.anchor.y);
        }else{
            sprite.setSize(GH.toMeters(width), GH.toMeters(height));
            sprite.setOrigin(sprite.getWidth() * this.anchor.x, sprite.getHeight() * this.anchor.y);
        }
    }

    @JsonProperty("sprite")
    public void setSprite(String[] args){
        String name = args[0];
        String atlas = args[1];
        int width = Integer.parseInt(args[2]);
        int height = Integer.parseInt(args[3]);

        this.setSprite(name, atlas, width, height);
    }

    public void configureSprite(){
        this.configureSprite(this.sprite, -1, -1);
    }

    /**
     * Call this method when a change to the sprite occurs (change in size, texture, etc...). This will adjust the size/dimensions and such.
     * @param sprite The Sprite to configure.
     */
    protected void configureSprite(Sprite sprite){
        this.configureSprite(sprite, -1, -1);
    }

    @JsonProperty("sprite")
    private String[] getSpriteData(){
        String[] data;
        if(this.sprite == null) data = new String[]{"", "", "-1", "-1"};
        else data = new String[]{this.spriteTextureName, this.atlasName, ""+(int)GH.toReal(this.sprite.getWidth()), ""+(int)GH.toReal(this.sprite.getHeight())};

        return data;
    }

    /**
     * Sets the anchor of this graphic identity. The values need to be between 0 and 1 (inclusive).
     * @param x The anchor point for the X position.
     * @param y THe anchor point for the Y position.
     */
    @JsonIgnore
    public void setAnchor(float x, float y){
        if(x < 0 || x > 1 || y < 0 || x > 1)
            GH.writeErrorMessage("The anchor X or Y value was below 0 or above 1 (out of bounds). Fix this problem!", true);
        else
            this.anchor.set(x, y);
    }

    @JsonIgnore
    public Vector2 getAnchor(){
        return this.anchor;
    }

    @JsonIgnore
    public String getSpriteTextureName(String name){
        return this.spriteTextureName;
    }
}
