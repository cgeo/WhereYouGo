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

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cz.matejcik.openwig.DialogObject;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Media;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;

public class PushDialogActivity extends CustomActivity {

    private static final String TAG = "PushDialog";

    private static DialogObject dobj;
    private static String button1;
    private static String button2;

    public static void setArguments(DialogObject dobj, String button1, String button2) {
        Logger.d(TAG, String.format("setArguments(%s, %s, %s)", dobj, button1, button2));
        synchronized (PushDialogActivity.class) {
            if (button1 == null)
                button1 = Locale.getString(R.string.ok);
            PushDialogActivity.button1 = button1;
            PushDialogActivity.button2 = button2;
            PushDialogActivity.dobj = dobj;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, String.format("onCreate(%s, %s, %s)", dobj, button1, button2));
        if (A.getMain() == null || Engine.instance == null) {
            finish();
            return;
        }
        setContentView(R.layout.layout_details);
        findViewById(R.id.layoutDetailsTextViewName).setVisibility(View.GONE);
        findViewById(R.id.layoutDetailsTextViewState).setVisibility(View.GONE);
        findViewById(R.id.layoutDetailsTextViewDistance).setVisibility(View.GONE);
        findViewById(R.id.layoutDetailsTextViewDescription).setVisibility(View.GONE);
        ImageView ivImage = (ImageView) findViewById(R.id.layoutDetailsImageViewImage);
        TextView tvImageText = (TextView) findViewById(R.id.layoutDetailsTextViewImageText);

        tvImageText.setText("");

        Media m = dobj.media;
        if (m != null) {
            try {
                byte[] img = Engine.mediaFile(m);
                MainActivity.setBitmapToImageView(BitmapFactory.decodeByteArray(img, 0, img.length),
                        ivImage);
            } catch (Exception e) {
                tvImageText.setText(m.altText);
            }
        } else {
            ivImage.setImageBitmap(null);
            ivImage.setMinimumWidth(0);
            ivImage.setMinimumHeight(0);
        }

        tvImageText.setText(tvImageText.getText().toString() + "\n" + dobj.text);

        if (button2 == null || button2.length() == 0) {
            button2 = null;
        }

        CustomDialog.setBottom(this,
                button1, new CustomDialog.OnClickListener() {

                    @Override
                    public boolean onClick(CustomDialog dialog, View v, int btn) {
                        dobj.doCallback("Button1");
                        PushDialogActivity.this.finish();
                        return true;
                    }
                },
                null, null,
                button2, new CustomDialog.OnClickListener() {

                    @Override
                    public boolean onClick(CustomDialog dialog, View v, int btn) {
                        dobj.doCallback("Button2");
                        PushDialogActivity.this.finish();
                        return true;
                    }
                }
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
