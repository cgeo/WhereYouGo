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

package menion.android.whereyougo.gui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.guide.Guide;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.UtilsFormat;

public class CartridgeDetailsActivity extends CustomActivity {

    private static final String TAG = "CartridgeDetails";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (A.getMain() == null || MainActivity.selectedFile == null
                || MainActivity.cartridgeFile == null) {
            finish();
            return;
        }
        setContentView(R.layout.layout_details);
        if (!Preferences.APPEARANCE_IMAGE_STRETCH) {
            findViewById(R.id.layoutDetailsImageViewImage).getLayoutParams().width = LayoutParams.WRAP_CONTENT;
        }

        TextView tvName = (TextView) findViewById(R.id.layoutDetailsTextViewName);
        tvName.setText(Html.fromHtml(MainActivity.cartridgeFile.name));

        TextView tvState = (TextView) findViewById(R.id.layoutDetailsTextViewState);
        tvState.setText(Html.fromHtml(getString(R.string.author) + ": "
                + MainActivity.cartridgeFile.author));

        TextView tvDescription = (TextView) findViewById(R.id.layoutDetailsTextViewDescription);
        tvDescription.setText(Html.fromHtml(MainActivity.cartridgeFile.description));

        ImageView ivImage = (ImageView) findViewById(R.id.layoutDetailsImageViewImage);
        try {
            byte[] is = MainActivity.cartridgeFile.getFile(MainActivity.cartridgeFile.splashId);
            Bitmap i = BitmapFactory.decodeByteArray(is, 0, is.length);
            MainActivity.setBitmapToImageView(i, ivImage);
        } catch (Exception e) {
        }

        TextView tvText = (TextView) findViewById(R.id.layoutDetailsTextViewImageText);
        tvText.setVisibility(View.GONE);

        TextView tvDistance = (TextView) findViewById(R.id.layoutDetailsTextViewDistance);

        Location loc = new Location(TAG);
        loc.setLatitude(MainActivity.cartridgeFile.latitude);
        loc.setLongitude(MainActivity.cartridgeFile.longitude);

        StringBuilder buff = new StringBuilder();
        buff.append(getString(R.string.distance)).append(": ").append("<b>")
                .append(UtilsFormat.formatDistance(LocationState.getLocation().distanceTo(loc), false))
                .append("</b>").append("<br />").append(getString(R.string.latitude)).append(": ")
                .append(UtilsFormat.formatLatitude(MainActivity.cartridgeFile.latitude)).append("<br />")
                .append(getString(R.string.longitude)).append(": ")
                .append(UtilsFormat.formatLatitude(MainActivity.cartridgeFile.longitude));

        tvDistance.setText(Html.fromHtml(buff.toString()));

        CustomDialog.setBottom(this, getString(R.string.start), new CustomDialog.OnClickListener() {
            @Override
            public boolean onClick(CustomDialog dialog, View v, int btn) {
                CartridgeDetailsActivity.this.finish();
                MainActivity.startSelectedCartridge(false);
                return true;
            }
        }, null, null, getString(R.string.navigate), new CustomDialog.OnClickListener() {
            @Override
            public boolean onClick(CustomDialog dialog, View v, int btn) {
                Location loc = new Location(TAG);
                loc.setLatitude(MainActivity.cartridgeFile.latitude);
                loc.setLongitude(MainActivity.cartridgeFile.longitude);
                Guide guide = new Guide(MainActivity.cartridgeFile.name, loc);
                A.getGuidingContent().guideStart(guide);
                MainActivity.callGudingScreen(CartridgeDetailsActivity.this);
                CartridgeDetailsActivity.this.finish();
                return true;
            }
        });
    }
}
