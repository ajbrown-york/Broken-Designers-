package com.github.brokendesigners.map.interactable;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.brokendesigners.Player;
import com.github.brokendesigners.item.Item;
import java.lang.reflect.InvocationTargetException;
/*
 * Dispenser station - extends station.
 */
public class DispenserStation extends Station {

	protected final Item dispenserItem;


	public DispenserStation(Rectangle interactionArea, Item dispenserItem) {
		super(interactionArea,"dispenser_station");
		this.dispenserItem = dispenserItem;
	}

	public DispenserStation(Vector2 worldPosition, float width, float height, Item dispenserItem){
		super(new Rectangle(worldPosition.x, worldPosition.y, width, height),"dispenser_station");
		this.dispenserItem = dispenserItem;
	}


	@Override
	public boolean pickUp(Player player) {

		player.hand.give(dispenserItem);
		pick_up.play();

		return true;
	}

	@Override
	public boolean dropOff(Player player)
	{
		return false;
	}

}
