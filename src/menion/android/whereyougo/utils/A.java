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

package menion.android.whereyougo.utils;

import menion.android.whereyougo.gui.extension.CustomMain;
import menion.android.whereyougo.gui.extension.MainApplication;
import menion.android.whereyougo.guiding.GuidingContent;
import menion.android.whereyougo.hardware.sensors.Orientation;
import menion.android.whereyougo.utils.audio.ManagerAudio;
import android.app.Application;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class A {

	private static String TAG = "A";

	private static MainApplication app;
	protected static CustomMain main;
	private static GuidingContent guidingContent;
	private static ManagerAudio managerAudio;
	private static Orientation rotator;
	
	public static void printState() {
		Logger.i(TAG, "printState() - STATIC VARIABLES");
		Logger.i(TAG, "app:" + app);
		Logger.i(TAG, "managerAudio:" + managerAudio);
		Logger.i(TAG, "main:" + main);
		Logger.i(TAG, "guidingContent:" + guidingContent);
		Logger.i(TAG, "rotator:" + rotator);
	}
	
	public static void destroy() {
		guidingContent = null;			
		managerAudio = null;
		main = null;
		if (rotator != null) {
			rotator.removeAllListeners();
			rotator = null;
		}
		// finally destroy app
		if (app != null)
			app.destroy();
		app = null;
	}
	
	public static void registerApp(MainApplication app) {
		A.app = app;
	}
	
	public static void registerMain(CustomMain main) {
		A.main = main;
	}
	
	public static Application getApp() {
		return app;
	}
	
	public static CustomMain getMain() {
		return main;
	}
	
	public static ManagerAudio getManagerAudio() {
		if (managerAudio == null) {
			managerAudio = new ManagerAudio();
		}
		return managerAudio;
	}
	
	public static GuidingContent getGuidingContent() {
		if (guidingContent == null) {
			guidingContent = new GuidingContent();
		}
		return guidingContent;
	}
	
	public static Orientation getRotator() {
		if (rotator == null) {
			rotator = new Orientation();
		}
		return rotator;
	}
}
