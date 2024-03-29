package com.github.brokendesigners;

import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.brokendesigners.map.KitchenCollisionObject;
import com.github.brokendesigners.map.interactable.Station;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import com.github.brokendesigners.renderer.PlayerRenderer;
import com.github.brokendesigners.textures.Animations;

public class Player {
	Vector2 worldPosition; // Position of the player in world-coords

	public float MOVEMENT_SPEED = 2 * Constants.UNIT_SCALE;  // Movement Speed of Chef differs between vertical and horizontal due to following 2 lines

	private float width; //NOTE:  NOT THE WIDTH OF CHEF SPRITE
	private float height;	//NOTE: NOT HEIGHT OF CHEF SPRITE

	public final float SPRITE_WIDTH;		// Width of chef when drawn
	public final float SPRITE_HEIGHT;		// Height of chef when drawn

	public Hand hand; // Items held by the chef

	Rectangle playerRectangle; // Rectangle representation of chef - Used for interactions/collisions

	private boolean selected; // Is Player Selected

	public Texture texture; // Texture of player if there are no animations for player
	public ArrayList<Animation<TextureRegion>> animations; // A list of animations
	public float keyFrame; // float updated every frame - used to determine which frame of animation of the player should render
	public boolean flipped; // is flipped or not -- NOT USED
	public boolean moving_disabled = false; // Is moving disabled? - Moving is disabled when interacting with most stations.

	float renderOffsetX = 0;
	// ^^ Offset where the sprite will render relative to the invisible rectangle
	// which represents the players position/collision boundaries

