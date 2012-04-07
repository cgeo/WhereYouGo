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

package menion.android.whereyougo.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.IconedListAdapter;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import menion.android.whereyougo.utils.UtilsFormat;
import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cz.matejcik.openwig.Action;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Media;
import cz.matejcik.openwig.Thing;
import cz.matejcik.openwig.Zone;

public abstract class ListVarious extends CustomActivity implements Refreshable {

	private static final String TAG = "ListVarious";
	
	private ListView lv;
	protected String title;
		
	private Vector<Object> stuff = new Vector<Object>();
	
	abstract protected void callStuff (Object what);
	abstract protected boolean stillValid ();
	abstract protected Vector<Object> getValidStuff();
	abstract protected String getStuffName (Object what);
	
	protected Bitmap getStuffIcon (Object object) {
		if (((EventTable) object).isLocated())
			return getLocatedIcon((EventTable) object);
		else {
			Media media = (Media) ((EventTable) object).table.rawget("Icon");
			if (media != null) {
				byte[] icon;
				try {
					icon = Engine.mediaFile(media);
				} catch (IOException e) {
					Logger.e(TAG, "getStuffIcon()", e);
					return Images.IMAGE_EMPTY_B;
				}
				return BitmapFactory.decodeByteArray(icon, 0, icon.length);
			} else { 
				return Images.IMAGE_EMPTY_B;
			}
		}
	}
	
	private static Paint paintText;
	private static Paint paintArrow;
	private static Paint paintArrowBorder;
	static {
		paintText = new Paint();
		paintText.setColor(Color.RED);
		paintText.setTextSize(16.0f);
		paintText.setTypeface(Typeface.DEFAULT_BOLD);
		paintText.setAntiAlias(true);
		
		paintArrow = new Paint();
		paintArrow.setColor(Color.YELLOW);
		paintArrow.setAntiAlias(true);
		paintArrow.setStyle(Style.FILL);
		
		paintArrowBorder = new Paint();
		paintArrowBorder.setColor(Color.BLACK);
		paintArrowBorder.setAntiAlias(true);
		paintArrowBorder.setStyle(Style.STROKE);
	}
	
