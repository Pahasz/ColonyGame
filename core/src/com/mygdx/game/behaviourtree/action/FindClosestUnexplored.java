package com.mygdx.game.behaviourtree.action;

import com.mygdx.game.behaviourtree.LeafTask;
import com.mygdx.game.component.BlackBoard;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.helpers.Constants;
import com.mygdx.game.helpers.Grid;
import com.mygdx.game.helpers.worldgeneration.WorldGen;
import com.mygdx.game.interfaces.Functional;

/**
 * Created by Paha on 2/17/2015.
 */
public class FindClosestUnexplored extends LeafTask{
    Entity target;

    public FindClosestUnexplored(String name, BlackBoard blackBoard) {
        super(name, blackBoard);

    }

    public FindClosestUnexplored(String name, BlackBoard blackBoard, Entity target) {
        this(name, blackBoard);

        this.target = target;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void start() {
        super.start();

        if(this.target == null)
            this.target = this.blackBoard.getEntityOwner();

        Functional.Perform<Grid.Node[][]> findClosestUnexplored = grid -> {
            int radius = 0;
            float closestDst = 999999999999999f;
            boolean finished = false;
            Grid.Node closestNode = null;
            Grid.Node targetNode = this.blackBoard.colonyGrid.getNode(this.target);
            Grid.Node myNode = this.blackBoard.colonyGrid.getNode(this.blackBoard.getEntityOwner());

            while(closestNode == null) {
                int startX = targetNode.getCol() - radius < 0 ? 0 : targetNode.getCol() - radius;
                int endX = targetNode.getCol() + radius >= grid.length ? grid.length - 1 : targetNode.getCol() + radius;
                int startY = targetNode.getRow() - radius < 0 ? 0 : targetNode.getRow() - radius;
                int endY = targetNode.getRow() + radius >= grid[targetNode.getCol()].length ? grid[targetNode.getCol()].length - 1 : targetNode.getRow() + radius;
                finished = true; //Reset the flag.

                for(int x = startX; x <= endX; x++){
                    for(int y = startY; y <= endY; y++){
                        Grid.Node tmpNode = this.blackBoard.colonyGrid.getNode(x, y);
                        int visibility = WorldGen.getVisibilityMap()[x][y].getVisibility();
                        int terrainType = WorldGen.getNode(x,y).type;
                        if(tmpNode == null || visibility != Constants.VISIBILITY_UNEXPLORED || terrainType == Constants.TERRAIN_WATER) {
                            continue;
                        }

                        finished = false;

                        //Get the distance from the current node to the tmpNode on the graph. If the closestNode is null or the dst is less than the closestDst, assign a new node!
                        float dst = Math.abs(myNode.getCol() - tmpNode.getCol()) + Math.abs(myNode.getRow() - tmpNode.getRow());
                        if(closestNode == null || dst < closestDst){
                            closestNode = tmpNode;
                            closestDst = dst;
                        }
                    }
                }

                radius++;
            }

            this.blackBoard.targetNode = closestNode;
        };

        this.blackBoard.colonyGrid.perform(findClosestUnexplored);

        this.control.finishWithSuccess();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void end() {
        super.end();
    }
}
