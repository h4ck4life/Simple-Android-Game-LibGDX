package com.filavents.pandaikira;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainFruit extends Game {
	
	SpriteBatch batch;
	BitmapFont font;
	
	ActionResolver actionResolver;
	
	public MainFruit(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}
 
	public void create() {
		batch = new SpriteBatch();
		
		// Use LibGDX's default Arial font.
		font = new BitmapFont(Gdx.files.internal("font/cartoon.fnt"), Gdx.files.internal("font/cartoon.png"), false);
		font.setScale(0.8f);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		/*
		generator = new FreeTypeFontGenerator(Gdx.files.internal("myfont.ttf"));
		font = generator.generateFont(32); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		*/
		
		this.setScreen(new MainMenuScreen(this));
	}
 
	public void render() {
		super.render(); // important!
	}
 
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}
