package com.mygdx.game.behaviourtree.action;

import com.mygdx.game.behaviourtree.BlackBoard;
import com.mygdx.game.behaviourtree.LeafTask;
import com.mygdx.game.component.Building;
import com.mygdx.game.component.Colonist;

/**
 * Created by Paha on 8/9/2015.
 */
public class GetBuildingFromColony extends LeafTask{
    public GetBuildingFromColony(String name, BlackBoard blackBoard) {
        super(name, blackBoard);
    }

    @Override
    public boolean check() {
        return super.check();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Get the colonist and use to get a building from its colony.
        Colonist col = this.blackBoard.myManager.getEntityOwner().getComponent(Colonist.class);
        Building building = col.getColony().getOwnedFromColony(Building.class, b -> b.getEntityOwner().getTags().hasTags(this.blackBoard.tagsToSearch));

        //If the building is not null, set it as the target and finish with success.
        if(building != null){
            this.blackBoard.target = building.getEntityOwner();
            this.control.finishWithSuccess();
        //Otherwise, finish with failure.
        }else
            this.control.finishWithFailure();
    }

    @Override
    public void end() {
        super.end();
    }
}
