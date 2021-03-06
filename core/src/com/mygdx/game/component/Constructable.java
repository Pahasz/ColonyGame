package com.mygdx.game.component;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.util.DataBuilder;
import com.mygdx.game.util.ItemNeeded;
import com.mygdx.game.util.managers.DataManager;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.HashMap;

/**
 * Created by Paha on 6/20/2015.
 * A Constructable class that can be attached to Entities that must be constructed.
 */
public class Constructable extends Component{
    private Inventory inventory;
    private HashMap<String, ConstructableItemAmounts> itemsNeededMap = new HashMap<>();
    private Array<ItemNeeded> itemsNeededList = new Array<>();
    private int totalItemsNeeded, totalItemsSupplied;
    private boolean complete=false;

    public Constructable(){
        this.setActive(false);
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void initLoad(TLongObjectHashMap<Entity> entityMap, TLongObjectHashMap<Component> compMap) {
        super.initLoad(entityMap, compMap);
    }

    @Override
    public void load(TLongObjectHashMap<Entity> entityMap, TLongObjectHashMap<Component> compMap) {
        super.load(entityMap, compMap);

        this.inventory = this.getComponent(Inventory.class);
        this.calculateItemsNeeded();
    }

    @Override
    public void init() {
        super.init();


    }

    @Override
    public void start() {
        super.start();
        //For the case that we instant build (testing or what not), don't do this if complete.
        if(!this.isComplete()) {
            //Get the recipe for this building and set the items required.
            DataBuilder.JsonRecipe recipe = DataManager.getData(this.getComponent(Building.class).getBuildingName(), DataBuilder.JsonRecipe.class);
            for (int i = 0; i < recipe.items.length; i++) {
                int amount = recipe.itemAmounts[i];
                this.addItemRequirement(recipe.items[i], amount);
            }
        }
        this.load(null, null);
    }

    /**
     * @return True if complete, false otherwise.
     */
    public boolean isComplete(){
        return this.complete;
    }

    /**
     * Adds an item and amount to this constructable.
     * @param itemName The name of the item.
     * @param itemAmount The amount of the item.
     * @return The Constructable for chaining.
     */
    public Constructable addItemRequirement(String itemName, int itemAmount){
        this.itemsNeededMap.put(itemName, new ConstructableItemAmounts(itemName, itemAmount));
        this.totalItemsNeeded+=itemAmount;
        return this;
    }

    /**
     * Recalculates the items needed for this constructable. Stores it in the itemsNeededList (reused).
     */
    private void calculateItemsNeeded(){
        this.itemsNeededList = new Array<>();
        for(String item : itemsNeededMap.keySet()){
            int amountNeeded = itemAmountNeeded(item);
            if(amountNeeded > 0) //Only add if we actually need some.
                this.itemsNeededList.add(new ItemNeeded(item, amountNeeded));
        }
    }

    /**
     * Gets the amount needed of an item by the name of 'itemName' to fulfill the need for that item. This includes the item in the inventory if it exists.
     * @param itemName The name of the item.
     * @return A positive number if the constructable requires more of the item. 0 or negative means none needed or an overflow respectively.
     */
    public int itemAmountNeeded(String itemName){
        ConstructableItemAmounts amounts = itemsNeededMap.get(itemName);
        return amounts.amountNeeded - amounts.amountFulfilled - this.inventory.getItemAmount(itemName, true);
    }

    /**
     * Builds the constructable using any items in the inventory that are usable for this construction.
     * @return -1 for not having enough items to finish the construction, 0 for not being finished but we can still continue to build,
     * and 1 for completing the construction.
     */
    public int build(){
        boolean hasAnItem = false;
        for(ConstructableItemAmounts item : itemsNeededMap.values()){
            int amount = this.inventory.getItemAmount(item.itemName);
            if(amount > 0){
                hasAnItem = true;
                int useAmount = 1; //How much to consume.
                this.inventory.removeItem(item.itemName, useAmount); //Remove the amount from the inventory
                this.totalItemsSupplied+=useAmount; //Add the amount to the total items supplied
                item.amountFulfilled+=useAmount;    //Add the amount to the item's amount fulfilled
                if(item.amountFulfilled >= item.amountNeeded)  //If we have fulfilled it all, remove it from the needed materials.
                    this.itemsNeededMap.remove(item.itemName);
                this.calculateItemsNeeded();
                break;
            }
        }

        //If we didn't have any items to build, return -1 for no items.
        if(!hasAnItem)
            return -1;

        //If we have no more items needed, return 1 for complete.
        if(itemsNeededMap.size() == 0) {
            this.setComplete();
            return 1;
        }

        //Otherwise, we aren't finished and there are no errors.
        return 0;
    }

    /**
     * Sets this Constructable to complete.
     */
    public void setComplete(){
        this.owner.getGraphicIdentity().getSprite().setAlpha(1f);
        this.complete = true;
        this.getEntityOwner().removeComponent(this);
        this.getEntityOwner().getTags().removeTag("constructing");
    }

    /**
     * Gets the remaining items needed and the amounts to finish this construction.
     * @return A list of ItemNeeded that holds an item name and an amount needed. This list is reused.
     */
    public final Array<ItemNeeded> getItemsNeeded(){
        return this.itemsNeededList;
    }

    /**
     * @return A Copy of the ItemNeeed array.
     */
    public final Array<ItemNeeded> getItemsNeededCopy(){
        Array<ItemNeeded> list = new Array<>();
        for(ItemNeeded needed : this.itemsNeededList)
            list.add(new ItemNeeded(needed.itemName, needed.amountNeeded));
        return list;
    }

    /**
     * @return A value between 0 and 1.
     */
    public float getPercentageDone(){
        return (float)this.totalItemsSupplied/(float)this.totalItemsNeeded;
    }

    /**
     * @return The Inventory of this Constructable.
     */
    public Inventory getInventory(){
        return this.inventory;
    }

    private class ConstructableItemAmounts{
        public String itemName; //The name
        public int amountNeeded=0; //The amount that is needed
        public int amountFulfilled=0; //The amount that has been used in the construction.

        public ConstructableItemAmounts(String itemName, int amountNeeded){
            this.itemName = itemName;
            this.amountNeeded = amountNeeded;
        }

        @Override
        public String toString() {
            return "item: "+itemName+", needed: "+amountNeeded+", fulfilled: "+amountFulfilled;
        }
    }

}
