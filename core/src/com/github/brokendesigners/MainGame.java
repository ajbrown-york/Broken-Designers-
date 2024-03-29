package com.github.brokendesigners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.brokendesigners.character.CustomerManager;
import com.github.brokendesigners.item.ItemRegister;
import com.github.brokendesigners.map.Kitchen;
import com.github.brokendesigners.map.KitchenCollisionObject;
import com.github.brokendesigners.map.interactable.Station;
import com.github.brokendesigners.menu.MenuScreen;
import com.github.brokendesigners.renderer.BubbleRenderer;
import com.github.brokendesigners.renderer.CustomerRenderer;
import com.github.brokendesigners.renderer.PlayerRenderer;
import com.github.brokendesigners.textures.Animations;
import com.github.brokendesigners.textures.Atlases;
import com.github.brokendesigners.textures.Textures;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class MainGame {
	ItemInitialiser itemInitialiser;

	PlayerRenderer playerRenderer;
	CustomerRenderer customerRenderer;
	BubbleRenderer bubbleRenderer;

	TiledMap map;
	OrthogonalTiledMapRenderer mapRenderer;

	InputProcessor inputProcessor;

	Player player1;
	Player player2;
	//Player player3;
	ArrayList<Player> playerList;
	int selectedPlayer;


	Kitchen kitchen;
	CustomerManager customerManager;
	ItemInitialiser initialiser;

	SpriteBatch spriteBatch;
	SpriteBatch hud_batch;
	ItemRegister itemRegister;

	float VIRTUAL_WIDTH = 32;  // Width of the world
	float VIRTUAL_HEIGHT = 18; // Height of the world

	OrthographicCamera camera;
	OrthographicCamera hud_cam;

	public MainGame(
		SpriteBatch spriteBatch,
		SpriteBatch hud_batch,
		OrthographicCamera gameCamera,
		OrthographicCamera hudCamera,
		PlayerRenderer playerRenderer,
		CustomerRenderer customerRenderer,
		BubbleRenderer bubbleRenderer,
		OrthogonalTiledMapRenderer mapRenderer,
		InputProcessor inputProcessor
	){
		this.playerRenderer = playerRenderer;
		this.customerRenderer = customerRenderer;
		this.bubbleRenderer = bubbleRenderer;
		this.map = map;
		this.mapRenderer = mapRenderer;
		this.inputProcessor = inputProcessor;
		this.spriteBatch = spriteBatch;
		this.camera = gameCamera;
		this.hud_cam = hudCamera;
		this.hud_batch = hud_batch;
	}


	public void create(){






		// MAP & MAP OBJECT BUILDING
		this.kitchen = new Kitchen(camera, spriteBatch, bubbleRenderer);

		ArrayList<KitchenCollisionObject> kitchenCollisionObjects = kitchen.getKitchenObstacles();

		customerManager = new CustomerManager( // Manages when customers should spawn in and holds the Timer
			customerRenderer,
			this.bubbleRenderer,
			5,
			kitchen.getCustomerSpawnPoint(),
			kitchen.getCustomerStations());

		// BUILD PLAYERS
		initialisePlayers(); //initialisePlayers is at the end of this java class.


		spriteBatch.enableBlending();
		customerManager.begin();
	}

	public void renderGame(){

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			Gdx.gl.glClearColor(14 / 255f, 104 / 255f, 44 / 255f, 1f);

			camera.update();

			spriteBatch.setProjectionMatrix(camera.combined);
			mapRenderer.setView(camera);

			player1.processMovement(kitchen.getKitchenObstacles());
			player2.processMovement(kitchen.getKitchenObstacles());
			//player3.processMovement(kitchen.getKitchenObstacles());

			if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
				player1.setSelected(true);
				player2.setSelected(false);
				//player3.setSelected(false);
				selectedPlayer = 0;

			} else if (Gdx.input.isKeyPressed(Keys.NUM_2)) {
				player1.setSelected(false);
				player2.setSelected(true);
				//player3.setSelected(false);
				selectedPlayer = 1;

			} /*else if (Gdx.input.isKeyPressed(Keys.NUM_3)){
			player1.setSelected(false);
			player2.setSelected(false);
			player3.setSelected(true);
			selectedPlayer = 2;
			}*/


			spriteBatch.begin();
			// Renders map in specific order to allow some cool rendering effects.
			mapRenderer.renderTileLayer(
				(TiledMapTileLayer) mapRenderer.getMap().getLayers().get("Floor"));
			mapRenderer.renderTileLayer(
				(TiledMapTileLayer) mapRenderer.getMap().getLayers().get("Walls"));
			mapRenderer.renderTileLayer(
				(TiledMapTileLayer) mapRenderer.getMap().getLayers().get("Extras"));
			spriteBatch.end();

			customerRenderer.renderCustomers();

			playerRenderer.renderPlayers();

			bubbleRenderer.renderBubbles();

			camera.position.set(new Vector3(playerList.get(selectedPlayer).worldPosition, 1));
			// ^^ camera follows selected player

			spriteBatch.begin();
			mapRenderer.renderTileLayer(
				(TiledMapTileLayer) mapRenderer.getMap().getLayers().get("Front"));
			// ^^ renders this layer after player which allows the player to go behind walls.
			spriteBatch.end();
			customerManager.update(spriteBatch, hud_batch);

			for (Station station : kitchen.getKitchenStations()) {

				station.renderCounter(spriteBatch);
			}
		}

	public void initialisePlayers(){

		//ANIMATION ARRAYS:
		ArrayList<Animation<TextureRegion>> glibbert_animations = new ArrayList<>();
		// Array of player animations
		// Index 0 is idle animation
		// Index 1 is move animation
		// Index 2 is action animation
		glibbert_animations.add(Animations.glibbert_idleAnimation);
		glibbert_animations.add(Animations.glibbert_moveAnimation);
		glibbert_animations.add(Animations.glibbert_actionAnimation);
		playerRenderer = new PlayerRenderer(spriteBatch);

		ArrayList<Animation<TextureRegion>> glibbert2_animations = new ArrayList<>();
		glibbert2_animations.add(Animations.glibbert_idleAnimation2);
		glibbert2_animations.add(Animations.glibbert_moveAnimation2);
		glibbert2_animations.add(Animations.glibbert_actionAnimation2); // bluggus has no action animation but still needs to have an animation referenced


		//BUILDING PLAYERS
		playerList = new ArrayList<>(); // List of Players - used to determine which is active

		player1 = new Player(playerRenderer, glibbert_animations, kitchen.getPlayerSpawnPoint(), 20 * Constants.UNIT_SCALE, 36 * Constants.UNIT_SCALE);
		player1.setRenderOffsetX(-1 * Constants.UNIT_SCALE);
		// ^^ Offset where the sprite will render relative to the invisible rectangle
		// which represents the players position/collision boundaries

		playerList.add(player1);

		// repeat for Player 2 & 3
		player2 = new Player(playerRenderer, glibbert2_animations, new Vector2(kitchen.getPlayerSpawnPoint().x + 32 * Constants.UNIT_SCALE, kitchen.getPlayerSpawnPoint().y), 20 * Constants.UNIT_SCALE, 36 * Constants.UNIT_SCALE);
		playerList.add(player2);
		player2.setRenderOffsetX(-1 * Constants.UNIT_SCALE);
		//player3 = new Player(playerRenderer, glibbert_animations, new Vector3(1, 0, 0), 20 * Constants.UNIT_SCALE, 36 * Constants.UNIT_SCALE);
		//playerList.add(player3);
		player1.setSelected(true);
		selectedPlayer = 0;

	}
	public void end(){
		customerManager.end();
		for (Player player : playerList){
			if (player.flipped){
				player.flipAnimations();
			}
		}
		customerRenderer.end();
	}


}
