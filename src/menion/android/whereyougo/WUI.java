/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo;

import java.io.ByteArrayInputStream;

import menion.android.whereyougo.gui.CartridgeDetails;
import menion.android.whereyougo.gui.CartridgeMainMenu;
import menion.android.whereyougo.gui.Details;
import menion.android.whereyougo.gui.InputScreen;
import menion.android.whereyougo.gui.ListActions;
import menion.android.whereyougo.gui.ListTargets;
import menion.android.whereyougo.gui.ListTasks;
import menion.android.whereyougo.gui.ListThings;
import menion.android.whereyougo.gui.ListZones;
import menion.android.whereyougo.gui.PushDialog;
import menion.android.whereyougo.gui.Refreshable;
import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.gui.extension.UtilsGUI;
import menion.android.whereyougo.guiding.GuidingScreen;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import se.krka.kahlua.vm.LuaClosure;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Media;
import cz.matejcik.openwig.platform.UI;

public class WUI implements UI {

	private static final String TAG = "WUI";
	
	public static final int SCREEN_MAIN = 10;
	public static final int SCREEN_CART_DETAIL = 11;
	public static final int SCREEN_ACTIONS = 12;
	public static final int SCREEN_TARGETS = 13;
	
	public static boolean saving = false;
	
	public void blockForSaving() {
		Logger.w(TAG, "blockForSaving()");
		saving = true;
	}
	
	public void unblock() {
		Logger.w(TAG, "unblock()");
		saving = false;
	}

	public void debugMsg(String msg) {
		Logger.w(TAG, "debugMsg(" + msg.trim() + ")");
	}

	public void playSound(byte[] data, String mime) {
Logger.e(TAG, "playSound(" + (data != null ? data.length : 0) + ", "+ mime + ")");
		// test on wrong data
		if (data == null || data.length == 0 || mime == null)
			return;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if ("audio/x-wav".equals(mime)) {
				A.getManagerAudio().playMp3File("audio", ".wav", bis);
			} else if ("audio/mpeg".equals(mime)) {
				A.getManagerAudio().playMp3File("audio", ".mp3", bis);
			} 
		} catch (Exception e) {
			Logger.e(TAG, "play(), cart:" + Main.cartridgeFile.code, e);
		}
	}
	
	public void showError(String msg) {
		Logger.w(TAG, "showError(" + msg.trim() + ")");
		UtilsGUI.showDialogError(Settings.getCurrentActivity(), msg);
	}

	public void pushDialog(String[] texts, Media[] media, String button1,
			String button2, LuaClosure callback) {
Logger.w(TAG, "pushDialog(" + texts + ", " + media + ", " + button1 + ", " + button2 + ", " + callback + ")");

		Activity activity = getParentActivity();
		PushDialog.setDialog(texts, media, button1, button2, callback);
		Intent intent = new Intent(activity, PushDialog.class);
		activity.startActivity(intent);
		closeActivity(activity);
					
		Vibrator v = (Vibrator) A.getMain().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(25);		
	}

	public void pushInput(EventTable input) {
Logger.w(TAG, "pushInput(" + input + ")");
		Activity activity = getParentActivity();
		InputScreen.setInput(input);
		Intent intent = new Intent(activity, InputScreen.class);
		activity.startActivity(intent);
		closeActivity(activity);
	}

	public void refresh() {
Logger.w(TAG, "refresh(), currentActivity:" + Settings.getCurrentActivity());
		if (Settings.getCurrentActivity() != null && Settings.getCurrentActivity() instanceof Refreshable) {
			((Refreshable) Settings.getCurrentActivity()).refresh();
		}
	}

	public void setStatusText(final String text) {
Logger.w(TAG, "setStatus(" + text + ")");
		if (text == null || text.length() == 0)
			return;
		
		try {
			final CustomActivity activity = getParentActivity();
			activity.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();	
				}
			});
		} catch (Exception e) {
			Logger.e(TAG, "setStatusText(" + text + ")", e);
		}	
	}

	public void showScreen(int screenId, EventTable details) {
		Activity activity = getParentActivity();
Logger.w(TAG, "showScreen(" + screenId + "), parent:" + activity + ", param:" + details);
				
		// disable currentActivity
		Settings.setCurrentActivity(null);
		
		switch (screenId) {
			case MAINSCREEN:
				Intent intent01 = new Intent(activity, CartridgeMainMenu.class);
				activity.startActivity(intent01);
				return;
			case SCREEN_CART_DETAIL:
				Intent intent02 = new Intent(activity, CartridgeDetails.class);
				activity.startActivity(intent02);
				return;
			case DETAILSCREEN:
				Details.et = details;
				Intent intent03 = new Intent(activity, Details.class);
				activity.startActivity(intent03);
				return;
			case INVENTORYSCREEN:
				Intent intent04 = new Intent(activity, ListThings.class);
				intent04.putExtra("title", "Inventory");
				intent04.putExtra("mode", ListThings.INVENTORY);
				activity.startActivity(intent04);
				return;
			case ITEMSCREEN:
				Intent intent05 = new Intent(activity, ListThings.class);
				intent05.putExtra("title", "You see");
				intent05.putExtra("mode", ListThings.SURROUNDINGS);
				activity.startActivity(intent05);
				return;
			case LOCATIONSCREEN:
				Intent intent06 = new Intent(activity, ListZones.class);
				intent06.putExtra("title", "Locations");
				activity.startActivity(intent06);
				return;
			case TASKSCREEN:
				Intent intent07 = new Intent(activity, ListTasks.class);
				intent07.putExtra("title", "Tasks");
				activity.startActivity(intent07);
				return;
			case SCREEN_ACTIONS:
				Intent intent09 = new Intent(activity, ListActions.class);
				if (details != null)
					intent09.putExtra("title", details.name);
				activity.startActivity(intent09);
				return;
			case SCREEN_TARGETS:
				Intent intent10 = new Intent(activity, ListTargets.class);
				if (details != null)
					intent10.putExtra("title", details.name);
				activity.startActivity(intent10);
				return;
		}
		
		closeActivity(activity);
	}

    private static ProgressDialog progressDialog;
    
    public static void showTextProgress(final String text) {
    	Logger.i(TAG, "showTextProgress(" + text + ")");
    }
    
    public static void startProgressDialog() {
    	progressDialog = new ProgressDialog(((CustomActivity) A.getMain()));
		progressDialog.setMessage("Loading...");
		progressDialog.show();
    }
    
	public void start() {
    	((CustomActivity) A.getMain()).runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog != null)
					progressDialog.dismiss();				
			}
    	});
    	showScreen(MAINSCREEN, null);
	}
	
	public void end() {
		if (progressDialog != null)
			progressDialog.dismiss();
		Engine.kill();
		showScreen(SCREEN_MAIN, null);
	}
	
	private static CustomActivity getParentActivity() {
		CustomActivity activity = (CustomActivity) Settings.getCurrentActivity();

		if (activity == null)
			activity = (CustomActivity) A.getMain();
		
		return activity;
	}
	
	private static void closeActivity(Activity activity) {
		if (activity instanceof PushDialog ||
				activity instanceof GuidingScreen) {
			activity.finish();
		}
	}

	@Override
	public String getDeviceId() {
		String appVersion = "";
		try {
			appVersion = A.getMain().getPackageManager().getPackageInfo(A.getMain().getPackageName(), 0).versionName;
		} catch (Exception e) {}
		return "WhereYouGo, app:" + appVersion;
	}

}
