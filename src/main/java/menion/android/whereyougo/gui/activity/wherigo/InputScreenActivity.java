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

import java.util.Arrays;

import cz.matejcik.openwig.DialogObject;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Media;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;

public class InputScreenActivity extends CustomActivity {

    private static final String TAG = "InputScreen";
    private static final int TEXT = 0;
    private static final int MULTI = 1;

    private static DialogObject dobj;
    private static String[] choices;
    private static int mode = TEXT;

    public static void setArguments(DialogObject dobj) {
        Logger.d(TAG, String.format("setArguments(%s)", dobj));
        synchronized (InputScreenActivity.class) {
            InputScreenActivity.dobj = dobj;
            InputScreenActivity.mode = TEXT;
        }
    }

    public static void setArguments(DialogObject dobj, String[] choices) {
        Logger.d(TAG, String.format("setArguments(%s, %s)", dobj, Arrays.toString(choices)));
        synchronized (InputScreenActivity.class) {
            InputScreenActivity.dobj = dobj;
            InputScreenActivity.choices = choices;
            InputScreenActivity.mode = MULTI;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, String.format("onCreate(%d, %s, %s)", mode, dobj, Arrays.toString(choices)));
        if (A.getMain() == null || Engine.instance == null) {
            finish();
            return;
        }
        setContentView(R.layout.layout_input);

        // set image and it's label
        ImageView ivImage = (ImageView) findViewById(R.id.layoutInputImageView01);
        TextView tvImageDesc = (TextView) findViewById(R.id.layoutInputTextView01);

        Media m = dobj.media;
        if (m != null) {
            tvImageDesc.setText(m.altText);
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
        String text = dobj.text;
        tvQuestion.setText(text);

        // set answer
        final EditText editText = (EditText) findViewById(R.id.layoutInputEditText);
        editText.setVisibility(View.GONE);
        final Spinner spinner = (Spinner) findViewById(R.id.layoutInputSpinner);
        spinner.setVisibility(View.GONE);
        CustomDialog.OnClickListener onClickListener = null;

        if (mode == TEXT) {
            editText.setText("");
            editText.setVisibility(View.VISIBLE);
            onClickListener = new CustomDialog.OnClickListener() {

                @Override
                public boolean onClick(CustomDialog dialog, View v, int btn) {
                    dobj.doCallback(editText.getText().toString());
                    InputScreenActivity.this.finish();
                    return true;
                }
            };
        } else if (mode == MULTI) {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choices);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setVisibility(View.VISIBLE);
            onClickListener = new CustomDialog.OnClickListener() {

                @Override
                public boolean onClick(CustomDialog dialog, View v, int btn) {
                    dobj.doCallback(spinner.getSelectedItemPosition());
                    InputScreenActivity.this.finish();
                    return true;
                }
            };
        }

        CustomDialog.setBottom(this,
                Locale.getString(R.string.answer), onClickListener,
                null, null,
                null, null
        );
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG, String.format("onKeyDown(%d, %s)", keyCode, event));
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dobj.doCallback(null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