	protected Bitmap getLocatedIcon(EventTable thing) {
		if (!thing.isLocated())
			return Images.IMAGE_EMPTY_B;
		
		try {
			Bitmap bitmap = Bitmap.createBitmap(96, 48, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			c.drawColor(Color.TRANSPARENT);
			
			Location nearest = new Location(TAG);
			if (thing instanceof Zone) {
				nearest.setLatitude(((Zone) thing).nearestPoint.latitude);
				nearest.setLongitude(((Zone) thing).nearestPoint.longitude);
			} else {
				nearest.setLatitude(thing.position.latitude);
				nearest.setLongitude(thing.position.longitude);
			}
			
			float azimuth = LocationState.getLocation().bearingTo(nearest);
			float distance = LocationState.getLocation().distanceTo(nearest);
			
	        double a;
	        int radius = bitmap.getHeight() / 2;
	        int cX = radius;
	        int cY = bitmap.getHeight() / 2;
	        float x1, x2, x3, x4, y1, y2, y3, y4;
	
	        a = azimuth / Const.RHO;
	        x1 = (float) (Math.sin(a) * (radius * 0.90));
	        y1 = (float) (Math.cos(a) * (radius * 0.90));
	
	        a = (azimuth + 180) / Const.RHO;
	        x2 = (float) (Math.sin(a) * (radius * 0.2));
	        y2 = (float) (Math.cos(a) * (radius * 0.2));
	
	        a = (azimuth + 140) / Const.RHO;
	        x3 = (float) (Math.sin(a) * (radius * 0.6));
	        y3 = (float) (Math.cos(a) * (radius * 0.6));
	
	        a = (azimuth + 220) / Const.RHO;
	        x4 = (float) (Math.sin(a) * (radius * 0.6));
	        y4 = (float) (Math.cos(a) * (radius * 0.6));
	
	        Path path = new Path();
	        path.moveTo(cX + x1, cY - y1);
	        path.lineTo(cX + x2, cY - y2);
	        path.lineTo(cX + x3, cY - y3);
	        c.drawPath(path, paintArrow);
	        
	        path = new Path();
	        path.moveTo(cX + x1, cY - y1);
	        path.lineTo(cX + x2, cY - y2);
	        path.lineTo(cX + x4, cY - y4);
	        c.drawPath(path, paintArrow);

	        c.drawLine(cX + x1, cY - y1, cX + x3, cY - y3, paintArrowBorder);
	        c.drawLine(cX + x1, cY - y1, cX + x4, cY - y4, paintArrowBorder);
	        c.drawLine(cX + x2, cY - y2, cX + x3, cY - y3, paintArrowBorder);
	        c.drawLine(cX + x2, cY - y2, cX + x4, cY - y4, paintArrowBorder);
	        
			c.drawText(UtilsFormat.formatDistance(distance, false), radius * 2 + 2, cY + paintText.getTextSize() / 2, paintText);
			return bitmap;
		} catch (Exception e) {
			Logger.e(TAG, "getLocatedIcon(" + thing + ")", e);
			return Images.IMAGE_EMPTY_B;
		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RelativeLayout rl = new RelativeLayout(ListVarious.this);
		rl.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// top textView title
		TextView tvTitle = new TextView(ListVarious.this);
		if (getIntent().getStringExtra("title") != null)
			title = getIntent().getStringExtra("title");
		tvTitle.setText(title);
		tvTitle.setBackgroundResource(R.drawable.title_bar);
		tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
		tvTitle.setTextSize(18.0f);
		tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
		tvTitle.setId(1);
		RelativeLayout.LayoutParams rlLp01 = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        rlLp01.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rl.addView(tvTitle, rlLp01);
        
    	// main bottom panel
        LinearLayout llBottom = new LinearLayout(ListVarious.this);
        llBottom.setOrientation(LinearLayout.HORIZONTAL);
        llBottom.setBackgroundResource(R.drawable.bottom_bar);
        int space = (int) Utils.getDpPixels(3);
        llBottom.setPadding(space, space, space, space);
        llBottom.setId(2);

        // center linearLayout
		lv = new ListView(ListVarious.this);
		RelativeLayout.LayoutParams rlLp03 = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rlLp03.setMargins((int) Utils.getDpPixels(5), (int) Utils.getDpPixels(5),
				(int) Utils.getDpPixels(5), (int) Utils.getDpPixels(5)); 
        rlLp03.addRule(RelativeLayout.BELOW, tvTitle.getId());
        rlLp03.addRule(RelativeLayout.ABOVE, llBottom.getId());
		rl.addView(lv, rlLp03);
        
		setContentView(rl);
	}
	
	public void onResume() {
		super.onResume();
		refresh();
	}
	
	public void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (!stillValid()) {
					ListVarious.this.finish();
					return;
				}
				
				Vector<Object> newStuff = getValidStuff();
				int scrollY = lv.getFirstVisiblePosition();
				// first, validate the stuff already in there
				// TODO
//				for (int i = 0; i < stuff.size(); i++) {
//					Object s = stuff.get(i);
//					int in = newStuff.indexOf(s);
//					if (in == -1) {
//						stuff.remove(i);
//						i--;
//					} else {
//						newStuff.setElementAt(null, in);
//					}
//				}
				// then, add the rest
				stuff.clear();
				for (int i = 0; i < newStuff.size(); i++) {
					Object s = newStuff.get(i);
					if (s != null) {
						stuff.add(s);
					}
				}
				
				// create visual part
				ArrayList<DataInfo> data = new ArrayList<DataInfo>();
				for (int i = 0; i < stuff.size(); i++) {
					Object s = stuff.get(i);
					DataInfo dataInfo = new DataInfo("");
//Logger.e("ListVarious", "addToList:" + s + ", " + (s instanceof Action) + ", " + (s instanceof Cartridge) + ", " + (s instanceof Container) + ", " + (s instanceof Thing));
					if (s instanceof Thing) {
						dataInfo = new DataInfo(((Thing) s).name, null, getStuffIcon(s));
					} else if (s instanceof Action) {
						dataInfo = new DataInfo(((Action) s).text, null, getStuffIcon(s));
					} else {
						dataInfo = new DataInfo(s.toString(), null, getStuffIcon(s));
					}
					data.add(dataInfo);
				}
				
				IconedListAdapter adapter = new IconedListAdapter(ListVarious.this, data, lv);
				adapter.setTextView02Visible(View.VISIBLE, true);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Logger.d(TAG, "onItemClick:" + position);
						
						Object s = null;
						synchronized (this) {
							if (position >= 0 && position < stuff.size()) {
								s = stuff.get(position);
							}
						}
						if (s != null)
							callStuff(s);
					}
				});
				
				lv.setSelectionFromTop(scrollY, 5);
			}
		});
	}
}
