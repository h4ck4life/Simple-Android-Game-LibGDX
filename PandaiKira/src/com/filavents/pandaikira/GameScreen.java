package com.filavents.pandaikira;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

	final MainFruit game;

	Texture watermelon, grape, banana, strawberry, pineapple;
	Array<Texture> fruits = new Array<Texture>();

	Texture bucketImage;
	Texture fruitRender;
	FruitTexture tmpFruit;

	// Game UI
	TextureAtlas gameuiTexture;
	TextureAtlas monkeyAtlas;
	TextureAtlas gameButtons;

	Sound dropSound;
	Sound bonusSound;
	Sound explodeSound;
	Sound powerupSound;
	Sound monkeyAASound;
	Sound menuSelectSound;
	// Music rainMusic;

	OrthographicCamera camera;
	Rectangle bucket;

	HashMap<FruitTexture, Rectangle> raindrops;
	Array<Sprite> hearts = new Array<Sprite>();

	long lastDropTime;
	int dropsGathered;
	float elapsedTime = 0;
	long actionBeginTime = 0;

	boolean bonus = false;

	private Texture bgImage;

	private Texture cloud;
	Rectangle cloudArea;

	private Texture cloud_2;
	Rectangle cloudArea_2;

	static final int WATERMELON = 0;
	static final int PINEAPPLE = 3;
	static final int BANANA = 1;
	static final int GRAPE = 2;
	static final int STRAWBERRY = 4;
	static final int BOMB = 5;
	static final int HEART = 6;

	Sprite spr;

	AtlasRegion gameover;
	Sprite gameOverSprite;
	Rectangle gameOverArea;

	AtlasRegion getReady;
	Sprite getReadySprite;
	Rectangle getReadyArea;

	AtlasRegion tapRight;
	Sprite tapRightSprite;
	Rectangle tapRightArea;

	AtlasRegion tapLeft;
	Sprite tapLeftSprite;
	Rectangle tapLeftArea;

	AtlasRegion tapTick;
	Sprite tapTickSprite;
	Rectangle tapTickArea;

	AtlasRegion monkeyFirstFrame;
	AtlasRegion monkeyFourFrame;
	AtlasRegion monkeySecondFrame;
	AtlasRegion monkeyFiveFrame;
	Array<AtlasRegion> monkeyAllFrames = new Array<AtlasRegion>();
	float fps = 0.8f; // Time between frames in seconds
	float fpsx = 0.05f;
	Animation monkeyFirstToSecond;
	Array<AtlasRegion> monkeyHitBuahFrame = new Array<AtlasRegion>();
	Animation monkeyKenaHitBuat;

	ParticleEffect fireParticle;
	ParticleEffect hitparticle;

	// game vars
	float light = 1f;
	int jumlahbuah = 700000000;
	int bonusSpeed = 30;
	int normalSpeed = 400;
	int heartCount = 3;
	boolean thrown = false;
	boolean hitted = false;

	// game state
	static boolean GAME_PAUSED = false;
	static boolean GAME_OVER = false;
	static boolean GAME_RUNNING = false;
	static boolean GAME_GETREADY = true;

	float stateTime = 0f;
	TextureRegion currentFrame;

	// Sound ID
	long dropSoundId = 0;
	long bonusSoundId = 1;
	long explodeSoundId = 2;
	long powerupSoundId = 3;
	long monkeyAASoundId = 4;

	float volume = 0.4f;

	AtlasRegion pausewindow;
	Rectangle pauseWindowArea;
	
	Preferences prefs;

	public GameScreen(final MainFruit gam) {
		this.game = gam;
		
		prefs = Gdx.app.getPreferences("fruitapeprefs");

		// bucketImage = new Texture(Gdx.files.internal("character_128.png"));
		monkeyAtlas = new TextureAtlas(
				Gdx.files.internal("monkey/character.pack"));
		monkeyFirstFrame = monkeyAtlas.findRegion("character");
		monkeySecondFrame = monkeyAtlas.findRegion("character", 2);
		monkeyFourFrame = monkeyAtlas.findRegion("character", 4);
		monkeyFiveFrame = monkeyAtlas.findRegion("character", 5);
		// monkeyAllFrames = monkeyAtlas.findRegions("character");
		monkeyAllFrames.add(monkeyFirstFrame);
		monkeyAllFrames.add(monkeySecondFrame);
		monkeyAllFrames.add(monkeyFirstFrame);
		monkeyAllFrames.add(monkeyFourFrame);

		monkeyFirstToSecond = new Animation(fps, monkeyAllFrames,
				Animation.LOOP);

		monkeyHitBuahFrame.add(monkeyFirstFrame);
		monkeyHitBuahFrame.add(monkeyFiveFrame);
		monkeyHitBuahFrame.add(monkeyFirstFrame);
		monkeyKenaHitBuat = new Animation(fpsx, monkeyHitBuahFrame,
				Animation.NORMAL);

		cloud = new Texture(Gdx.files.internal("whiteclouds.png"));
		cloudArea = new Rectangle(0, 100, 919, 300);

		cloud_2 = new Texture(Gdx.files.internal("whiteclouds.png"));
		cloudArea_2 = new Rectangle(-919, 100, 919, 300);

		// particles
		fireParticle = new ParticleEffect();
		fireParticle.load(Gdx.files.internal("particles/fire"),
				Gdx.files.internal("particles"));

		hitparticle = new ParticleEffect();
		hitparticle.load(Gdx.files.internal("particles/hitme.p"),
				Gdx.files.internal("particles"));

		// Game UI
		gameuiTexture = new TextureAtlas(
				Gdx.files.internal("texturepack/ui.pack"));
		// Game Over
		gameover = gameuiTexture.findRegion("gameoverwindow");
		gameOverSprite = new Sprite(gameover);
		gameOverArea = new Rectangle(220, -365,
				gameover.getRotatedPackedWidth(),
				gameover.getRotatedPackedHeight());
		// Game Ready
		getReady = gameuiTexture.findRegion("textGetReady");
		getReadySprite = new Sprite(getReady);
		getReadyArea = new Rectangle(220, 350,
				getReady.getRotatedPackedWidth(),
				getReady.getRotatedPackedHeight());
		// tap right
		tapRight = gameuiTexture.findRegion("tapRight");
		tapRightSprite = new Sprite(tapRight);
		tapRightArea = new Rectangle(300, 280,
				tapRight.getRotatedPackedWidth(),
				tapRight.getRotatedPackedHeight());
		// tap left
		tapLeft = gameuiTexture.findRegion("tapLeft");
		tapLeftSprite = new Sprite(tapLeft);
		tapLeftArea = new Rectangle(460, 280, tapLeft.getRotatedPackedWidth(),
				tapLeft.getRotatedPackedHeight());
		// tap tick
		tapTick = gameuiTexture.findRegion("tapTick");
		tapTickSprite = new Sprite(tapTick);
		tapTickArea = new Rectangle(
				200 + (getReady.getRotatedPackedWidth() / 2), 260,
				tapTick.getRotatedPackedWidth(),
				tapTick.getRotatedPackedHeight());

		// Buttons UI
		gameButtons = new TextureAtlas(Gdx.files.internal("button.pack"));
		pausewindow = gameButtons.findRegion("pausewindow");
		pauseWindowArea = new Rectangle(220, -250,
				pausewindow.getRegionWidth(), pausewindow.getRegionHeight());

		// load 3 hearts
		for (int i = 0; i < 3; i++) {
			hearts.add(new Sprite(new Texture(Gdx.files.internal("heart.png"))));
		}

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
		bonusSound = Gdx.audio.newSound(Gdx.files.internal("bonus.wav"));
		explodeSound = Gdx.audio.newSound(Gdx.files.internal("explode.wav"));
		powerupSound = Gdx.audio.newSound(Gdx.files.internal("powerup.wav"));
		monkeyAASound = Gdx.audio.newSound(Gdx.files.internal("a.wav"));
		menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
		// rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
		// rainMusic.setLooping(true);
		// rainMusic.setVolume(0f);

		// set background image
		bgImage = new Texture(Gdx.files.internal("bg-png.png"));
		spr = new Sprite(bgImage);

		Texture.setEnforcePotImages(false);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		bucket.y = 5; // bottom left corner of the bucket is 20 pixels above
						// the bottom screen edge
		bucket.width = 64;
		bucket.height = 64;

		// create the raindrops array and spawn the first raindrop
		raindrops = new HashMap<FruitTexture, Rectangle>();

	}

	private void spawnRaindrop() {
		// fruitRender = fruits.get(MathUtils.random(4));
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;

		int randRange = MathUtils.random(4);

		// Drop the bomb
		if (dropsGathered % 1 == 0 && dropsGathered != 0 && bonus == false
				&& heartCount != 0) {
			randRange = MathUtils.random(5);
		}

		// Drop the heart
		if (dropsGathered % 100 == 0 && dropsGathered != 0 && bonus == false
				&& heartCount < 3 && thrown == false) {

			tmpFruit = new FruitTexture(Gdx.files.internal("fruits/" + 6
					+ ".png"));
			randRange = 6;
			thrown = true;

		} else {

			tmpFruit = new FruitTexture(Gdx.files.internal("fruits/"
					+ randRange + ".png"));
		}

		switch (randRange) {
		case WATERMELON:
			tmpFruit.setType(WATERMELON);
			break;
		case BANANA:
			tmpFruit.setType(BANANA);
			break;
		case GRAPE:
			tmpFruit.setType(GRAPE);
			break;
		case PINEAPPLE:
			tmpFruit.setType(PINEAPPLE);
			break;
		case STRAWBERRY:
			tmpFruit.setType(STRAWBERRY);
			break;
		case BOMB:
			tmpFruit.setType(BOMB);
			break;
		case HEART:
			tmpFruit.setType(HEART);
			break;
		}

		raindrops.put(tmpFruit, raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void damnBonus() {
		bonus = true;
		// bonusSound.play(volume);
	}

	private void changeLighting() {
		// System.out.println("MILIS: " + TimeUtils.millis());
		// CONTOH FORMAT DELTA TIME: 0.01684818
		if (TimeUtils.millis() % 100 == 0 && TimeUtils.millis() != 0) {
			spr.setColor(1, 1, 1, light);
			light -= 0.1f;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void render(float delta) {

		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		game.batch.begin();

		spr.draw(game.batch);
		// game.batch.draw(bgImage, 0, 0);

		int tmpHeart = 20;
		for (int i = 0; i < 3; i++) {
			hearts.get(i).draw(game.batch);
			hearts.get(i).setX(780 - (tmpHeart));
			hearts.get(i).setY(430);
			tmpHeart += 35;
		}

		game.font.draw(game.batch, dropsGathered + " fruits", 20, 460);

		// game.batch.draw(cloud, cloudArea.getX(), cloudArea.getY());
		// game.batch.draw(cloud_2, cloudArea_2.getX(), cloudArea_2.getY());

		// game.batch.draw(bucketImage, bucket.x, bucket.y);
		stateTime += Gdx.graphics.getDeltaTime();

		if (hitted == true) {
			currentFrame = monkeyKenaHitBuat.getKeyFrame(stateTime, false);
			if (monkeyKenaHitBuat.getKeyFrameIndex(stateTime) == 2) {
				hitted = false;
			}
		} else {
			currentFrame = monkeyFirstToSecond.getKeyFrame(stateTime, true);
		}

		game.batch.draw(currentFrame, bucket.x, bucket.y);

		for (Map.Entry<FruitTexture, Rectangle> entry : raindrops.entrySet()) {
			game.batch.draw(entry.getKey(), entry.getValue().x,
					entry.getValue().y);
		}
		
		fireParticle.draw(game.batch);
		hitparticle.draw(game.batch);

		// GAME PAUSED
		game.batch.draw(pausewindow, pauseWindowArea.getX(),
				pauseWindowArea.getY());
		// GAME OVER
		game.batch.draw(gameOverSprite, gameOverArea.getX(),
				gameOverArea.getY());
		// GET READY
		game.batch.draw(getReadySprite, getReadyArea.getX(),
				getReadyArea.getY());
		// TAP RIGHT
		game.batch.draw(tapRightSprite, tapRightArea.getX(),
				tapRightArea.getY());
		// TAP LEFT
		game.batch.draw(tapLeftSprite, tapLeftArea.getX(), tapLeftArea.getY());
		// TAP TICK
		game.batch.draw(tapTickSprite, tapTickArea.getX(), tapTickArea.getY());


		game.batch.end();

		while (GAME_GETREADY == true) {
			if (Gdx.input.isTouched()) {
				menuSelectSound.play(volume);
				GAME_GETREADY = false;
				GAME_RUNNING = true;
				getReadyArea.y += 800;
				tapTickArea.y += 800;
				tapRightArea.y += 800;
				tapLeftArea.y += 800;
			}

			break;
		}

		moveTheFruits();

		// changeLighting();
		fireParticle.update(delta);
		hitparticle.update(delta);
		
		// Game Achievements
		 if (dropsGathered >= 100) game.actionResolver.unlockAchievementGPGS("CgkIp-26x7gZEAIQAQ"); // ninja
		 if (dropsGathered >= 300) game.actionResolver.unlockAchievementGPGS("CgkIp-26x7gZEAIQAg"); // Sifu
		 if (dropsGathered >= 500) game.actionResolver.unlockAchievementGPGS("CgkIp-26x7gZEAIQAw"); // Master
		 if (dropsGathered >= 800) game.actionResolver.unlockAchievementGPGS("CgkIp-26x7gZEAIQBA"); // Mahaguru
		 if (dropsGathered >= 1200) game.actionResolver.unlockAchievementGPGS("CgkIp-26x7gZEAIQBQ"); // Lord

		// process user input
		if (Gdx.input.isTouched()) {

			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);

			if (GAME_OVER != true) {

				if (GAME_PAUSED == true) {
					if (Intersector.isPointInTriangle(touchPos.x, touchPos.y,
							310, 355, 270, 305, 350, 305) == true) {
						menuSelectSound.play(volume);
						pauseWindowArea.y = -250;
						GAME_PAUSED = false;
					}

					if (Intersector.isPointInTriangle(touchPos.x, touchPos.y,
							410, 355, 373, 305, 445, 305) == true) {
						menuSelectSound.play(volume);
						GAME_PAUSED = false;
						GAME_RUNNING = false;
						game.setScreen(new GameScreen(game));
					}
				}

				if (GAME_PAUSED != true) {
					bucket.x = touchPos.x - 64 / 2;
				}

				/*
				 * if (bucket.x != touchPos.x - 64 / 2) { if (bucket.x <
				 * touchPos.x) bucket.x += 1200 * Gdx.graphics.getDeltaTime();
				 * 
				 * if (bucket.x > touchPos.x) bucket.x -= 1200 *
				 * Gdx.graphics.getDeltaTime(); }
				 */
			}

			if (GAME_OVER == true) {
				
				if (game.actionResolver.getSignedInGPGS()) {
					 prefs.putInteger("currentscore", dropsGathered);
					 prefs.putInteger("highscore", dropsGathered);
					 prefs.putInteger("totalscore", dropsGathered);
					 prefs.flush();
					 game.actionResolver.submitScoreGPGS(dropsGathered);
				}

				// Restart button
				Array<Vector2> restartbtn = new Array<Vector2>();
				restartbtn.add(new Vector2(380, 320));
				restartbtn.add(new Vector2(380, 40));
				restartbtn.add(new Vector2(440, 340));
				restartbtn.add(new Vector2(440, 40));
				if (Intersector.isPointInPolygon(restartbtn, new Vector2(
						touchPos.x, touchPos.y))) {
					menuSelectSound.play(volume);
					GAME_RUNNING = false;
					game.setScreen(new GameScreen(game));
				}

				// Home button
				/*
				 * Array<Vector2> hometbtn = new Array<Vector2>();
				 * hometbtn.add(new Vector2(330, 110)); hometbtn.add(new
				 * Vector2(330, 70)); hometbtn.add(new Vector2(370, 110));
				 * hometbtn.add(new Vector2(70, 370)); if
				 * (Intersector.isPointInPolygon(hometbtn, new
				 * Vector2(touchPos.x, touchPos.y))) {
				 * System.out.println("Go home!"); }
				 */
			}

			if (Gdx.input.isKeyPressed(Keys.LEFT))
				bucket.x -= 200 * Gdx.graphics.getDeltaTime();
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
				bucket.x += 200 * Gdx.graphics.getDeltaTime();

			// make sure the bucket stays within the screen bounds
			if (bucket.x < 0)
				bucket.x = 0;
			if (bucket.x > 800 - 128)
				bucket.x = 800 - 128;

		}

		switch (dropsGathered) {
		case 10:
			jumlahbuah = 600000000;
			normalSpeed = 500;
			break;
		case 30:
			jumlahbuah = 500000000;
			break;
		case 100:
			jumlahbuah = 300000000;
			normalSpeed = 600;
			break;
		case 200:
			normalSpeed = 700;
			break;
		case 300:
			normalSpeed = 800;
			break;
		}

		if (dropsGathered % 120 == 0 && dropsGathered != 0) {
			damnBonus();
		}

		if (GAME_OVER == true) {
			if (gameOverArea.y <= 50) {
				gameOverArea.y += 800 * Gdx.graphics.getDeltaTime();
			}
			// System.out.println(gameOverArea.getY());
		}

		if (GAME_PAUSED == true) {
			if (pauseWindowArea.y <= 250) {
				pauseWindowArea.y += 1500 * Gdx.graphics.getDeltaTime();
			}
		}

		// check if we need to create a new raindrop
		if (GAME_OVER != true && GAME_GETREADY == false && GAME_PAUSED != true) {
			if (TimeUtils.nanoTime() - lastDropTime > (bonus == true ? 20000000
					: jumlahbuah)) {
				spawnRaindrop();
				bonus = false;
			}
		}

		// Goto right and repeat
		cloudArea.x += 30 * Gdx.graphics.getDeltaTime();
		if (cloudArea.x > 800) {
			cloudArea.x = -919;
		}

		// Goto right and repeat
		cloudArea_2.x += 30 * Gdx.graphics.getDeltaTime();
		if (cloudArea_2.x > 800) {
			cloudArea_2.x = -919;
		}

	}

	private void moveTheFruits() {
		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator iter = raindrops.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry raindrop = (Map.Entry) iter.next();
			Rectangle fallingFruit = (Rectangle) raindrop.getValue();
			FruitTexture keyfruit = (FruitTexture) raindrop.getKey();

			if (GAME_PAUSED != true) {
				fallingFruit.y -= (bonus == true ? bonusSpeed : normalSpeed)
						* Gdx.graphics.getDeltaTime();
			}

			if (fallingFruit.y + 64 < 0) {
				iter.remove();
				keyfruit.dispose();
			}
			if (fallingFruit.overlaps(bucket)) {
				// Kalau kena bomb baq hang !!
				if (keyfruit.getType() == BOMB) {

					if (heartCount >= 1 && heartCount < 4) {

						fireParticle.setPosition(bucket.getX() + (128 / 2),
								bucket.getY() + (bucket.getHeight() - 20));
						fireParticle.start();

						hearts.get(heartCount != 0 ? heartCount - 1 : 0)
								.setColor(1, 1, 1, 0f);
						heartCount--;
						thrown = false;
						// monkeyAASoundId = monkeyAASound.play(volume);
						explodeSoundId = explodeSound.play(volume);

						hitted = true;
						stateTime = 0;

						if (heartCount == 0) {
							GAME_OVER = true;
							GAME_RUNNING = false;
						}

					}

				} else if (keyfruit.getType() == HEART) {

					if (heartCount < 3) {
						powerupSoundId = powerupSound.play(volume);
						heartCount++;
						hearts.get(heartCount != 0 ? heartCount - 1 : 0)
								.setColor(1, 1, 1, 1f);
					}

				} else {

					hitparticle.allowCompletion();
					hitparticle.setPosition(bucket.getX() + (128 / 2),
							bucket.getY() + (bucket.getHeight() - 20));
					hitparticle.start();

					hitted = true;
					stateTime = 0;

					dropSoundId = dropSound.play(volume);
					dropsGathered++;
				}
				iter.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {

		// start the playback of the background music
		// when the screen is shown
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyUp(final int keycode) {
				if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {

					if (GAME_RUNNING == true) {
						// GAME_PAUSED = GAME_PAUSED == true ? false : true;
						GAME_PAUSED = true;
					}

					if (GAME_GETREADY == true) {
						game.setScreen(new MainMenuScreen(game));
					}
				}
				return false;
			}
		});
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
		resetGameState();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {

		bucketImage.dispose();
		dropSound.dispose();
		// rainMusic.dispose();
		bonusSound.dispose();
		explodeSound.dispose();
		powerupSound.dispose();
		cloud.dispose();
		cloud_2.dispose();
		monkeyAtlas.dispose();
		menuSelectSound.dispose();

		resetGameState();
	}

	private void resetGameState() {
		GAME_OVER = false;
		GAME_GETREADY = true;
	}

}
