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

package menion.android.whereyougo.gui;

import java.util.ArrayList;
import java.util.Vector;

import locus.api.android.ActionDisplay.ExtraAction;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionTools;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.RequiredVersionMissingException;
import locus.api.objects.extra.ExtraStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.ColorStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.Units;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Track;
import locus.api.objects.extra.Waypoint;
import menion.android.whereyougo.Main;
import menion.android.whereyougo.R;
import menion.android.whereyougo.WUI;
import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.hardware.location.LocationEventListener;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.hardware.location.SatellitePosition;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.UtilsFormat;

import org.mapsforge.applications.android.advancedmapviewer.container.MapPoint;
import org.mapsforge.applications.android.advancedmapviewer.container.PackMapPoints;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
public class Details extends CustomActivity implements Refreshable, LocationEventListener {

  private static final String TAG = "Details";

  public static EventTable et;

  private static final String[] taskStates = {Loc.get(R.string.pending),
      Loc.get(R.string.finished), Loc.get(R.string.failed)};

  private TextView tvName;
  private ImageView ivImage;
  private TextView tvImageText;
  private TextView tvDescription;
  private TextView tvDistance;
  private TextView tvState;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.layout_details);
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
      Details.this.finish();
    }

    refresh();
  }

  @Override
  public void refresh() {
    runOnUiThread(new Runnable() {

      @Override
      public void run() {
        if (!stillValid()) {
          Logger.d(TAG, "refresh(), not valid anymore");
          Details.this.finish();
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
            Main.setBitmapToImageView(i, ivImage);
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
      validActions = ListActions.getValidActions(t);
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
            Main.callGudingScreen(Details.this);
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
          switch (SettingValues.GLOBAL_MAP_PROVIDER) {
            case Settings.VALUE_MAP_PROVIDER_VECTOR:
              vectorMap();
              break;
            case Settings.VALUE_MAP_PROVIDER_LOCUS:
              locusMap();
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
            ListActions.reset((Thing) et);
            Main.wui.showScreen(WUI.SCREEN_ACTIONS, et);
            Details.this.finish();
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
                ListActions.reset((Thing) et);
                ListActions.callAction(action);
                Details.this.finish();
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
                ListActions.reset((Thing) et);
                ListActions.callAction(action);
                Details.this.finish();
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
                ListActions.reset((Thing) et);
                ListActions.callAction(action);
                Details.this.finish();
                return true;
              }
            };
          }
        } else {
          btn03 = "Actions (" + actions + ")";
          btn03Click = new CustomDialog.OnClickListener() {
            @Override
            public boolean onClick(CustomDialog dialog, View v, int btn) {
              ListActions.reset((Thing) et);
              Main.wui.showScreen(WUI.SCREEN_ACTIONS, et);
              Details.this.finish();
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

  private void enableGuideOnEventTable() {
    Waypoint wpt = getTargetWaypoint();
    if (wpt != null) {
      A.getGuidingContent().guideStart(wpt);
    } else {
      Logger.d(TAG, "enableGuideOnEventTable(), waypoint 'null'");
    }
  }

  private void vectorMap() {
    if (et == null || !et.isLocated()) {
      Logger.d(TAG, "enableGuideOnEventTable(), waypoint 'null'");
      return;
    }
    ArrayList<PackMapPoints> packs = new ArrayList<PackMapPoints>();
    if (et instanceof Zone) {
      Zone z = ((Zone) et);
      PackMapPoints border = new PackMapPoints();
      border.setPolygon(true);
      for (int i = 0; i < z.points.length; i++) {
        border.getPoints().add(new MapPoint("", z.points[i].latitude, z.points[i].longitude));
      }
      if (border.getPoints().size() >= 3)
        border.getPoints().add(border.getPoints().get(0));
      packs.add(border);

      PackMapPoints pack = new PackMapPoints();
      pack.getPoints().add(new MapPoint(z.name, z.nearestPoint.latitude, z.nearestPoint.longitude));
      packs.add(pack);
    } else {
      PackMapPoints pack = new PackMapPoints();
      pack.getPoints().add(new MapPoint(et.name, et.position.latitude, et.position.longitude));
      packs.add(pack);
    }

    Intent intent =
        new Intent(this,
            org.mapsforge.applications.android.advancedmapviewer.AdvancedMapViewer.class);
    intent.putParcelableArrayListExtra("packs", packs);
    intent.putExtra("center", true);
    startActivity(intent);
  }

  private void locusMap() {
    Waypoint wpt = getTargetWaypoint();
    Track track = getTargetTrack();
    try {
      if (track != null) {
        ArrayList<Track> tracks = new ArrayList<Track>();
        tracks.add(track);
        ActionDisplayTracks.sendTracks(Details.this, tracks, ExtraAction.CENTER);
      }
      if (wpt != null) {
        ActionTools.actionStartGuiding(Details.this, wpt);
      } else {
        Logger.d(TAG, "enableGuideOnEventTable(), waypoint 'null'");
      }
    } catch (RequiredVersionMissingException e) {
      Logger.e(TAG, "btn02.click() - missing locus version", e);
      LocusUtils.callInstallLocus(Details.this);
    } catch (Exception e) {
      Logger.e(TAG, "btn02.click() - unknown problem", e);
    }
  }

  private Waypoint getTargetWaypoint() {
    if (et == null || !et.isLocated())
      return null;

    if (et instanceof Zone) {
      Zone z = ((Zone) et);
      Location loc = new Location(TAG);
      loc.setLatitude(z.nearestPoint.latitude);
      loc.setLongitude(z.nearestPoint.longitude);
      return new Waypoint(et.name, loc);
    } else {
      Location loc = new Location(TAG);
      loc.setLatitude(et.position.latitude);
      loc.setLongitude(et.position.longitude);
      return new Waypoint(et.name, loc);
    }
  }

  private Track getTargetTrack() {
    if (et == null || !et.isLocated())
      return null;

    if (et instanceof Zone) {
      Zone z = ((Zone) et);

      ArrayList<Location> locs = new ArrayList<Location>();
      for (int i = 0; i < z.points.length; i++) {
        Location loc = new Location(TAG);
        loc.setLatitude(z.points[i].latitude);
        loc.setLongitude(z.points[i].longitude);
        locs.add(loc);
      }
      if (locs.size() >= 3)
        locs.add(locs.get(0));

      Track track = new Track();
      ExtraStyle style = new ExtraStyle();
      style.setLineStyle(ColorStyle.SIMPLE, Color.MAGENTA, 2.0f, Units.PIXELS);
      track.styleNormal = style;
      track.setPoints(locs);

      return track;
    } else {
      return null;
    }
  }

  public void onStart() {
    super.onStart();
    if (et instanceof Zone)
      LocationState.addLocationChangeListener(this);
  }

  public void onStop() {
    super.onStop();
    LocationState.removeLocationChangeListener(this);
  }

  public void onLocationChanged(Location location) {
    refresh();
  }

  public void onStatusChanged(String provider, int state, Bundle extras) {}

  public void onGpsStatusChanged(int event, ArrayList<SatellitePosition> sats) {}

  public int getPriority() {
    return LocationEventListener.PRIORITY_MEDIUM;
  }

  @Override
  public boolean isRequired() {
    return false;
  }

  @Override
  public String getName() {
    return TAG;
  }
}
