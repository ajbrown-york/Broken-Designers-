package com.github.brokendesigners.map.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.brokendesigners.Constants;
import com.github.brokendesigners.Player;
import com.github.brokendesigners.item.Item;

import java.lang.reflect.InvocationTargetException;

public abstract class Station {

	protected Rectangle interactionArea;
	protected String station_name;
	public Item hand;
	public Boolean storing;
	public Vector2 handPosition = new Vector2(0,0);

	protected Station(Rectangle rectangle, String n) {
		this.station_name = n;
		this.hand = null;
		this.storing = false;
		this.interactionArea = rectangle;
		this.handPosition.x = this.interactionArea.x;
		this.handPosition.y = this.interactionArea.y;
	}


//	public boolean pickUp(Player player)
//		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//		return false;
//	}

	public Rectangle getInteractionArea() {
		return interactionArea;
	}

//	public boolean dropOff(Player player){
//		return false;
//	}

	public boolean action(Player player){
		return false;
	}

	//Check if operation can be completed
	public Boolean Applicable(String[] Conditions,String state, String itemName)
	{
		if(this.station_name == state) //Checks if its in the correct station before preforming
		{
			for(int i = 0;i<Conditions.length;i++)
			{
				if(Conditions[i] == itemName)
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasEmptyHand(){
		if (this.hand == null){
			return true;
		} else {
			return false;
		}
	}

	public void dumpHand(){
		this.hand = null;
	}

	public boolean pickUp(Player player){
		if (this.hasEmptyHand() || player.hand.isFull()){
			return false;
		} else {
			player.hand.give(hand);
			this.dumpHand();
			return true;
		}
	}

	public boolean dropOff(Player player){
		if (this.hasEmptyHand()){
			this.hand = player.hand.drop();
			return true;
		} else {
			return false;
		}
	}
	public void renderCounter(SpriteBatch spriteBatch) {
		spriteBatch.begin();
		if (!this.hasEmptyHand()) {
			spriteBatch.draw(this.hand.getTexture(), this.handPosition.x, this.handPosition.y, 16 * Constants.UNIT_SCALE, 16 * Constants.UNIT_SCALE);
		}
		spriteBatch.end();

	}


}
