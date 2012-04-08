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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import menion.android.whereyougo.geoData.Waypoint;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.gui.extension.CustomMain;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.IconedListAdapter;
import menion.android.whereyougo.gui.extension.UtilsGUI;
import menion.android.whereyougo.gui.location.SatelliteScreen;
import menion.android.whereyougo.guiding.GuidingScreen;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.settings.UtilsSettings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.formats.CartridgeFile;

public class Main extends CustomMain {

	private static final String TAG = "Main";
	
	public static WUI wui = new WUI();
	public static WLocationService wLocationService = new WLocationService();
	public static CartridgeFile cartridgeFile;

	public static String selectedFile;
	
	private static Vector<CartridgeFile> cartridgeFiles;
	
	@Override
	protected void eventFirstInit() {
    	// call after start actions here
        MainAfterStart.afterStartAction();
    }
	
	@Override
	protected void eventSecondInit() {
	}

	
	@Override
	protected void eventCreateLayout() {
		setContentView(R.layout.layout_main);

		// set title
		((TextView) findViewById(R.id.title_text)).setText(APP_NAME);
		
		// define buttons
		View.OnClickListener mOnClickListener = new View.OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button_start:
					if (cartridgeFiles != null && cartridgeFiles.size() != 0) {
						try {
							// sort cartridges
							final Location actLoc = LocationState.getLocation();
							final Location loc1 = new Location(TAG);
							final Location loc2 = new Location(TAG);
							Collections.sort(cartridgeFiles, new Comparator<CartridgeFile>() {
								public int compare(
										CartridgeFile object1,
										CartridgeFile object2) {
									loc1.setLatitude(object1.latitude);
									loc1.setLongitude(object1.longitude);
									loc2.setLatitude(object2.latitude);
									loc2.setLongitude(object2.longitude);
									return (int) (actLoc.distanceTo(loc1) - actLoc.distanceTo(loc2));
								}
							});
							
							ArrayList<DataInfo> data = new ArrayList<DataInfo>();
							for (int i = 0; i < cartridgeFiles.size(); i++) {
								CartridgeFile file = cartridgeFiles.get(i);
								byte[] iconData = file.getFile(file.iconId);
								Bitmap icon;
								try {
									icon = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
								} catch (Exception e) {
									icon = Images.getImageB(R.drawable.icon_gc_wherigo);
								}
								
								DataInfo di = new DataInfo(file.name, file.type +
										", " + file.author + ", " + file.version, icon);
								di.value01 = file.latitude;
								di.value02 = file.longitude;
								di.setDistAzi(actLoc);
								data.add(di);
							}

							IconedListAdapter adapter = new IconedListAdapter(A.getMain(), data, null);
							adapter.setTextView02Visible(View.VISIBLE, false);
							
							// create listView
							ListView lv = UtilsGUI.createListView(Main.this, false, data);
							
							// consturct dialog
							final CustomDialog dialog = new CustomDialog.Builder(Main.this, true).
									setTitle(R.string.choose_cartridge).
									setTitleExtraCancel().
									setContentView(lv, false).create();
							
							// set click listener
							lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			    				@Override
			    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									try {
										Main.cartridgeFile = cartridgeFiles.get(position);
										Main.selectedFile = Main.cartridgeFile.filename;
									
										if (Main.cartridgeFile.getSavegame().exists()) {
											UtilsGUI.showDialogQuestion(Main.this,
													R.string.resume_previous_cartridge,
													new CustomDialog.OnClickListener() {
														@Override
														public boolean onClick(CustomDialog dialog, View v, int btn) {
															File file = new File(selectedFile.substring(0, selectedFile.length() - 3) + "gwl");
															FileOutputStream fos = null;
															try {
																if (!file.exists())
																	file.createNewFile();
																fos = new FileOutputStream(file);
															} catch (Exception e) {
																Logger.e(TAG, "onResume() - create empty saveGame file", e);
															}
															Main.restoreCartridge(fos);
															return true;
														}
													}, new CustomDialog.OnClickListener() {
														@Override
														public boolean onClick(CustomDialog dialog, View v, int btn) {
															Main.wui.showScreen(WUI.SCREEN_CART_DETAIL, null);
															try {
																Main.getSaveFile().delete();
															} catch (Exception e) {
																Logger.e(TAG, "onCreate() - deleteSyncFile", e);
															}
															return true;
														}
													});
										} else {
											Main.wui.showScreen(WUI.SCREEN_CART_DETAIL, null);
										}
									} catch (Exception e) {
										Logger.e(TAG, "onCreate()", e);
									}
									dialog.dismiss();
			    				}
							});
							
