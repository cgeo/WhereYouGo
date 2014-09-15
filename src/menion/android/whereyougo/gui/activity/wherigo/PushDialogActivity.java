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

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import se.krka.kahlua.vm.LuaClosure;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Media;

public class PushDialogActivity extends CustomActivity {

  private static final String TAG = "PushDialog";

  private static String menu01Text = null;
  private static String menu02Text = null;

  // STATIC CONTENT
  private static String[] texts;
  private static Media[] media;
  private static LuaClosure callback;
  private static int page = -1;

  public static void setDialog(String[] texts, Media[] media, String button1, String button2,
      LuaClosure callback) {
    synchronized (PushDialogActivity.class) {
      PushDialogActivity.texts = texts;
      PushDialogActivity.media = media;
      PushDialogActivity.callback = callback;
      PushDialogActivity.page = -1;

      if (button1 == null)
        button1 = "OK";

      menu01Text = button1;
      menu02Text = button2;
      Logger.d(TAG, "setDialog() - finish, callBack:" + (callback != null));
    }
  }

  private ImageView ivImage;
  private TextView tvImageText;

  private void nextPage() {
    synchronized (PushDialogActivity.class) {
      Logger.d(TAG, "nextpage() - page:" + page + ", texts:" + texts.length + ", callback:"
          + (callback != null));
      page++;
      if (page >= texts.length) {
        if (callback != null) {
          LuaClosure call = callback;
          callback = null;
          Engine.invokeCallback(call, "Button1");
        }
        PushDialogActivity.this.finish();
        return;
      }

      tvImageText.setText("");

      Media m = media[page];
      if (m != null) {
        try {
          byte[] img = Engine.mediaFile(m);
          MainActivity.setBitmapToImageView(BitmapFactory.decodeByteArray(img, 0, img.length),
              ivImage);
        } catch (Exception e) {
          tvImageText.setText(Html.fromHtml(m.altText));
        }
      } else {
        ivImage.setImageBitmap(null);
        ivImage.setMinimumWidth(0);
        ivImage.setMinimumHeight(0);
      }

      tvImageText.setText(Html.fromHtml(tvImageText.getText().toString() + "\n" + texts[page]));
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (A.getMain() == null || Engine.instance == null) {
      finish();
      return;
    }
    setContentView(R.layout.layout_details);
    if (!Preferences.APPEARANCE_IMAGE_STRETCH) {
      findViewById(R.id.layoutDetailsImageViewImage).getLayoutParams().width =
          LayoutParams.WRAP_CONTENT;
    }
    findViewById(R.id.layoutDetailsTextViewName).setVisibility(View.GONE);
    findViewById(R.id.layoutDetailsTextViewState).setVisibility(View.GONE);
    findViewById(R.id.layoutDetailsTextViewDescription).setVisibility(View.GONE);
    ivImage = (ImageView) findViewById(R.id.layoutDetailsImageViewImage);
    tvImageText = (TextView) findViewById(R.id.layoutDetailsTextViewImageText);
    findViewById(R.id.layoutDetailsTextViewDistance).setVisibility(View.GONE);

    if (menu02Text == null || menu02Text.length() == 0) {
      menu02Text = null;
    }

    CustomDialog.setBottom(this, menu01Text, new CustomDialog.OnClickListener() {

      @Override
      public boolean onClick(CustomDialog dialog, View v, int btn) {
        nextPage();
        return true;
      }
    }, null, null, menu02Text, new CustomDialog.OnClickListener() {

      @Override
      public boolean onClick(CustomDialog dialog, View v, int btn) {
        if (callback != null)
          Engine.invokeCallback(callback, "Button2");
        callback = null;
        PushDialogActivity.this.finish();
        return true;
      }
    });

    if (page == -1) {
      nextPage();
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Logger.d(TAG, "onKeyDown(" + keyCode + ", " + event + ")");
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }
}
