package com.filavents.pandaikira;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class FruitTexture extends Texture {

	int type;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public FruitTexture(FileHandle file) {
		super(file);
		// TODO Auto-generated constructor stub
	}

}
