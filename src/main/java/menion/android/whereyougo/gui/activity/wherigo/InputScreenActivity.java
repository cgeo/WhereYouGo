/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo.gui.activity.wherigo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Media;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import se.krka.kahlua.vm.LuaTable;

public class InputScreenActivity extends CustomActivity {

    private static final String TAG = "InputScreen";
    private static final int TEXT = 0;
    private static final int MULTI = 1;
    private static EventTable input;
    private int mode = TEXT;

    public static void reset(EventTable input) {
        InputScreenActivity.input = input;
    }

    public static void setInput(EventTable input) {
        InputScreenActivity.input = input;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (A.getMain() == null || Engine.instance == null || input == null) {
            finish();
            return;
        }
        setContentView(R.layout.layout_input);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Engine.callEvent(input, "OnGetInput", null);
            InputScreenActivity.this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onResume() {
        super.onResume();

        try {
            // set image and it's label
            ImageView ivImage = (ImageView) findViewById(R.id.layoutInputImageView01);
            TextView tvImageDesc = (TextView) findViewById(R.id.layoutInputTextView01);

            Media m = (Media) input.table.rawget("Media");
            if (m != null) {
                tvImageDesc.setText(UtilsGUI.simpleHtml(m.altText));
                try {
                    byte[] is = Engine.mediaFile(m);
                    Bitmap i = BitmapFactory.decodeByteArray(is, 0, is.length);
                    MainActivity.setBitmapToImageView(i, ivImage);
                } catch (Exception e) {
                }
            } else {
                ivImage.setImageBitmap(Images.IMAGE_EMPTY_B);
            }

            // set question TextView
            TextView tvQuestion = (TextView) findViewById(R.id.layoutInputTextView02);
            String text = (String) input.table.rawget("Text");
            tvQuestion.setText(UtilsGUI.simpleHtml(text));

            // set answer
            final EditText editText = (EditText) findViewById(R.id.layoutInputEditText);
            editText.setVisibility(View.GONE);
            final Spinner spinner = (Spinner) findViewById(R.id.layoutInputSpinner);
            spinner.setVisibility(View.GONE);
            String type = (String) input.table.rawget("InputType");
            mode = -1;

            if ("Text".equals(type)) {
                editText.setText("");
                editText.setVisibility(View.VISIBLE);
                mode = TEXT;
            } else if ("MultipleChoice".equals(type)) {
                LuaTable choices = (LuaTable) input.table.rawget("Choices");
                String[] data = new String[choices.len()];
                for (int i = 0; i < choices.len(); i++) {
                    data[i] = (String) choices.rawget((double) (i + 1));
                    if (data[i] == null)
                        data[i] = "-";
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setVisibility(View.VISIBLE);
                mode = MULTI;
            }

            CustomDialog.setBottom(this, Locale.getString(R.string.answer), new CustomDialog.OnClickListener() {

                @Override
                public boolean onClick(CustomDialog dialog, View v, int btn) {
                    if (mode == TEXT) {
                        Engine.callEvent(input, "OnGetInput", editText.getText()
                                .toString());
                    } else if (mode == MULTI) {
                        String item = String.valueOf(spinner.getSelectedItem());
                        Engine.callEvent(input, "OnGetInput", item);
                    } else {
                        Engine.callEvent(input, "OnGetInput", null);
                    }
                    InputScreenActivity.this.finish();
                    return true;
                }
            }, null, null, null, null);
        } catch (Exception e) {
            Logger.e(TAG, "onResume()", e);
        }
    }
}
