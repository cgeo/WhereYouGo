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

import java.util.ArrayList;
import java.util.Vector;

import locus.api.android.ActionDisplay.ExtraAction;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionTools;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.RequiredVersionMissingException;
import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.ILocationEventListener;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.geo.location.SatellitePosition;
import menion.android.whereyougo.gui.IRefreshable;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.guide.Guide;
import menion.android.whereyougo.maps.utils.LocusMapDataProvider;
import menion.android.whereyougo.maps.utils.VectorMapDataProvider;
import menion.android.whereyougo.openwig.WUI;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.UtilsFormat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cz.matejcik.openwig.Action;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Media;
import cz.matejcik.openwig.Task;
import cz.matejcik.openwig.Thing;
import cz.matejcik.openwig.Zone;

// ADD locationListener to update UpdateNavi
public class DetailsActivity extends CustomActivity implements IRefreshable, ILocationEventListener {

  private static final String TAG = "Details";

  public static EventTable et;

  private static final String[] taskStates = {Locale.get(R.string.pending),
      Locale.get(R.string.finished), Locale.get(R.string.failed)};

  private static Location extractLocation(EventTable et) {
    if (et == null || !et.isLocated())
      return null;

    Location loc = new Location(TAG);
    if (et instanceof Zone) {
      Zone z = ((Zone) et);
      loc.setLatitude(z.nearestPoint.latitude);
      loc.setLongitude(z.nearestPoint.longitude);
    } else {
      loc.setLatitude(et.position.latitude);
      loc.setLongitude(et.position.longitude);
    }
    return loc;
  }

  private TextView tvName;
  private ImageView ivImage;
  private TextView tvImageText;
  private TextView tvDescription;
  private TextView tvDistance;

  private TextView tvState;

  private void enableGuideOnEventTable() {
    Location loc = extractLocation(et);
    if (loc != null) {
      A.getGuidingContent().guideStart(new Guide(et.name, loc));
    } else {
      Logger.d(TAG, "enableGuideOnEventTable(), waypoint 'null'");
    }
  }

  @Override
  public String getName() {
    return TAG;
  }

  public int getPriority() {
    return ILocationEventListener.PRIORITY_MEDIUM;
  }

  @Override
  public boolean isRequired() {
    return false;
  }

