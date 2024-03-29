package com.github.brokendesigners.map.interactable;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.github.brokendesigners.Constants;
import com.github.brokendesigners.Player;
import com.github.brokendesigners.bubble.ActionBubble;
import com.github.brokendesigners.bubble.Bubble;
import com.github.brokendesigners.item.Item;
import com.github.brokendesigners.item.ItemRegister;
import com.github.brokendesigners.renderer.BubbleRenderer;
import com.github.brokendesigners.textures.Animations;


public class CounterStation extends Station {

	Vector2 handPosition;
	Bubble bubble;


	public CounterStation(Rectangle interactionArea, float handX, float handY, BubbleRenderer bubbleRenderer) {

		super(interactionArea,"Counter_Station");

		handPosition = new Vector2(handX, handY);

		this.bubble = new ActionBubble(bubbleRenderer, this.handPosition,
			Animations.pattyFormingAnimation);


	}
	public CounterStation(Vector2 objectPosition, float width, float height, float handX, float handY, BubbleRenderer bubbleRenderer){
		super(new Rectangle(objectPosition.x, objectPosition.y, width, height),"Counter_Station");
		handPosition = new Vector2(handX, handY);
		System.out.println(handX);
		System.out.println(handY);
		this.bubble = new ActionBubble(bubbleRenderer, this.handPosition, Animations.pattyFormingAnimation);
	}


	public void renderCounter(SpriteBatch spriteBatch) {
		spriteBatch.begin();
		if (!this.hasEmptyHand()) {
			spriteBatch.draw(this.hand.getTexture(), this.handPosition.x, this.handPosition.y, 16 * Constants.UNIT_SCALE, 16 * Constants.UNIT_SCALE);
		}
		spriteBatch.end();

	}


	public Item formPatty() //Turn meat into patty
	{
		if(hand.getName()=="Meat")
		{
			hand =  ItemRegister.itemRegister.get("Patty");
		}
		return hand;

	}

	@Override
	public boolean action(final Player player){
		if (this.inuse == true) {
			return false;
		} else{
			this.inuse = true;
		}
		if (hand == null){
			return false;
		} else if (hand.equals(ItemRegister.itemRegister.get("Meat"))) {
			this.bubble.setVisible(true);
			interacting = true;
			Timer timer = new Timer();
			player.disableMovement();
			timer.scheduleTask(new Task() {
				@Override
				public void run() {
					player.enableMovement();
					bubble.setVisible(false);
					dumpHand();
					hand = ItemRegister.itemRegister.get("Patty");
					interacting = false;
					inuse = false;
				}
			}, 10f);
			return true;

		}
		return false;
	}



}
