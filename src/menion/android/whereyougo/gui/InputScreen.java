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

import menion.android.whereyougo.Main;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import se.krka.kahlua.vm.LuaTable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Media;

public class InputScreen extends CustomActivity {

	private static final String TAG = "InputScreen";
	
	private static EventTable input;

	private static final int TEXT = 0;
	private static final int MULTI = 1;

	private int mode = TEXT;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_input);
	}
	
	public static void setInput(EventTable input) {
		InputScreen.input = input;
	}
	
	public void onResume() {
		super.onResume();
		
		try {
			// set image and it's label
			ImageView ivImage = (ImageView) findViewById(R.id.layoutInputImageView01);
			TextView tvImageDesc = (TextView) findViewById(R.id.layoutInputTextView01); 
	
			Media m = (Media)input.table.rawget("Media");
			if (m != null) {
				tvImageDesc.setText(m.altText);
				try {
					byte[] is = Engine.mediaFile(m);
					Bitmap i = BitmapFactory.decodeByteArray(is, 0, is.length);
					Main.setBitmapToImageView(i, ivImage);
				} catch (Exception e) { }
			} else {
				ivImage.setImageBitmap(Images.IMAGE_EMPTY_B);
			}
			
			// set question TextView
			TextView tvQuestion = (TextView) findViewById(R.id.layoutInputTextView02);
			String text = Engine.removeHtml((String)input.table.rawget("Text"));
			tvQuestion.setText(text);
			
			// set answer LinearLayout
			String type = (String) input.table.rawget("InputType");
			final LinearLayout ll = (LinearLayout) findViewById(R.id.layoutInputLinearLayout01);
			mode = -1;

			if ("Text".equals(type)) {
				EditText editText = new EditText(this);
				editText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				ll.addView(editText);
				mode = TEXT;
			} else if ("MultipleChoice".equals(type)) {
				LuaTable choices = (LuaTable) input.table.rawget("Choices");
				String[] data = new String[choices.len()];
				for (int i = 0; i < choices.len(); i++) {
					data[i] = (String) choices.rawget(new Double(i + 1));
					if (data[i] == null)
						data[i] = "-";
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner spinner = new Spinner(this);
				spinner.setAdapter(adapter);
				spinner.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				ll.addView(spinner);
				
				mode = MULTI;
			}
			
			CustomDialog.setBottom(this, 
					Loc.get(R.string.answer), new CustomDialog.OnClickListener() {
						
						@Override
						public boolean onClick(CustomDialog dialog, View v, int btn) {
							if (mode == TEXT) {
								Engine.callEvent(input, "OnGetInput", ((EditText) ll.getChildAt(0)).getText().toString());
							} else if (mode == MULTI) {
								String item = String.valueOf(((Spinner) ll.getChildAt(0)).getSelectedItem());
								Engine.callEvent(input, "OnGetInput", item);
							} else {
								Engine.callEvent(input, "OnGetInput", null);
							}
							InputScreen.this.finish();
							return true;
						}
					}, null, null, null, null);
		} catch (Exception e) {
			Logger.e(TAG, "onResume()", e);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Engine.callEvent(input, "OnGetInput", null);
			InputScreen.this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	public static void reset(EventTable input) {
		InputScreen.input = input;
	}
}
