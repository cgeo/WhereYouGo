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

import java.util.ArrayList;

import menion.android.whereyougo.R;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ListView;

public class UtilsGUI {

	public static int getUniqueId() {
		return (int) (Math.random() * Integer.MAX_VALUE);
	}
	
	public static void setWindowDialogCorrectWidth(Window window) {
		android.view.WindowManager.LayoutParams params = window.getAttributes(); 
        params.width = getDialogWidth();
        window.setAttributes((android.view.WindowManager.LayoutParams) params); 
	}
	
	public static void setWindowFloatingRight(Activity activity) {
		int height = Math.min(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT);
		// set sizes to window
		android.view.WindowManager.LayoutParams params = activity.getWindow().getAttributes();
		// set width
        params.width = UtilsGUI.getDialogWidth();
        params.height = height;
        // set location
        params.x = (int) (Const.SCREEN_WIDTH - params.width - Utils.getDpPixels(10.0f));
        //params.y = 10;//(Const.SCREEN_HEIGHT - height) / 4;
        // commit
        activity.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	}
	
	public static int getDialogWidth() {
		if (Utils.isAndroid30OrMore()) {
			if (Const.SCREEN_WIDTH < 600) {
				return LayoutParams.MATCH_PARENT;
			} else {
				return 600;
			}
		} else {
			return LayoutParams.MATCH_PARENT;
		}
	}
	
	public static final int DIALOG_EDIT_TEXT_ID = 10005;
	public static final int DIALOG_SPINNER_ID = 10006;
	
	/******************************/
	/*      DIALOG QUESTION       */
	/******************************/
	
	public static void showDialogQuestion(Activity activity, CharSequence msg,
			CustomDialog.OnClickListener posLis) {
		showDialogQuestion(activity, msg, posLis, CustomDialog.CLICK_CANCEL);
	}
	
	public static void showDialogQuestion(Activity activity, int msg,
			CustomDialog.OnClickListener posLis) {
		showDialogQuestion(activity, activity.getText(msg), posLis, CustomDialog.CLICK_CANCEL);
	}
	
	public static void showDialogQuestion(Activity activity, int msg,
			CustomDialog.OnClickListener posLis, CustomDialog.OnClickListener negLis) {
		showDialogQuestion(activity, activity.getText(msg), posLis, negLis);
	}

	public static void showDialogQuestion(Activity activity, CharSequence msg,
			CustomDialog.OnClickListener posLis, CustomDialog.OnClickListener negLis) {
		dialogDoItem(activity,
				activity.getText(R.string.question),
				R.drawable.ic_question_default,
				msg,
				activity.getString(R.string.yes), posLis,
				activity.getString(R.string.no), negLis);
	}
	
	/******************************/
	/*        DIALOG INFO         */
	/******************************/
	
	public static void showDialogInfo(Activity activity, int msg) {
		showDialogInfo(activity, activity.getText(msg));
	}
	
	public static void showDialogInfo(Activity activity, CharSequence msg) {
		dialogDoItem(activity, activity.getText(R.string.info),  R.drawable.ic_warning_default,
				msg, null, null, activity.getString(R.string.close), CustomDialog.CLICK_CANCEL);
	}
	
	public static void showDialogInfo(Activity activity, int msg,
			CustomDialog.OnClickListener cancelList) {
		dialogDoItem(activity, activity.getText(R.string.info), R.drawable.ic_warning_default,
				activity.getText(msg), null, null, activity.getString(R.string.close), cancelList);
	}
	
	/******************************/
	/*        DIALOG ERROR        */
	/******************************/
	
	public static void showDialogError(Activity activity, int msg,
			CustomDialog.OnClickListener cancelList) {
		showDialogError(activity, activity.getText(msg), cancelList);
	}
	
	public static void showDialogError(Activity activity, CharSequence msg) {
		showDialogError(activity, msg, CustomDialog.CLICK_CANCEL);
	}
	
	public static void showDialogError(Activity activity, CharSequence msg,
			CustomDialog.OnClickListener cancelList) {
		dialogDoItem(activity,
				activity.getText(R.string.error), R.drawable.ic_info_default,
				msg, null, null, activity.getString(R.string.close), cancelList);
	}
	
