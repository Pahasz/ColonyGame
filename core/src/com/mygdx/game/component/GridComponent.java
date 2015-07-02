package com.mygdx.game.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.game.ColonyGame;
import com.mygdx.game.component.collider.Collider;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Grid;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Paha on 1/17/2015.
 */
public class GridComponent extends Component{
    @JsonProperty
    private int gridType;
    @JsonIgnore
    private Grid.Node currNode;
    @JsonIgnore
    private Grid.GridInstance grid;
    @JsonProperty
    public int exploreRadius = 3;
    @JsonProperty
    private boolean addMulti = false;

    public GridComponent() {
        super();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        super.start();
        //Gets a node to start.
        this.grid = ColonyGame.worldGrid;
        if(!addMulti) this.currNode = this.grid.addToGrid(this.owner);
        else{
            Rectangle bounds = new Rectangle();
            Collider collider = this.getComponent(Collider.class);
            float minX=0, maxX=0, minY=0, maxY=0;

            if(collider.fixture.getShape().getType() == Shape.Type.Polygon){
                PolygonShape shape = (PolygonShape)collider.fixture.getShape();
                Vector2 vertex = new Vector2();
                //This basically forms the bounding box.
                for(int i=0;i<shape.getVertexCount();i++){
                    shape.getVertex(i, vertex);
                    if(vertex.x < minX || minX == 0) minX = vertex.x;
                    if(vertex.y < minY || minY == 0) minY = vertex.y;
                    if(vertex.x > maxX || minX == 0) maxX = vertex.x;
                    if(vertex.y > maxY || minY == 0) maxY = vertex.y;
                }
            }else if(collider.fixture.getShape().getType() == Shape.Type.Circle){
                CircleShape shape = (CircleShape)collider.fixture.getShape();
                //Just use the radius if a circle
                minX = minY = -shape.getRadius()/2;
                maxX = maxY = shape.getRadius()/2;
            }

            //Since the vertex's positions are relative to the unit (origin being 0,0), add the Entity's position to it.
            Vector2 pos = this.owner.getTransform().getPosition();
            minX+=pos.x;
            minY+=pos.y;
            maxX+=pos.x;
            maxY+=pos.y;

            float width = (maxX - minX);
            float height = (maxY - minY);

            bounds.setX(minX);
            bounds.setY(minY);
            bounds.setWidth(width);
            bounds.setHeight(height);
            this.currNode = this.grid.addToGrid(this.owner, true, bounds);
        }
        this.grid.addViewer(this.currNode, this.exploreRadius);
        this.load();

    }

    @Override
    public void save() {

    }

    @Override
    public void load() {
        this.grid = ColonyGame.worldGrid;
        if(this.gridType == Constants.GRIDSTATIC)
            this.setActive(false);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        this.currNode = this.grid.checkNode(this.currNode, this.owner, true, exploreRadius);
    }

    @JsonIgnore
    public GridComponent setGridType(int gridType) {
        this.gridType = gridType;
        return this;
    }

    @JsonIgnore
    public GridComponent setGrid(Grid.GridInstance grid) {
        this.grid = grid;
        return this;
    }

    @JsonIgnore
    public GridComponent setExploreRadius(int exploreRadius) {
        this.exploreRadius = exploreRadius;
        return this;
    }

    @JsonIgnore
    public GridComponent setAddMulti(boolean multi){
        this.addMulti = multi;
        return this;
    }

    @JsonIgnore
    public Grid.Node getCurrNode(){
        return this.currNode;
    }

    @Override
    public void destroy(Entity destroyer) {
        this.currNode.removeEntity(this.owner);

        super.destroy(destroyer);
    }
}