	/*
	* Instantiates player with a texture - for if you dont want to make animations for them.
 	*/
	public Player(PlayerRenderer renderer, Texture texture, Vector2 worldPosition){

		this.worldPosition = worldPosition;

		SPRITE_HEIGHT = this.texture.getHeight() * Constants.UNIT_SCALE; // Sets height of player to height of player's texture
		SPRITE_WIDTH = this.texture.getWidth() * Constants.UNIT_SCALE; // Sets width of player to width of player's texture




		this.texture = texture;

		hand = new Hand(); // Instantiates Hand so the player can pick items up

		renderer.addPlayer(this); // Adds player to the renderer's "List of Players to Render every frame"

		boolean flipped = false;

		this.width = 18 * Constants.UNIT_SCALE; // default values for the dimensions of the Player Rectangle
		this.height = 4 * Constants.UNIT_SCALE; //
		this.renderOffsetX = -1; // Default renderOffsetX

		playerRectangle = new Rectangle(worldPosition.x, worldPosition.y, this.width, this.height);


	}
	/*
	* Instantiates player with array of animations.
	* Array has 3 animations :
	* 		Index 0 : Idle animation
	* 		Index 1 : Move animation
	* 		Index 2 : Interaction Animation
	 */
	public Player(PlayerRenderer renderer, ArrayList<Animation<TextureRegion>> animations, Vector2 worldPosition, float sprite_width, float sprite_height){

		this.worldPosition = worldPosition;

		SPRITE_HEIGHT = sprite_height;
		SPRITE_WIDTH = sprite_width;

		this.animations = animations;

		hand = new Hand();

		renderer.addPlayer(this);
		this.width = 18 * Constants.UNIT_SCALE;
		this.height = 4 * Constants.UNIT_SCALE;

		playerRectangle = new Rectangle(worldPosition.x, worldPosition.y, this.width, this.height);
	}
	/*
	* Gets player rectangle, used for calculating collisions and interactions.
	 */
	public Rectangle getPlayerRectangle() {
		return playerRectangle;
	}
	/*
	* updates position of rectangle - ideally size of rectangle should stay constant.
	 */
	private void updateRectangle(){
		playerRectangle.x = worldPosition.x;
		playerRectangle.y = worldPosition.y;

	}
	/*
	* returns worldPosition
	* Used by PlayerRenderer to render players in correct location.
	 */
	public Vector2 getWorldPosition(){
		return worldPosition;
	}
	/*
	* Processes movement inputs.
	* each axis has its own if statement to allow diagonal movement and sliding across collision objects without sticking.
	 */
	public void processMovement(ArrayList<KitchenCollisionObject> objects){

		if (this.selected == true && !this.moving_disabled) {

			if (Gdx.input.isKeyPressed(Keys.W)) {
				this.moveUp(objects);
			} else if (Gdx.input.isKeyPressed(Keys.S)) {
				this.moveDown(objects);
			}
			this.updateRectangle();
			if (Gdx.input.isKeyPressed(Keys.A)) {
				this.moveLeft(objects);
				if (!this.flipped){
					this.flipAnimations();
					this.flipped = true;
				}
			} else if (Gdx.input.isKeyPressed(Keys.D)) {
				this.moveRight(objects);
				if (this.flipped){
					this.flipAnimations();
					this.flipped = false;
				}

			}
			this.updateRectangle();
		}
	}
	/*
	* Flips animation set for the player.
	* Allows the player to look in both directions. be careful if reusing animations, the .flip() function is one way - if you flip it,
	* you have to call .flip(true, false) again to flip it back.
	 */
	public void flipAnimations(){
		for (Animation animation : animations){
			for (TextureRegion region : (TextureRegion[]) animation.getKeyFrames()){
				region.flip(true, false);
			}
		}
	}
	/*
	* Handles moving and collision detection for when you move up.
	* The move methods are separate because of special cases in the collision detection.
	* ^^ If a players position + their movement speed is inside an object, their position still needs to change,
	* so we set the players position to where they are making contact with the object they're colliding with.
	 */
	public boolean moveUp(ArrayList<KitchenCollisionObject> objects){
		this.playerRectangle.y += (this.MOVEMENT_SPEED);
		for (KitchenCollisionObject object : objects){
			if(Intersector.overlaps(object.getRectangle(), this.getPlayerRectangle())){
				this.worldPosition.y = object.getRectangle().y - this.height;

				return false;
			}
		}
		this.worldPosition.y += (this.MOVEMENT_SPEED);
		this.updateRectangle();
		return true;

	}
	public boolean moveDown(ArrayList<KitchenCollisionObject> objects){
		this.playerRectangle.y -= (this.MOVEMENT_SPEED);
		for (KitchenCollisionObject object : objects){
			if(Intersector.overlaps(object.getRectangle(), this.getPlayerRectangle())){
				this.worldPosition.y = object.getRectangle().y + object.getHEIGHT();
				return false;
			}
		}
		this.worldPosition.y -= (this.MOVEMENT_SPEED);
		this.updateRectangle();
		return true;

	}
	public boolean moveRight(ArrayList<KitchenCollisionObject> objects){
		this.playerRectangle.x += (this.MOVEMENT_SPEED);
		for (KitchenCollisionObject object : objects){
			if(Intersector.overlaps(object.getRectangle(), this.getPlayerRectangle())){
				this.worldPosition.x = object.getRectangle().x - this.width;
				return false;
			}
		}

		this.worldPosition.x += (this.MOVEMENT_SPEED);
		this.updateRectangle();
		return true;

	}
	public boolean moveLeft(ArrayList<KitchenCollisionObject> objects){
		this.playerRectangle.x -= (this.MOVEMENT_SPEED);
		for (KitchenCollisionObject object : objects){
			if(Intersector.overlaps(object.getRectangle(), this.getPlayerRectangle())){
				this.worldPosition.x = object.getRectangle().x + object.getWIDTH();
				return false;
			}
		}
		this.worldPosition.x -= (this.MOVEMENT_SPEED);
		this.updateRectangle();
		return true;

	}
	/*
	 * Handles picking up of items.
	 * Scans through an array of kitchen objects, finds if they are intersecting with any, and if it does, it initiates
	 * a station.pickUp(Player player) method.
	 */
	public boolean pickUp(ArrayList<? extends Station> stations)
		throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		if (this.isSelected()) {
			for (Station station : stations) {
				if (Intersector.overlaps(station.getInteractionArea(), this.getPlayerRectangle())) {
					station.pickUp(this);
					return true;
				}
			}
		}
		return false;
	}
	/*
	 * Handles dropping off of items.
	 * Scans through an array of kitchen objects, finds if they are intersecting with any, and if it does, it initiates
	 * a station.dropOff(Player player) method.
	 */
	public boolean dropOff(ArrayList<? extends Station> stations){
		if (this.isSelected()) {
			for (Station station : stations) {
				if (Intersector.overlaps(station.getInteractionArea(), this.getPlayerRectangle())) {
					station.dropOff(this);
					return true;
				}
			}
		}
		return false;
	}
	/*
	 * Sets whether or not player is selected. Used by the PlayerRenderer to help decide which frame to render at any
	 * given time.
	 */
	public void setSelected(boolean isSelected){
		this.selected = isSelected;
	}
	/*
	 * Returns if player is selected or not.
	 */
	public boolean isSelected() {
		return selected;
	}

	/*
	 * Handles Interacting with items.
	 * Scans through an array of kitchen objects, finds if they are intersecting with any, and if it does, it initiates
	 * a station.action(Player player) method.
	 */
	public boolean interact(ArrayList<? extends Station> stations){
		if (this.isSelected()) {
			for (Station station : stations) {
				if (Intersector.overlaps(station.getInteractionArea(), this.getPlayerRectangle())) {
					station.action(this);
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * disposes of textures in memory.
	 */
	public void dispose(){
		if (this.texture != null){
			this.texture.dispose();
		}
	}

	/*
	 * Disables movement of player - called when player starts interacting with stations - also helps PlayerRenderer
	 * decide which frame to render.
	 */
	public void disableMovement(){
		this.moving_disabled = true;
	}
	/*
	 * Enables movement
	 */
	public void enableMovement(){
		this.moving_disabled = false;
	}
	/*
	 * Sets width of player's rectangle.
	 */
	public void setWidth(float width) {
		this.width = width;
		playerRectangle.width = this.width;
	}
	/*
	 * Sets height of player's rectangle.
	 */
	public void setHeight(float height) {
		this.height = height;
		playerRectangle.height = this.height;
	}

	/*
	 * Sets renderOffsetX
	 */
	public void setRenderOffsetX(float renderOffsetX) {
		this.renderOffsetX = renderOffsetX;
	}
	/*
	 * Returns renderOffsetX - Used by PlayerRenderer to render player in the right place
	 * relative to the player's hitbox
	 */
	public float getRenderOffsetX() {
		return renderOffsetX;
	}
}