	/******************************/
	/*       DIALOG DELETE        */
	/******************************/
	
	public static void showDialogDeleteItem(Activity activity, String itemName,
			CustomDialog.OnClickListener posLis) {
		dialogDoItem(activity,
				Loc.get(R.string.question), R.drawable.ic_question_default,
				(itemName != null ? activity.getString(R.string.do_you_really_want_to_delete_x, 
						Html.fromHtml(itemName)) :
			activity.getText(R.string.do_you_really_want_to_delete_selected_items)),
			activity.getString(R.string.yes), posLis,
			activity.getString(R.string.no), CustomDialog.CLICK_CANCEL);
	}
	
	/*******************************/
	/*     WEBVIEW CONSTRUCTION    */
	/*******************************/
	
	public static void showDialogWebView(final Activity activity, final int title,
			final String msg) {
		showDialogWebView(activity, activity.getString(title), msg);
	}
	
	public static void showDialogWebView(final Activity activity, final String title,
			final String msg) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				new CustomDialog.Builder(activity, true).
				setTitle(title).
				setTitleExtraCancel().
				setContentView(getFilledWebView(activity, msg), true).
				show();
			}
		});
	}
	
    public static WebView getFilledWebView(Activity activity, String data) {
    	WebView webView = new WebView(activity);
		//webView.loadData(UtilsHttp.repairHtmlFile(data), "text/html", "utf-8");
    	try {
//    		webView.loadData(URLEncoder.encode(
//    				UtilsHttp.repairHtmlFile(data),"utf-8").replaceAll("\\+"," "),
//    				"text/html", "utf-8");
    		// http://stackoverflow.com/questions/4917860/android-character-encoding-raw-resource-files
    		webView.loadDataWithBaseURL(null, data.replaceAll("\\+"," "),
    				"text/html", "utf-8", null);
    	} catch (Exception e) {}
   		webView.setLayoutParams(new  ViewGroup.LayoutParams(
   				getDialogWidth(), LayoutParams.WRAP_CONTENT));
		webView.setBackgroundColor(Color.WHITE);
		return webView;
    }
	
	/******************************/
	/*     DIALOG CONSTRUCTION    */
	/******************************/
    
	public static void dialogDoItem(final Activity activity,
			final CharSequence title, final int icon,
			final CharSequence msg, 
			final String posText, final CustomDialog.OnClickListener posLis,
			final String negText, final CustomDialog.OnClickListener negLis) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				new CustomDialog.Builder(activity, false).
				setTitle(title, icon).
				setMessage(msg).
				setPositiveButton(posText, posLis).
				setNegativeButton(negText, negLis).
				show();
			}
		});
	}

	public static void setButtons(Activity a, int[] btns,
			View.OnClickListener onClick, View.OnLongClickListener onLongClick) {
		for (int btn : btns) {
			if (onClick != null)
				((View) a.findViewById(btn)).setOnClickListener(onClick);
			if (onLongClick != null)
				((View) a.findViewById(btn)).setOnLongClickListener(onLongClick);
		}
	}
	
	/******************************/
	/*       LIST VIEW PART       */
	/******************************/
	
    public static ListView createListView(Context context, boolean setMultiple, ArrayList<DataInfo> adapterData) {
    	ListView lv = new ListView(context);
    	setListView(context, lv, setMultiple, adapterData);
		return lv;
    }
    
    /**
     * Prepare ListView with added data. Set IconedListAdapter as adapter, second line
     * to visible and fast scroll if more then 50 items in adapter array.
     * @param context
     * @param lv
     * @param adapterData
     */
    public static void setListView(Context context, ListView lv, boolean setMultiple, ArrayList<DataInfo> adapterData) {
    	if (setMultiple)
    		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    	IconedListAdapter adapter = new IconedListAdapter(context, adapterData, lv);
    	adapter.setTextView02Visible(View.VISIBLE, true);
    	if (adapterData.size() > 50)
    		lv.setFastScrollEnabled(true);
		lv.setAdapter(adapter);
    }
}
