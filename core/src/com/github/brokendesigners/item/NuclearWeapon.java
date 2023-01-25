package com.github.brokendesigners.item;

import com.badlogic.gdx.graphics.Texture;

public class NuclearWeapon extends Item{

	public Texture texture = new Texture("items/WMD.png");

	public NuclearWeapon() {
		super("Nuclear_Weapon");

	}


	@Override
	public Texture getTexture() {
		return texture;
	}


}
