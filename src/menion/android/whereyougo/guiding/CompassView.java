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

package menion.android.whereyougo.guiding;

import menion.android.whereyougo.R;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Utils;
import menion.android.whereyougo.utils.UtilsFormat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class CompassView extends View {

//	private static final String TAG = "CompassView";

    private float mAzimuth;
    private float mPitch;
    private float mRoll;
    
    /* azimuth for target arrow */
    private float mAzimuthToTarget;
    /* distance to target */
    private double mDistanceToTarget;
    
    private float cX1, cY1, cX2, cX3, cY2, cY3;
    private float r1, r23;

    private Paint mPaintBitmap;
    
    private Paint paintValueLabel;
    private Paint paintValueDistance;
    private Paint paintValueAzimuth;
    private Paint paintValueTilt;
    private Paint paintTiltBorder;
    private Paint paintTiltBorder1;
    private Paint paintTiltBorder2;
    private Paint paintTiltBg;

    private Drawable bitCompassBg;
    private Drawable bitCompassArrow;
    
    public CompassView(Context context, AttributeSet attr) {
    	super(context, attr);
    	initialize();
    }
    
	public CompassView(Context context) {
		super(context);
		initialize();
	}
	
	private void initialize() {
		mAzimuth = 0.0f;
        mAzimuthToTarget = 0.0f;
        
    	// load images
    	bitCompassBg = Images.getImageD(R.drawable.var_compass);
    	bitCompassArrow = Images.getImageD(R.drawable.var_compass_arrow);

    	// set paint methods
        mPaintBitmap = new Paint();
        mPaintBitmap.setAntiAlias(true);
        mPaintBitmap.setFilterBitmap(true);
        
        paintValueLabel = new Paint();
        paintValueLabel.setAntiAlias(true);
        paintValueLabel.setTextAlign(Align.CENTER);
        paintValueLabel.setColor(Color.WHITE);
        paintValueLabel.setTextSize(Utils.getDpPixels(12.0f));
        
        paintValueDistance = new Paint(paintValueLabel);
        paintValueAzimuth = new Paint(paintValueDistance);
        
        paintValueTilt = new Paint(paintValueDistance);
        paintValueTilt.setColor(Color.parseColor("#00a2e6"));
        paintValueTilt.setTypeface(Typeface.DEFAULT_BOLD);
        paintValueTilt.setShadowLayer(Utils.getDpPixels(3), 0, 0, Color.BLACK);
        
        paintTiltBorder = new Paint();
		paintTiltBorder.setAntiAlias(true);
		paintTiltBorder.setColor(Color.DKGRAY);
		paintTiltBorder.setStyle(Style.STROKE);
		paintTiltBorder.setStrokeWidth(3.0f);
		
		paintTiltBorder1 = new Paint();
		paintTiltBorder1.setAntiAlias(true);
		paintTiltBorder1.setColor(Color.BLACK);
		paintTiltBorder1.setStyle(Style.STROKE);
		paintTiltBorder1.setStrokeWidth(Utils.getDpPixels(3.0f));
		
		paintTiltBorder2 = new Paint();
		paintTiltBorder2.setAntiAlias(true);
		paintTiltBorder2.setColor(Color.WHITE);
		paintTiltBorder2.setStyle(Style.STROKE);
		paintTiltBorder2.setStrokeWidth(Utils.getDpPixels(2.0f));
		
		paintTiltBg = new Paint();
		paintTiltBg.setAntiAlias(true);
		paintTiltBg.setColor(Color.RED);
		paintTiltBg.setStyle(Style.FILL);
		paintTiltBg.setShadowLayer(3, 3, 3, Color.BLACK);
	}
	
	private int lastWidth;
	
	private void setConstants(Canvas c) {
		if (lastWidth == c.getWidth())
			return;
		
		lastWidth = c.getWidth();
		
    	// set basic constants
    	int w = c.getClipBounds().width();
    	int h = c.getClipBounds().height();
    	
    	float neededHeight = Math.min(w, h);
    	r1 = neededHeight / 2 * 0.90f;

    	// draw background
       	paintTiltBg.setShader(new LinearGradient(
       			0, 0, 0, c.getClipBounds().height(),
       			new int[] {Color.rgb(200, 200, 200), Color.rgb(100, 100, 100), Color.BLACK},
       			new float[] {0.0f, 0.2f, 1.0f},
       			TileMode.CLAMP));
       	
       	
		float sizeBelow = h -  neededHeight;
    	float maxWidth = w / 2.0f;
    	float space = 0;
    	
    	if (sizeBelow > maxWidth) { // enough space below main compass
    		space = (h - neededHeight - maxWidth) / 3.0f;	
    		cX1 = w / 2.0f;
    		cY1 = space + neededHeight / 2.0f;
    	} else { 
    		cX1 = w / 2.0f;
    		cY1 = neededHeight / 2.0f;
    	}

       	double val1 = cX1;
       	double val2 = h - cY1;
       	// distance from center to bottom corner 
       	double val3 = Math.sqrt(val1 * val1 + val2 * val2);
       	// center angle in RAD
       	double ang1 = Math.atan(val1 / val2);
       	// distance from bottom corner to center of small circle, 0.9 is reduction due to compass image border
       	double val4 = (val3 - (r1 * 0.9)) / 2.0;
       	// distance from small circle center to bottom and left
       	double val5 = Math.cos(ang1) * val4;
       	double val6 = Math.sin(ang1) * val4;
       	
       	r23 = (float) (Math.min(val5, val6) * 0.90f);
       	cX2 = (float) val5;
       	cY2 = (float) (h - val6 - space);
       	cX3 = w - cX2;
       	cY3 = cY2;
       	
    	// center distance text
       	paintValueDistance.setTextSize(r1 / 5);
       	paintValueAzimuth.setTextSize(r1 / 6);
       	paintValueTilt.setTextSize(r1 / 8);
	}
	
    public void draw(Canvas c) {
    	// init basic values
    	setConstants(c);

    	// draw background
    	c.save();
    	c.translate(cX1, cY1);
    	c.rotate(-mAzimuth);
    	bitCompassBg.setBounds((int) (-r1), (int) (-r1),  (int) (+r1), (int) (+r1));
    	bitCompassBg.draw(c);
    	c.restore();
    	
    	if (A.getGuidingContent().isGuiding()) {
        	c.save();
        	c.translate(cX1, cY1);
        	c.rotate(mAzimuthToTarget - mAzimuth);
        	bitCompassArrow.setBounds((int) (-r1), (int) (-r1),  (int) (+r1), (int) (+r1));
        	bitCompassArrow.draw(c);
        	c.restore();
    	}

    	// draw compass texts
        drawCompassTexts(c);
        // draw tilt rounds
        drawTilt(c);
    }
	
    private void drawCompassTexts(Canvas c) {
   		float space = r1 / 20;
   		c.drawText(Loc.get(R.string.distance),
   				cX1, cY1 - paintValueDistance.getTextSize() - space, paintValueLabel);
   		c.drawText(UtilsFormat.formatDistance(mDistanceToTarget, false),
   				cX1, cY1 - space, paintValueDistance);
    		
   		c.drawText(Loc.get(R.string.azimuth),
   				cX1, cY1 + paintValueLabel.getTextSize() + space, paintValueLabel);
   		c.drawText(UtilsFormat.formatAngle(mAzimuth),
   				cX1, cY1 + paintValueLabel.getTextSize() + 
   				paintValueAzimuth.getTextSize() + space, paintValueAzimuth);
    }
    
    private Path mPath = new Path();
    
    private void drawTilt(Canvas c) {
    	if (r23 > 0) {
    		// draw mPitch
            c.save();
            c.translate(cX2 - r23, cY2 - r23);
            mPath.reset();
            c.clipPath(mPath); // makes the clip empty
            mPath.addCircle(r23, r23, r23, Path.Direction.CCW);
            c.clipPath(mPath, Region.Op.REPLACE);

            c.clipRect(0, 0, r23 * 2, r23 * 2);
            if (mPitch > 90) {
            	mPitch = 180 - mPitch;
            } else if (mPitch < -90) {
            	mPitch = -180 - mPitch;
            }
            
        	float startY = r23 - (r23 / 90.0f) * mPitch;
        	float endY = r23 * 2.0f;

            c.drawRect(0, startY, 2 * r23, endY, paintTiltBg);
            c.drawLine(0, startY, 2 * r23, startY, paintTiltBorder);
            c.restore();
            
            // draw mRoll
            c.save();
            c.translate(cX3 - r23, cY3 - r23);
            mPath.reset();
            c.clipPath(mPath); // makes the clip empty
            mPath.addCircle(r23, r23, r23, Path.Direction.CCW);
            c.clipPath(mPath, Region.Op.REPLACE);

            c.clipRect(0, 0, r23 * 2, r23 * 2);
            if (mRoll > 90) {
            	mRoll = 180 - mRoll;
            } else if (mPitch < -90) {
            	mRoll = -180 - mRoll;
            }
            
            c.rotate(mRoll, r23, r23);
            c.drawRect(0, r23, 2 * r23, 2 * r23, paintTiltBg);
            c.drawLine(0, r23, 2 * r23, r23, paintTiltBorder);
            c.restore();
            
    		// draw border
    		c.drawCircle(cX2, cY2, r23, paintTiltBorder1);
    		c.drawCircle(cX2, cY2, r23, paintTiltBorder2);
    		c.drawCircle(cX3, cY3, r23, paintTiltBorder1);
    		c.drawCircle(cX3, cY3, r23, paintTiltBorder2);
    		
    		// draw texts
    		c.drawText(Loc.get(R.string.pitch), cX2 + r23,
    				cY2 - r23 - Utils.getDpPixels(2.0f), paintValueLabel);
    		c.drawText(Loc.get(R.string.roll), cX3 - r23,
    				cY3 - r23 - Utils.getDpPixels(2.0f), paintValueLabel);
            // draw pitch value
            c.drawText(UtilsFormat.formatAngle(mPitch), cX2, cY2 + paintValueTilt.getTextSize() / 2, paintValueTilt);
    		// draw roll value
            c.drawText(UtilsFormat.formatAngle(mRoll), cX3, cY3 + paintValueTilt.getTextSize() / 2, paintValueTilt);
    	}
    }
    
//    private void drawSun(Canvas c) {
//    	double sunAzimuthDiff = (SunPosition.getSunAzimuth() - mAzimuth) / Const.RHO;
//    	float x1 = (float) (Math.sin(sunAzimuthDiff) * (r1 - Utils.getTextSize(Const.TEXT_SIZE_MEDIUM)));
//        float y1 = (float) (Math.cos(sunAzimuthDiff) * (r1 - Utils.getTextSize(Const.TEXT_SIZE_MEDIUM)));
//        c.drawBitmap(bitmapSun, cX1 + x1 - bitmapSun.getWidth() / 2, cY1 - y1 - bitmapSun.getHeight() / 2, null);
//    }
    
    /**
     * Function which rotate arrow and compas (angles in degrees)
     * @param azimuth new angle for compas north
     * @param azimuthDiff new angle for arrow
     */
    public void moveAngles(float azimuthToTarget, float azimuth, float pitch, float roll) {
		this.mAzimuthToTarget = azimuthToTarget;        
		this.mAzimuth = azimuth;
		this.mPitch = pitch;
		this.mRoll = roll;
        invalidate();
    }
    
    public void setDistance(double distance) {
    	this.mDistanceToTarget = distance;
    	invalidate();
    }
}
