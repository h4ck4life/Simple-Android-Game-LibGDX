package com.filavents.pandaikira;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class MainActivity extends AndroidApplication implements
		GameHelperListener, ActionResolver {

	private GameHelper gameHelper;

	public MainActivity() {
		gameHelper = new GameHelper(this);
		gameHelper.enableDebugLog(true, "GPGS");
	}

	private boolean checkNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;

		initialize(new MainFruit(this), cfg);
		gameHelper.setup(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	@Override
	public boolean getSignedInGPGS() {
		// TODO Auto-generated method stub
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		// TODO Auto-generated method stub
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}

	}

	@Override
	public void submitScoreGPGS(int score) {
		// TODO Auto-generated method stub
		gameHelper.getGamesClient().submitScore("CgkIp-26x7gZEAIQAA", score);

	}

	@Override
	public void unlockAchievementGPGS(final String achievementId) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				gameHelper.getGamesClient().unlockAchievement(achievementId);
			}
		});
	}

	@Override
	public void getLeaderboardGPGS() {
		// TODO Auto-generated method stub
		startActivityForResult(gameHelper.getGamesClient()
				.getLeaderboardIntent("CgkIp-26x7gZEAIQAA"), 100);

	}

	@Override
	public void getAchievementsGPGS() {
		// TODO Auto-generated method stub
		startActivityForResult(gameHelper.getGamesClient()
				.getAchievementsIntent(), 101);

	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub

	}
}