package com.filavents.pandaikira;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MainMenuScreen implements Screen {

	final MainFruit game;

	OrthographicCamera camera;
	private Texture texture;
	Array<Vector2> playButton = new Array<Vector2>();
	Array<Vector2> scoreButton = new Array<Vector2>();
	Array<Vector2> muteButton = new Array<Vector2>();

	Sound menuSelectSound;
	float volume = 0.4f;

	Preferences prefs;

	public MainMenuScreen(final MainFruit gam) {
		game = gam;

		prefs = Gdx.app.getPreferences("fruitapeprefs");

		Texture.setEnforcePotImages(false);

		texture = new Texture(Gdx.files.internal("home_screen_new.png"));
		menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
		// Texture.setEnforcePotImages(false);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(texture, 0, 0);
		game.batch.end();

		if (Gdx.input.justTouched()) {

			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);

			playButton.add(new Vector2(400, 130));
			playButton.add(new Vector2(480, 120));
			playButton.add(new Vector2(480, 0));
			playButton.add(new Vector2(400, 0));
			playButton.add(new Vector2(400, 130));

			if (Intersector.isPointInPolygon(playButton, new Vector2(
					touchPos.x, touchPos.y)) == true) {
				menuSelectSound.play(volume);
				game.setScreen(new GameScreen(game));
				dispose();
			}

			scoreButton.add(new Vector2(535, 110));
			scoreButton.add(new Vector2(615, 110));
			scoreButton.add(new Vector2(615, 10));
			scoreButton.add(new Vector2(535, 10));
			scoreButton.add(new Vector2(535, 110));

			if (Intersector.isPointInPolygon(scoreButton, new Vector2(
					touchPos.x, touchPos.y)) == true) {
				menuSelectSound.play(volume);
				if (game.actionResolver.getSignedInGPGS()) {
					game.actionResolver.getLeaderboardGPGS();
				} else {
					game.actionResolver.loginGPGS();
				}
			}

			muteButton.add(new Vector2(635, 110));
			muteButton.add(new Vector2(715, 110));
			muteButton.add(new Vector2(715, 10));
			muteButton.add(new Vector2(635, 10));
			muteButton.add(new Vector2(655, 110));

			if (Intersector.isPointInPolygon(muteButton, new Vector2(
					touchPos.x, touchPos.y)) == true) {
				// System.out.println("Mute button touch");
				menuSelectSound.play(volume);
				game.actionResolver.getAchievementsGPGS();
			}

		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		
		//System.out.println(prefs.getInteger("highscore", 0));

		game.actionResolver.submitScoreGPGS(prefs.getInteger("highscore", 0));

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyUp(final int keycode) {
				if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {

					Gdx.app.exit();
				}
				return false;
			}
		});
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		texture.dispose();
		menuSelectSound.dispose();
	}

}
