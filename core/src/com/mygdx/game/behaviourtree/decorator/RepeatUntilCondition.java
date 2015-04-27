package com.mygdx.game.behaviourtree.decorator;

import com.mygdx.game.behaviourtree.Task;
import com.mygdx.game.component.BlackBoard;

/**
 * Created by Paha on 4/6/2015.
 */
public class RepeatUntilCondition extends TaskDecorator{
    public RepeatUntilCondition(String name, BlackBoard bb, Task task) {
        super(name, bb, task);
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
        if(control.callbacks.successCriteria != null && control.callbacks.successCriteria.test(this.task)){
            this.control.finishWithSuccess();
        }else {
            //If the job finished and this repeat job has not met it's criteria, restart the job.
            if(this.task.getControl().hasFinished()) {
                this.task.getControl().reset();
                this.task.getControl().safeStart();
            }
            this.task.update(delta);
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public String getName() {
        return super.getName()+" "+this.task.getName();
    }
}