package com.github.brokendesigners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.github.brokendesigners.item.Item;
import java.util.ArrayList;
/*
 * Hand stack of size 3.
 * Acts as a FIFO stack.
 */
public class Hand{

	public ArrayList<Item> heldItems;
	/*
	 * Instantiates Hand
	 */
	public Hand(){ // Instantiates Hand
		this.heldItems = new ArrayList<Item>(3);
	}


	/*
	 * Gives item to hand. Use an ItemRegister reference for the item to be recognisable to stations.
	 */
	public void give(Item item){

		if (this.heldItems.size() == 3){
			return;
		}
		this.heldItems.add(item);
	}
	/*
	 * drops a specific item - not used
	 */
	public Item drop(Item item){
		this.heldItems.remove(item);
		return(item);
	}
	/*
	 * Drops the top item on the stack.
	 */
	public Item drop() {

		if (!this.heldItems.isEmpty()) {
			int amountOfItems = this.heldItems.size();

			Item droppedItem = this.heldItems.get(
				amountOfItems - 1);
			this.heldItems.remove(amountOfItems - 1);


			return droppedItem;

		} else {
			return null;
		}
	}
	/*
	 *  Returns if hand is empty
	 */
	public boolean isEmpty(){
		return this.heldItems.isEmpty();
	}
	/*
	 * Returns if hand is full
	 */
	public boolean isFull(){
		if (this.heldItems.size() == 3){
			return true;
		}
		else{
			return false;
		}
	}

	/*
	 * Returns array of held items - used for PlayerRenderer to render items on player.
	 */
	public ArrayList<Item> getHeldItems(){
		return heldItems;
	}


}