package com.mygdx.game.util;

import java.util.HashMap;

/**
 * Created by Paha on 4/11/2015.
 */
public class StateSystem<T>{
    private HashMap<String, State<T>> stateMap = new HashMap<>();
    private State<T> currState = new State<>("default", true, null);
    private State<T> defaultState = currState;

    public StateSystem(){

    }

    /**
     * Adds a new state to this system.
     * @param stateName The compName for the state.
     */
    public State addState(String stateName){
        return this.addState(stateName, false, null);
    }

    /**
     * Adds a new state to this system with user data (whatever object you want!)
     * @param stateName The compName for the state.
     * @param userData The data for the state to hold.
     */
    public State addState(String stateName, T userData){
        return this.addState(stateName, false, userData);
    }

    /**
     * Adds a new state to this system with the option to make it the default state.
     * @param stateName The compName of the state.
     * @param defaultState True if the state should be the default state of this system, false otherwise.
     */
    public State addState(String stateName, boolean defaultState){
        return this.addState(stateName, defaultState, null);
    }

    /**
     * Adds a new state to this system with the option to make it the default state and to add user data.
     * @param stateName The compName of the state.
     * @param defaultState True if it the state should be the default state, false otherwise.
     * @param userData The user data for this state.
     */
    public State<T> addState(String stateName, boolean defaultState, T userData){
        State<T> newState = new State<>(stateName, false, userData);
        this.stateMap.putIfAbsent(stateName, newState);
        if(defaultState) this.defaultState = newState;
        return newState;
    }

    /**
     * @return The current State of this system.
     */
    public State<T> getCurrState(){
        return this.currState;
    }

    /**
     * @return The default State of this system.
     */
    public State<T> getDefaultState(){
        return this.defaultState;
    }

    /**
     * @param stateName The compName of the state to check.
     * @return True if the State compName passed in is the compName of the current State of this system, false otherwise.
     */
    public boolean isCurrState(String stateName){
        return this.currState.stateName.equals(stateName);
    }

    /**
     * Sets the current State of this system. If the State compName does not exist, the default State becomes the current State.
     * @param stateName The name of the state to become the current state.
     * @return The state that the current state was assigned;
     */
    public State<T> setCurrState(String stateName){
        this.currState = this.stateMap.getOrDefault(stateName, defaultState);
        return this.currState;
    }

    /**
     * Gets a state by name from this StateSystem.
     * @param stateName The name of the state.
     * @return Either the State that was found by 'stateName', or the default state of this system if it did not exist.
     */
    public State<T> getState(String stateName){
        return this.stateMap.getOrDefault(stateName, defaultState);
    }

    /**
     * Sets the current State to the default State.
     */
    public void setToDefaultState(){
        this.currState = defaultState;
    }

    /**
     * @param stateName The compName of the State to check for.
     * @return True if this State system contains the state, false otherwise.
     */
    public boolean stateExists(String stateName){
        return this.stateMap.containsKey(stateName);
    }

    public static class State<T>{
        public String stateName;
        private boolean repeat, repeatLastState, canBeSavedAsLast;
        private T userData;

        public State(String stateName, boolean repeat, T userData){
            this.stateName = stateName;
            this.userData = userData;
            this.repeat = repeat;
        }

        public T getUserData(){
            return this.userData;
        }

        /**
         * Set if this state should repeat on ending.
         * @param val True or false.
         * @return The State for easy chainging.
         */
        public State<T> setRepeat(boolean val){
            this.repeat = val;
            return this;
        }

        /**
         * Set if the last state saved should be repeated.
         * @param val True or false.
         * @return The State for easy chaining.
         */
        public State<T> setRepeatLastState(boolean val){
            this.repeatLastState = val;
            return this;
        }

        /**
         * Set if this State should can be saved to repeat after the current State.
         * @param val True or false.
         * @return The State for easy chaining.
         */
        public State<T> setCanBeSavedAsLast(boolean val){
            this.canBeSavedAsLast = val;
            return this;
        }

        public boolean getRepeat(){
            return this.repeat;
        }

        public boolean getRepeatLastState(){
            return this.repeatLastState;
        }

        public boolean getCanBeSavedAsLast(){
            return this.canBeSavedAsLast;
        }
    }

    public static class DefineTask{
        public String taskOnSuccess, taskOnFailure, lastState;

        public DefineTask(String taskOnSuccess, String taskOnFailure) {
            this.taskOnSuccess = taskOnSuccess;
            this.taskOnFailure = taskOnFailure;
        }
    }
}