							// show dialog
							dialog.show();
						} catch (Exception e) {
							Logger.e(TAG, "button_start_click", e);
						}
					} else {
						UtilsGUI.showDialogInfo(Main.this, 
								getString(R.string.no_wherigo_cartridge_available,
										FileSystem.ROOT, CustomMain.APP_NAME));
					}
					break;
				case R.id.button_gps:
					Intent intent02 = new Intent(Main.this, SatelliteScreen.class);
					startActivity(intent02);
					break;
				case R.id.button_settings:
					UtilsSettings.showSettings(Main.this);
					break;
				case R.id.button_map:
					ManagerNotify.toastLongMessage("Not implemented, simple solution is to use MapsForge library or Google Lib");
					break;
				case R.id.button_logo:
					showDialog(DIALOG_ABOUT);
					break;
				}
			}
		};
		
		View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				switch (v.getId()) {
				case R.id.button_start:
					break;
				case R.id.button_map:
					break;
				case R.id.button_gps:
					break;
				case R.id.button_settings:
					break;
				case R.id.button_logo:
					break;
				}
				return true;
			}
		};
		
		UtilsGUI.setButtons(this, new int[] {
				R.id.button_start, R.id.button_map, R.id.button_gps, R.id.button_settings,
				R.id.button_logo}, mOnClickListener, mOnLongClickListener);
	}
	
	@Override
	protected void eventDestroyApp() {
	}
	
    public void onResume() {
    	super.onResume();
    	refreshCartridge();
    }
    
	private static final int DIALOG_ABOUT = 0;
	
	protected Dialog onCreateDialog(int id) {
	    final Dialog dialog;
	    switch(id) {
	    case DIALOG_ABOUT:
			StringBuffer buffer = new StringBuffer();
			buffer.append("<div align=\"center\"><h2><b>WhereYouGo</b></h2></div>");
			buffer.append("<div>");
			buffer.append("<b>Wherigo player for Android device</b><br /><br />");
			try {
				buffer.append(Loc.get(R.string.version) + "<br />&nbsp;&nbsp;<b>" + 
						getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "</b><br /><br />");
			} catch (Exception e) {}
			buffer.append(getString(R.string.author) + "<br />&nbsp;&nbsp;<b>Menion Asamm</b><br /><br />");
			buffer.append(getString(R.string.web_page) + "<br />&nbsp;&nbsp;<b><a href=\"http://forum.asamm.cz\">http://forum.asamm.cz</a></b><br /><br />");
			buffer.append(getString(R.string.libraries));
			buffer.append("<br />&nbsp;&nbsp;<b>OpenWig</b>");
			buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;Matejicek");
			buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small>http://code.google.com/p/openwig</small>");
			buffer.append("<br />&nbsp;&nbsp;<b>Kahlua</b>");
			buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;Kristofer Karlsson");
			buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small>http://code.google.com/p/kahlua/</small>");
			buffer.append("</div>");
			
			// add news
			buffer.append(MainAfterStart.getNews(1, 
					Settings.getApplicationVersionActual()));
			
	    	WebView webView = new WebView(A.getMain());
			webView.loadData(buffer.toString(), "text/html", "utf-8");
			webView.setLayoutParams(new  ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			webView.setBackgroundColor(Color.WHITE);

	    	dialog = new CustomDialog.Builder(this, true).
	    	setTitle(R.string.about_application, R.drawable.ic_title_logo).
	    	setTitleExtraCancel().
	    	setContentView(webView, true).
	    	setNeutralButtonCancel(R.string.close).
	    	create();
	        break;
	    default:
	        dialog = null;
	    }
	    
	    if (dialog != null) {
	    	dialog.setCanceledOnTouchOutside(true);
	    }
	    
	    return dialog;
	}
    
    
	public static void loadCartridge(OutputStream log) {
		try {
			WUI.startProgressDialog();
			Engine.newInstance(cartridgeFile, log, wui, wLocationService).start();
		} catch (Throwable t) {}
	}

	public static void restoreCartridge(OutputStream log) {
		try {
			WUI.startProgressDialog();
			Engine.newInstance(cartridgeFile, log, wui, wLocationService).restore();
		} catch (Throwable t) {}
	}
	
	public static File getSaveFile() throws IOException {
		try {
			File file = new File(selectedFile.substring(0, selectedFile.length() - 3) + "ows");
			return file;
		} catch (SecurityException e) {
			Logger.e(TAG, "getSyncFile()", e);
			return null;
		}
	}
	
	public static void setBitmapToImageView(Bitmap i, ImageView iv) {
Logger.w(TAG, "setBitmapToImageView(), " + i.getWidth() + " x " + i.getHeight());
		float width = i.getWidth() - 10;
		float height = (Const.SCREEN_WIDTH / width) * i.getHeight();
	
		if ((height / Const.SCREEN_HEIGHT) > 0.60f) {
			height = 0.60f * Const.SCREEN_HEIGHT;
			width = (height / i.getHeight()) * i.getWidth();
		}
		iv.setMinimumWidth((int) width);
		iv.setMinimumHeight((int) height);
		iv.setImageBitmap(i);
	}

	private void refreshCartridge() {
Logger.w(TAG, "refreshCartridge(), " + (Main.selectedFile == null));
		if (Main.selectedFile != null)
			return;
		
        // load cartridge files
		File[] files = FileSystem.getFiles(FileSystem.ROOT, "gwc");
		cartridgeFiles = new Vector<CartridgeFile>();
        
        // add cartridges to map
		ArrayList<Waypoint> wpts = new ArrayList<Waypoint>();
        
        File actualFile = null;
       	if (files != null) {
       		for (File file : files) {
       	        try {
        			actualFile = file;
        			CartridgeFile cart = CartridgeFile.read(new WSeekableFile(file), new WSaveFile(file));
        			if (cart != null) {
        				cart.filename = file.getAbsolutePath();
        				
        				Waypoint waypoint = new Waypoint(cart.name);
        				Location loc = new Location(TAG);
        				loc.setLatitude(cart.latitude);
        				loc.setLongitude(cart.longitude);
        				waypoint.setLocation(loc, true);
        				
        				cartridgeFiles.add(cart);
        				wpts.add(waypoint);
        			}
       	        } catch (Exception e) {
       	        	Logger.w(TAG, "refreshCartridge(), file:" + actualFile + ", e:" + e.toString());
       	        	ManagerNotify.toastShortMessage(Loc.get(R.string.invalid_cartridge, file.getName()));
       	        }
       	    }
       	}

       	if (wpts.size() > 0) {
    		// TODO add items on map
    	}
	}
	
	/**
	 * Call activity that guide onto point.
	 * @param activity
	 * @return true if internal activity was called. False if external by intent.
	 */
	public static boolean callGudingScreen(Activity activity) {
		Intent intent = new Intent(activity, GuidingScreen.class);
		activity.startActivity(intent);
		return true;
	}

	@Override
	protected int getCloseValue() {
		return CLOSE_DESTROY_APP_NO_DIALOG;
	}

	@Override
	protected String getCloseAdditionalText() {
		return null;
	}

	@Override
	protected void eventRegisterOnly() {
	}
}