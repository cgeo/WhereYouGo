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

package menion.android.whereyougo.gui.extension;

import menion.android.whereyougo.R;
import menion.android.whereyougo.utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {

	public static final int NO_IMAGE = Integer.MIN_VALUE;

    public interface OnClickListener {
    	public boolean onClick(CustomDialog dialog, View v, int btn);
    }

	public static final CustomDialog.OnClickListener CLICK_CANCEL = new CustomDialog.OnClickListener() {
		@Override
		public boolean onClick(CustomDialog dialog, View v, int which) {
			return true;
		}
	};
	
    public CustomDialog(Context context) {
        super(context);
    }

	public CustomDialog(Context context, int theme) {
		super(context, theme);

	}
	
	public View getButton(int buttonId) {
		switch (buttonId) {
		case BUTTON_POSITIVE:
			return findViewById(R.id.button_positive);
		case BUTTON_NEUTRAL:
			return findViewById(R.id.button_neutral);
		case BUTTON_NEGATIVE:
			return findViewById(R.id.button_negative);
		} 
		return null;
	}
	
    public void show(Activity ownerActivity) {
    	setOwnerActivity(ownerActivity);
    	show();
    }
    
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
 
        private Context context;
        private boolean cancelable;

        // title
        private Bitmap titleImage = null;
        private CharSequence titleText = null;
        
        private int titleExtraImg1 = NO_IMAGE;
        private OnClickListener titleExtraClick1;
        
        private int titleExtraImg2 = NO_IMAGE;
        private OnClickListener titleExtraClick2;
        
        // content
        private View contentView;
        private boolean fillWidth;
        private int margins;
        
        // bottom buttons
        private String positiveButtonText;
        private OnClickListener positiveButtonClickListener;
        private String neutralButtonText;
        private OnClickListener neutralButtonClickListener;
        private String negativeButtonText;
        private OnClickListener negativeButtonClickListener;
        
        // cancel listener
        private DialogInterface.OnCancelListener onCancelListener;
        
        public Builder(Context context, boolean cancelable) {
            this.context = context;
            this.cancelable = cancelable;
        }

        public Builder setTitle(int title) {
        	return setTitle(context.getText(title), null);
        }
 
        public Builder setTitle(CharSequence title) {
            this.titleText = title;
            return this;
        }
        
        public Builder setTitle(CharSequence title, int icon) {
        	return setTitle(title, icon == NO_IMAGE ? null :
        		BitmapFactory.decodeResource(context.getResources(), icon));
        }

        public Builder setTitle(int title, int icon) {
        	return setTitle(context.getText(title), icon);
        }
        
        public Builder setTitle(CharSequence title, Bitmap icon) {
            this.titleText = title;
           	this.titleImage = icon;
           	return this;
        }
        
        public Builder setTitleExtra(int titleExtraImg, OnClickListener titleExtraClick) {
        	this.titleExtraImg1 = titleExtraImg;
        	this.titleExtraClick1 = titleExtraClick;
        	return this;
        }
        
        public Builder setTitleExtraButton2(int titleExtraImg, OnClickListener titleExtraClick) {
        	this.titleExtraImg2 = titleExtraImg;
        	this.titleExtraClick2 = titleExtraClick;
        	return this;
        }
        
        public Builder setTitleExtraCancel() {
        	return setTitleExtra(R.drawable.ic_cancel, CLICK_CANCEL);
        }
        
        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
        	setMessage(Html.fromHtml(message));
            return this;
        }
 
        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
        	return setMessage(context.getText(message));
        }
        
        public Builder setMessage(CharSequence text) {
        	int paddingTB = (int) (Utils.getDpPixels(15.0f));
        	int paddingLR = text.length() < 50 ?
        			(int) (Utils.getDpPixels(20.0f)) : (int) (Utils.getDpPixels(15.0f));
        	
        	TextView tv = new TextView(context);
        	tv.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
            tv.setText(text);
            tv.setTextColor(Color.BLACK);
            return setContentView(tv, false);
        }
 
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v, boolean fillWidth) {
            this.contentView = v;
            this.fillWidth = fillWidth;
            return this;
        }
        
        public Builder setContentView(View v, int margins, boolean fillWidth) {
        	this.margins = margins;
        	return setContentView(v, fillWidth);
        }
        
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = context.getString(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        public Builder setNeutralButtonCancel(int neutralButtonText) {
            this.neutralButtonText = (String) context.getText(neutralButtonText);
            this.neutralButtonClickListener = CLICK_CANCEL;
            return this;
        }
        
        public Builder setNeutralButton(int neutralButtonText, OnClickListener listener) {
            this.neutralButtonText = context.getString(neutralButtonText);
            this.neutralButtonClickListener = listener;
            return this;
        }
        
        public Builder setNeutralButton(String neutralButtonText, OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }
        
        public Builder setNegativeButton(int negativeButtonText, OnClickListener negativeButtonClickListener) {
            this.negativeButtonText = context.getString(negativeButtonText);
            this.negativeButtonClickListener = negativeButtonClickListener;
            return this;
        }
 
        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        
        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelList) {
        	this.onCancelListener = onCancelList;
        	return this;
        }
 
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View view = inflater.inflate(R.layout.custom_dialog, null);

            // set the dialog title
            setCustomDialogTitle(dialog, view, titleText, titleImage,
            		titleExtraImg1, titleExtraClick1,
            		titleExtraImg2, titleExtraClick2);
            
            // set the content message
            if (contentView != null) {
                // add the contentView to the dialog body
                LayoutParams lp = new LayoutParams(
                		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                lp.setMargins(margins, margins, margins, margins);
                addViewToContent(((LinearLayout) view.findViewById(R.id.linear_layout_content)), lp,
                		contentView);
            }
            
    		// set buttons (at last because of bottom shadow)
            setCustomDialogBottom(dialog, view, positiveButtonText, positiveButtonClickListener,
            		neutralButtonText, neutralButtonClickListener,
            		negativeButtonText, negativeButtonClickListener);
            
            if (onCancelListener != null)
            	dialog.setOnCancelListener(onCancelListener);
            
            dialog.setContentView(view);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(cancelable);
            if (context instanceof Activity)
            	dialog.setOwnerActivity((Activity) context);
            
			if (contentView != null && fillWidth) {
               	// set width to max!
                LinearLayout llCon = (LinearLayout) view.findViewById(R.id.linear_layout_content);
                llCon.setLayoutParams(new RelativeLayout.LayoutParams(
                		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			
            return dialog;
        }

		public void show() {
			create().show();
		}
    }
    
    /*********************************/
    /*     STATIC SET VARIABLES      */
    /*********************************/
    
    public static void setTitle(Activity activity,
    		CharSequence titleText, Bitmap titleImage, 
    		int titleExtraImg, final OnClickListener titleExtraClick) {
    	setCustomDialogTitle(null, activity.findViewById(R.id.linear_layout_main),
    			titleText, titleImage, titleExtraImg, titleExtraClick, NO_IMAGE, null);
    }
    
    private static void setCustomDialogTitle(final CustomDialog dialog, View view,
    		CharSequence titleText, Bitmap titleImage, 
    		int titleExtraImg1, final OnClickListener titleExtraClick1,
    		int titleExtraImg2, final OnClickListener titleExtraClick2) {
        // set the dialog title
        if (titleImage == null && titleText == null &&
        		titleExtraImg1 == NO_IMAGE && titleExtraImg2 == NO_IMAGE) {
        	// hide title
        	((LinearLayout) view.findViewById(R.id.linear_layout_title)).setVisibility(View.GONE);
        } else {
        	// set title image
    		if (titleImage == null) {
    			((ImageView) view.findViewById(R.id.image_view_title_logo)).setVisibility(View.INVISIBLE);
    		} else {
    			((ImageView) view.findViewById(R.id.image_view_title_logo)).setImageBitmap(titleImage);
    		}
    		
    		// set title text
    		((TextView) view.findViewById(R.id.text_view_title_text)).setText(titleText);
    		
    		// set title extra buttons
    		setCustomDialogTitleButton(dialog, view, TITLE_BUTTON_RIGHT, titleExtraImg1, titleExtraClick1);
    		setCustomDialogTitleButton(dialog, view, TITLE_BUTTON_LEFT, titleExtraImg2, titleExtraClick2);
        }
    }
    
    private static final int TITLE_BUTTON_RIGHT = 1;
    private static final int TITLE_BUTTON_LEFT = 2;
    
    private static void setCustomDialogTitleButton(final CustomDialog dialog, View view, int button,
    		int titleExtraImg, final OnClickListener titleExtraClick) {
		// set title extra
		if (titleExtraImg != NO_IMAGE && titleExtraClick != null) {
			ImageView iv = null;
			ImageButton ib = null;
			if (button == TITLE_BUTTON_RIGHT) {
				iv = (ImageView) view.findViewById(R.id.image_view_separator1);
				ib = (ImageButton) view.findViewById(R.id.image_button_title1);
			} else {
				iv = (ImageView) view.findViewById(R.id.image_view_separator2);
				ib = (ImageButton) view.findViewById(R.id.image_button_title2);
			}
			
			iv.setVisibility(View.VISIBLE);
			ib.setVisibility(View.VISIBLE);
			ib.setImageResource(titleExtraImg);
			ib.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (titleExtraClick.onClick(dialog, v, 0)) {
						if (dialog != null)
							dialog.dismiss();
					}
				}
			});
		}
    }
    
    public static void setBottom(Activity activity, 
    		String positiveButtonText, OnClickListener positiveButtonClickListener,
    		String neutralButtonText, OnClickListener neutralButtonClickListener,
    		String negativeButtonText, OnClickListener negativeButtonClickListener) {
    	setCustomDialogBottom(null, activity.findViewById(R.id.linear_layout_bottom),
    			positiveButtonText, positiveButtonClickListener,
    			neutralButtonText, neutralButtonClickListener,
    			negativeButtonText, negativeButtonClickListener);
    }
    
    private static void setCustomDialogBottom(CustomDialog dialog, View view, 
    		String positiveButtonText, OnClickListener positiveButtonClickListener,
    		String neutralButtonText, OnClickListener neutralButtonClickListener,
    		String negativeButtonText, OnClickListener negativeButtonClickListener) {
        int btnCount = 0;
        if (setButton(dialog, view, R.id.button_positive, DialogInterface.BUTTON_POSITIVE,
				positiveButtonText, positiveButtonClickListener))
        	btnCount++;
		if (setButton(dialog, view, R.id.button_negative, DialogInterface.BUTTON_NEGATIVE,
				negativeButtonText, negativeButtonClickListener))
			btnCount++;
		if (setButton(dialog, view, R.id.button_neutral, DialogInterface.BUTTON_NEUTRAL,
				neutralButtonText, neutralButtonClickListener))
			btnCount++;
		
		if (btnCount == 0) {
			view.findViewById(R.id.linear_layout_bottom).setVisibility(View.GONE);
		} else if (btnCount == 1) {
			view.findViewById(R.id.linear_layout_left_spacer).setVisibility(View.VISIBLE);
			view.findViewById(R.id.linear_layout_right_spacer).setVisibility(View.VISIBLE);
		}
    }
    
    public static void setContent(Activity activity, View view, int margins, boolean dialog) {
    	setContent(activity, view, margins, false, dialog);
    }
    
    public static void setContent(Activity activity, View view, int margins,
    		boolean fillHeight, boolean dialog) {
    	// set width to correct values if dialog is shown
    	if (dialog)
    		UtilsGUI.setWindowDialogCorrectWidth(activity.getWindow());

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        		fillHeight ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT);
        if (margins > 0)
        	lp.setMargins(margins, activity.getResources().getDimensionPixelSize(R.dimen.shadow_height)
        			+ margins, margins, margins);
        LinearLayout llCon = (LinearLayout) activity.findViewById(R.id.linear_layout_content);
        llCon.setLayoutParams(new RelativeLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT, fillHeight ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT));
        addViewToContent(llCon, lp, view);
    }
    
    private static boolean setButton(final CustomDialog dialog, View layout, int btnId, final int btnType, 
    		String text, final OnClickListener click) {
        if (text != null && click != null) {
            ((Button) layout.findViewById(btnId)).setText(text);
            ((Button) layout.findViewById(btnId)).setOnClickListener(new View.OnClickListener() {
            	public void onClick(View v) {
            		if (click.onClick(dialog, v, btnType)) {
            			if (dialog != null)
            				dialog.dismiss();
            		}
            	}
            });	
            return true;
        } else {
            // if no confirm button just set the visibility to GONE
            layout.findViewById(btnId).setVisibility(View.GONE);
            return false;
        }
    }
    
    private static void addViewToContent(View viewContent, 
    		LinearLayout.LayoutParams llLp, View view) {
    	LinearLayout llContent = (LinearLayout) viewContent;
    	llContent.removeAllViews();
    	if (llLp == null) {
    		llContent.addView(view, 
    				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    	} else{
    		llContent.addView(view, llLp);	
    	}
    }
}
