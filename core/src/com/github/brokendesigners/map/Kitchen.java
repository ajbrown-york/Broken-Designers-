package com.github.brokendesigners.map;

import static java.lang.Math.abs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.github.brokendesigners.Constants;
import com.github.brokendesigners.item.ItemRegister;
import com.github.brokendesigners.map.interactable.*;

import java.util.ArrayList;

public class Kitchen {
	public static TiledMap tileMap = Constants.TILE_MAP;
	private ArrayList<KitchenCollisionObject> kitchenObstacles;
	private ArrayList<Station> kitchenStations;
	private ArrayList<CustomerStation> customerStations;

	public Kitchen(Camera camera, SpriteBatch spriteBatch){

		Matrix4 inverseProjection = spriteBatch.getProjectionMatrix().cpy();
		inverseProjection.inv();

		MapObjects mapObjects = tileMap.getLayers().get("Collision Layer").getObjects();
		kitchenObstacles = new ArrayList<>();
		customerStations = new ArrayList<>();
		for (RectangleMapObject rectangleMapObject : mapObjects.getByType(RectangleMapObject.class)){
			Rectangle rectangle = rectangleMapObject.getRectangle();
			Vector3 objectPosition = new Vector3(rectangle.x * Constants.UNIT_SCALE, rectangle.y * Constants.UNIT_SCALE, 0);

			kitchenObstacles.add(
				new KitchenCollisionObject(
					objectPosition,
					rectangle.width * Constants.UNIT_SCALE,
					rectangle.height * Constants.UNIT_SCALE));

		}
		MapObjects mapStations = tileMap.getLayers().get("Interact").getObjects();

		kitchenStations = new ArrayList<>();
		for (RectangleMapObject rectangleMapObject : mapStations.getByType(RectangleMapObject.class)){
			Rectangle rectangle = rectangleMapObject.getRectangle();
			Vector3 objectPosition = new Vector3(rectangle.x * Constants.UNIT_SCALE, rectangle.y * Constants.UNIT_SCALE, 0);

			if (rectangleMapObject.getProperties().get("objectType").equals("Dispenser")){

				kitchenStations.add(
					new DispenserStation(
						objectPosition,
						rectangle.width * Constants.UNIT_SCALE,
						rectangle.height * Constants.UNIT_SCALE,
							ItemRegister.itemRegister.get(rectangleMapObject.getProperties().get("itemType"))));

			} else if (rectangleMapObject.getProperties().get("objectType").equals("Counter")){

				float handX = (float)rectangleMapObject.getProperties().get("Center X");
				float handY = (float)rectangleMapObject.getProperties().get("Center Y");

				kitchenStations.add(
					new CounterStation(
						objectPosition,
						rectangle.width * Constants.UNIT_SCALE,
						rectangle.height * Constants.UNIT_SCALE,
						handX * Constants.UNIT_SCALE,
						handY * Constants.UNIT_SCALE));
			} else if (rectangleMapObject.getProperties().get("objectType").equals("CustomerCounter")){

				float handX = (float)rectangleMapObject.getProperties().get("Center X");
				float handY = (float)rectangleMapObject.getProperties().get("Center Y");

				CustomerStation station = new CustomerStation(
					objectPosition,
					rectangle.width * Constants.UNIT_SCALE,
					rectangle.height * Constants.UNIT_SCALE,
					handX * Constants.UNIT_SCALE,
					handY * Constants.UNIT_SCALE);

				kitchenStations.add(station);
				customerStations.add(station);
			} else if (rectangleMapObject.getProperties().get("objectType").equals("Bin")){

				kitchenStations.add(
					new BinStation(
						new Rectangle(
							objectPosition.x,
							objectPosition.y,
							rectangle.width * Constants.UNIT_SCALE,
							rectangle.height * Constants.UNIT_SCALE)));
			} else if (rectangleMapObject.getProperties().get("objectType").equals("Assembly")){

				kitchenStations.add(
						new AssemblyStation(
								objectPosition,
								rectangle.width * Constants.UNIT_SCALE,
								rectangle.height * Constants.UNIT_SCALE,
								objectPosition.x,
								objectPosition.y
						));
			}

		}
	}

	public ArrayList getKitchenObstacles() {
		return kitchenObstacles;
	}

	public ArrayList<? extends Station> getKitchenStations(){
		return kitchenStations;
	}

	public static TiledMap getTileMap() {
		return tileMap;
	}

	public static void dispose(){
		tileMap.dispose();
	}

	public void addKitchenObject(KitchenCollisionObject kitchenCollisionObject){
		kitchenObstacles.add(kitchenCollisionObject);
	}

	public ArrayList<CustomerStation> getCustomerStations() {
		return customerStations;
	}
}
