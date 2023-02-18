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

import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.guide.Guide;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.UtilsFormat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

        TextView tvName = (TextView) findViewById(R.id.layoutDetailsTextViewName);
        tvName.setText(MainActivity.cartridgeFile.name);

        TextView tvState = (TextView) findViewById(R.id.layoutDetailsTextViewState);
        tvState.setText(getString(R.string.author) + ": " + MainActivity.cartridgeFile.author);

        TextView tvDescription = (TextView) findViewById(R.id.layoutDetailsTextViewDescription);
        tvDescription.setText(UtilsGUI.simpleHtml(MainActivity.cartridgeFile.description));

        ImageView ivImage = (ImageView) findViewById(R.id.mediaImageView);
        try {
            byte[] data = MainActivity.cartridgeFile.getFile(MainActivity.cartridgeFile.splashId);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            bmp = Images.resizeBitmap(bmp);
            ivImage.setImageBitmap(bmp);
            ivImage.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        TextView tvText = (TextView) findViewById(R.id.mediaTextView);
        tvText.setVisibility(View.GONE);

        TextView tvDistance = (TextView) findViewById(R.id.layoutDetailsTextViewDistance);

        Location loc = new Location(TAG);
        loc.setLatitude(MainActivity.cartridgeFile.latitude);
        loc.setLongitude(MainActivity.cartridgeFile.longitude);

        String buff = getString(R.string.distance) + ": " + "<b>" +
                UtilsFormat.formatDistance(LocationState.getLocation().distanceTo(loc), false) +
                "</b>" + "<br />" + getString(R.string.latitude) + ": " +
                UtilsFormat.formatLatitude(MainActivity.cartridgeFile.latitude) + "<br />" +
                getString(R.string.longitude) + ": " +
                UtilsFormat.formatLongitude(MainActivity.cartridgeFile.longitude);

        tvDistance.setText(Html.fromHtml(buff));

        CustomDialog.setBottom(this, getString(R.string.start), (dialog, v, btn) -> {
            finish();
            MainActivity.startSelectedCartridge(false);
            return true;
        }, null, null, getString(R.string.navigate), (dialog, v, btn) -> {
            Location loc1 = new Location(TAG);
            loc1.setLatitude(MainActivity.cartridgeFile.latitude);
            loc1.setLongitude(MainActivity.cartridgeFile.longitude);
            Guide guide = new Guide(MainActivity.cartridgeFile.name, loc1);
            A.getGuidingContent().guideStart(guide);
            Intent intent = new Intent(CartridgeDetailsActivity.this, GuidingActivity.class);
            startActivity(intent);
            finish();
            return true;
        });
    }
}