  private void locusMap() {
    if (et == null || !et.isLocated())
      return;
    LocusMapDataProvider mdp = LocusMapDataProvider.getInstance();
    mdp.clear();
    mdp.addAll();
    try {
      locus.api.objects.extra.Location loc = new locus.api.objects.extra.Location(TAG);
      if (et instanceof Zone) {
        Zone z = ((Zone) et);
        loc.setLatitude(z.nearestPoint.latitude);
        loc.setLongitude(z.nearestPoint.longitude);
      } else {
        loc.setLatitude(et.position.latitude);
        loc.setLongitude(et.position.longitude);
      }
      locus.api.objects.extra.Waypoint wpt = new locus.api.objects.extra.Waypoint(et.name, loc);
      ActionDisplayTracks.sendTracks(this, mdp.getTracks(), ExtraAction.CENTER);
      // ActionDisplayTracks.sendTracksSilent(activity, tracks, true);
      ActionTools.actionStartGuiding(this, wpt);
    } catch (RequiredVersionMissingException e) {
      Logger.e(TAG, "btn02.click() - missing locus version", e);
      LocusUtils.callInstallLocus(this);
    } catch (Exception e) {
      Logger.e(TAG, "btn02.click() - unknown problem", e);
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (A.getMain() == null || Engine.instance == null) {
      finish();
      return;
    }
    setContentView(R.layout.layout_details);
  }

  public void onGpsStatusChanged(int event, ArrayList<SatellitePosition> sats) {}

  public void onLocationChanged(Location location) {
    refresh();
  }

  public void onResume() {
    super.onResume();
    Logger.d(TAG, "onResume(), et:" + et);
    if (et != null) {
      setTitle(et.name);

      tvName = (TextView) findViewById(R.id.layoutDetailsTextViewName);
      tvState = (TextView) findViewById(R.id.layoutDetailsTextViewState);
      tvDescription = (TextView) findViewById(R.id.layoutDetailsTextViewDescription);
      ivImage = (ImageView) findViewById(R.id.layoutDetailsImageViewImage);
      tvImageText = (TextView) findViewById(R.id.layoutDetailsTextViewImageText);
      tvDistance = (TextView) findViewById(R.id.layoutDetailsTextViewDistance);
    } else {
      Logger.i(TAG, "onCreate(), et == null, end!");
      DetailsActivity.this.finish();
    }

    refresh();
  }

  public void onStart() {
    super.onStart();
    if (et instanceof Zone)
      LocationState.addLocationChangeListener(this);
  }

  public void onStatusChanged(String provider, int state, Bundle extras) {}

  public void onStop() {
    super.onStop();
    LocationState.removeLocationChangeListener(this);
  }

  @Override
  public void refresh() {
    runOnUiThread(new Runnable() {

      @Override
      public void run() {
        if (!stillValid()) {
          Logger.d(TAG, "refresh(), not valid anymore");
          DetailsActivity.this.finish();
          return;
        }

        tvName.setText(et.name);
        tvDescription.setText(et.description);

        Media m = (Media) et.table.rawget("Media");
        if (m != null) {
          tvImageText.setText(m.altText);
          // Logger.w(TAG, "SET: " + et.name + ", " + m.id);
          try {
            byte[] is = Engine.mediaFile(m);
            Bitmap i = BitmapFactory.decodeByteArray(is, 0, is.length);
            MainActivity.setBitmapToImageView(i, ivImage);
          } catch (Exception e) {
            Logger.e(TAG, "refresh()", e);
          }
        } else {
          ivImage.setImageBitmap(null);
          ivImage.setMinimumWidth(0);
          ivImage.setMinimumHeight(0);
        }

        updateNavi();
        setBottomMenu();
      }
    });
  }

  private void setBottomMenu() {
    String btn01 = null, btn02 = null, btn03 = null;
    CustomDialog.OnClickListener btn01Click = null, btn02Click = null, btn03Click = null;

    // get count of items
    boolean location = et.isLocated();

    int actions = 0;
    Vector<Object> validActions = null;

    if (et instanceof Thing) {
      Thing t = (Thing) et;
      actions = t.visibleActions() + Engine.instance.cartridge.visibleUniversalActions();
      Logger.d(TAG, "actions:" + actions);
      validActions = ListActionsActivity.getValidActions(t);
      actions = validActions.size();
      Logger.d(TAG, "validActions:" + actions);
    }

    Logger.d(TAG, "setBottomMenu(), loc:" + et.isLocated() + ", et:" + et + ", act:" + actions);

    // set location on first two buttons
    if (location) {
      btn01 = getString(R.string.navigate);
      btn01Click = new CustomDialog.OnClickListener() {
        @Override
        public boolean onClick(CustomDialog dialog, View v, int btn) {
          try {
            enableGuideOnEventTable();
            MainActivity.callGudingScreen(DetailsActivity.this);
            DetailsActivity.this.finish();
          } catch (Exception e) {
            Logger.w(TAG, "btn01.click() - unknown problem");
          }
          return true;
        }
      };

      btn02 = getString(R.string.map);
      btn02Click = new CustomDialog.OnClickListener() {

        @Override
        public boolean onClick(CustomDialog dialog, View v, int btn) {
          switch (Preferences.GLOBAL_MAP_PROVIDER) {
            case PreferenceValues.VALUE_MAP_PROVIDER_VECTOR:
              vectorMap();
              DetailsActivity.this.finish();
              break;
            case PreferenceValues.VALUE_MAP_PROVIDER_LOCUS:
              locusMap();
              DetailsActivity.this.finish();
              break;
          }
          return true;
        }
      };
    }

    // set actions
    if (actions > 0) {
      if (location) {
        // only one empty button, set actions on it
        btn03 = "Actions (" + actions + ")";
        btn03Click = new CustomDialog.OnClickListener() {
          @Override
          public boolean onClick(CustomDialog dialog, View v, int btn) {
            ListActionsActivity.reset((Thing) et);
            MainActivity.wui.showScreen(WUI.SCREEN_ACTIONS, et);
            DetailsActivity.this.finish();
            return true;
          }
        };
      } else {
        // all three buttons free
        if (actions <= 3) {
          if (actions > 0) {
            final Action action = (Action) validActions.get(0);
            btn01 = action.text;
            btn01Click = new CustomDialog.OnClickListener() {
              @Override
              public boolean onClick(CustomDialog dialog, View v, int btn) {
                ListActionsActivity.reset((Thing) et);
                ListActionsActivity.callAction(action);
                DetailsActivity.this.finish();
                return true;
              }
            };
          }
          if (actions > 1) {
            final Action action = (Action) validActions.get(1);
            btn02 = action.text;
            btn02Click = new CustomDialog.OnClickListener() {
              @Override
              public boolean onClick(CustomDialog dialog, View v, int btn) {
                ListActionsActivity.reset((Thing) et);
                ListActionsActivity.callAction(action);
                DetailsActivity.this.finish();
                return true;
              }
            };
          }
          if (actions > 2) {
            final Action action = (Action) validActions.get(2);
            btn03 = action.text;
            btn03Click = new CustomDialog.OnClickListener() {
              @Override
              public boolean onClick(CustomDialog dialog, View v, int btn) {
                ListActionsActivity.reset((Thing) et);
                ListActionsActivity.callAction(action);
                DetailsActivity.this.finish();
                return true;
              }
            };
          }
        } else {
          btn03 = "Actions (" + actions + ")";
          btn03Click = new CustomDialog.OnClickListener() {
            @Override
            public boolean onClick(CustomDialog dialog, View v, int btn) {
              ListActionsActivity.reset((Thing) et);
              MainActivity.wui.showScreen(WUI.SCREEN_ACTIONS, et);
              DetailsActivity.this.finish();
              return true;
            }
          };
        }
      }
    }

    // show bottom menu
    CustomDialog.setBottom(this, btn01, btn01Click, btn02, btn02Click, btn03, btn03Click);

    // set title text
    if (et instanceof Task) {
      Task t = (Task) et;
      tvState.setText(taskStates[t.state()]);
    }
  }

  public boolean stillValid() {
    if (et != null) {
      if (et instanceof Thing) {
        return ((Thing) et).visibleToPlayer();
      }
      return et.isVisible();
    } else
      return false;
  }

  private void updateNavi() {
    if (!(et instanceof Zone)) {
      return;
    }

    Zone z = (Zone) et;
    String ss = "(nothing)";
    switch (z.contain) {
      case Zone.DISTANT:
        ss = "distant";
        break;
      case Zone.PROXIMITY:
        ss = "near";
        break;
      case Zone.INSIDE:
        ss = "inside";
        break;
    }
    tvState.setText("State: " + ss);

    if (z.contain == Zone.INSIDE) {
      tvDistance.setText("Distance: inside");
    } else {
      tvDistance.setText("Distance: " + UtilsFormat.formatDistance(z.distance, false));
    }
  }

  // show vector map
  private void vectorMap() {
    VectorMapDataProvider mdp = VectorMapDataProvider.getInstance();
    mdp.clear();
    mdp.addAll();
    MainActivity.wui.showScreen(WUI.SCREEN_MAP, null);
    //MainActivity.wui.showMap(true, true);
  }
  
}
