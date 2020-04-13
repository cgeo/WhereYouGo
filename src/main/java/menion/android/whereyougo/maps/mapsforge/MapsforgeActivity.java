/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013, 2014 biylda <biylda@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package menion.android.whereyougo.maps.mapsforge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.mapsforge.android.AndroidUtils;
import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapScaleBar;
import org.mapsforge.android.maps.MapScaleBar.TextField;
import org.mapsforge.android.maps.MapViewPosition;
import org.mapsforge.android.maps.mapgenerator.MapGenerator;
import org.mapsforge.android.maps.mapgenerator.TileCache;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.TileDownloader;
import org.mapsforge.android.maps.overlay.Circle;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.mapsforge.map.reader.header.MapFileInfo;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.IRefreshable;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.maps.container.MapPoint;
import menion.android.whereyougo.maps.container.MapPointPack;
import menion.android.whereyougo.maps.mapsforge.filefilter.FilterByFileExtension;
import menion.android.whereyougo.maps.mapsforge.filefilter.ValidMapFile;
import menion.android.whereyougo.maps.mapsforge.filefilter.ValidRenderTheme;
import menion.android.whereyougo.maps.mapsforge.filepicker.FilePicker;
import menion.android.whereyougo.maps.mapsforge.mapgenerator.MapGeneratorFactory;
import menion.android.whereyougo.maps.mapsforge.mapgenerator.MapGeneratorInternal;
import menion.android.whereyougo.maps.mapsforge.overlay.LabelMarker;
import menion.android.whereyougo.maps.mapsforge.overlay.MyLocationOverlay;
import menion.android.whereyougo.maps.mapsforge.overlay.NavigationOverlay;
import menion.android.whereyougo.maps.mapsforge.overlay.PointListOverlay;
import menion.android.whereyougo.maps.mapsforge.overlay.PointOverlay;
import menion.android.whereyougo.maps.mapsforge.overlay.RotationMarker;
import menion.android.whereyougo.maps.mapsforge.overlay.SensorMyLocationOverlay;
import menion.android.whereyougo.maps.mapsforge.preferences.EditPreferences;
import menion.android.whereyougo.maps.utils.VectorMapDataProvider;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.CgeoUtils;
import menion.android.whereyougo.utils.UtilsFormat;

/**
 * A map application which uses the features from the mapsforge map library. The map can be centered
 * to the current location. A simple file browser for selecting the map file is also included. Some
 * preferences can be adjusted via the {@link EditPreferences} activity and screenshots of the map
 * may be taken in different image formats.
 */
public class MapsforgeActivity extends MapActivity implements IRefreshable {
    /**
     * The default number of tiles in the file system cache.
     */
    public static final int FILE_SYSTEM_CACHE_SIZE_DEFAULT = 250;

    /**
     * The maximum number of tiles in the file system cache.
     */
    public static final int FILE_SYSTEM_CACHE_SIZE_MAX = 500;

    /**
     * The default move speed factor of the map.
     */
    public static final int MOVE_SPEED_DEFAULT = 10;

    /**
     * The maximum move speed factor of the map.
     */
    public static final int MOVE_SPEED_MAX = 30;

    /**
     * The maximum icon size.
     */
    private static final int ICON_SIZE_MAX = 32;

    private static final String KEY_MAP_GENERATOR = "mapGenerator"; // store map generator
    public static final String BUNDLE_CENTER = "center";
    public static final String BUNDLE_NAVIGATE = "navigate";
    public static final String BUNDLE_ITEMS = "items";
    public static final String BUNDLE_ALLOW_START_CARTRIDGE = "allowStartCartridge";
    private static final String BUNDLE_CENTER_AT_FIRST_FIX = "centerAtFirstFix";
    private static final String BUNDLE_SHOW_MY_LOCATION = "showMyLocation";
    private static final String BUNDLE_SNAP_TO_LOCATION = "snapToLocation";
    private static final String BUNDLE_SHOW_PINS = "showPins";
    private static final String BUNDLE_SHOW_LABELS = "showLabels";
    private static final int DIALOG_ENTER_COORDINATES = 0;
    private static final int DIALOG_INFO_MAP_FILE = 1;
    private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;
    private static final FileFilter FILE_FILTER_EXTENSION_MAP = new FilterByFileExtension(".map");
    private static final FileFilter FILE_FILTER_EXTENSION_XML = new FilterByFileExtension(".xml");
    private static final int SELECT_MAP_FILE = 0;
    private static final int SELECT_RENDER_THEME_FILE = 1;
    private final Object lock = new Object();
    MyMapView mapView;
    private MapGeneratorInternal mapGeneratorInternal = MapGeneratorInternal.BLANK;
    private PointListOverlay listOverlay;
    private MyLocationOverlay myLocationOverlay;
    private NavigationOverlay navigationOverlay;
    private ScreenshotCapturer screenshotCapturer;
    private ToggleButton snapToLocationView;
    private Menu menu;
    private WakeLock wakeLock;
    private double itemsLatitude, itemsLongitude;
    private boolean showPins = true;
    private boolean showLabels = true;
    private boolean allowStartCartridge = false;
    private final TapEventListener tapListener = new TapEventListener() {
        @Override
        public void onTap(final PointOverlay pointOverlay) {
            if (pointOverlay.getPoint() == null)
                return;
            // final MapPoint p = pointOverlay.getPoint();
            //MapsforgeActivity.this.navigationOverlay.setTarget(pointOverlay.getGeoPoint());
            TextView textView = (TextView) View.inflate(MapsforgeActivity.this, R.layout.point_detail_view, null);
            textView.setText(UtilsFormat.formatGeoPoint(pointOverlay.getGeoPoint()) + "\n\n"
                    + Html.fromHtml(pointOverlay.getDescription()));

            AlertDialog.Builder builder = new AlertDialog.Builder(MapsforgeActivity.this)
                    .setTitle(pointOverlay.getLabel())
                    .setView(textView)
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            final String cguid = pointOverlay.getPoint().getData();
            if (allowStartCartridge && cguid != null)
                builder.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MapsforgeActivity.this, MainActivity.class);
                        intent.putExtra("cguid", cguid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        MapsforgeActivity.this.finish();
                    }
                });
            builder.show();
        }
    };

    private static Polyline createPolyline(List<GeoPoint> geoPoints) {
        PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
        Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.MAGENTA);
        paintStroke.setStrokeWidth(4);

        return new Polyline(polygonalChain, paintStroke);
    }

    private void configureMapView() {
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setClickable(true);
        this.mapView.setFocusable(true);

        MapScaleBar mapScaleBar = this.mapView.getMapScaleBar();
        mapScaleBar.setText(TextField.KILOMETER, getString(R.string.unit_symbol_kilometer));
        mapScaleBar.setText(TextField.METER, getString(R.string.unit_symbol_meter));
    }

    private Circle createCircle(GeoPoint geoPoint) {
        Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.BLUE);
        // paintFill.setAlpha(64);

        Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.DKGRAY);
        // paintStroke.setAlpha(128);
        paintStroke.setStrokeWidth(3);

        return new Circle(geoPoint, 0, paintFill, paintStroke);
    }

    private void disableShowMyLocation() {
        if (this.myLocationOverlay.isMyLocationEnabled()) {
            this.myLocationOverlay.disableMyLocation();
            disableSnapToLocation(false);
            this.snapToLocationView.setVisibility(View.GONE);
        }
    }

    /**
     * @param showToast defines whether a toast message is displayed or not.
     */
    private void disableSnapToLocation(boolean showToast) {
        if (this.myLocationOverlay.isSnapToLocationEnabled()) {
            this.myLocationOverlay.setSnapToLocationEnabled(false);
            this.snapToLocationView.setChecked(false);
            this.mapView.setClickable(true);
            if (showToast) {
                showToastOnUiThread(getString(R.string.snap_to_location_disabled));
            }
        }
    }

    /**
     * Enables the "show my location" mode.
     *
     * @param centerAtFirstFix defines whether the map should be centered to the first fix.
     */
    private void enableShowMyLocation(boolean centerAtFirstFix) {
        if (!this.myLocationOverlay.isMyLocationEnabled()) {
            if (!this.myLocationOverlay.enableMyLocation(centerAtFirstFix)) {
                showDialog(DIALOG_LOCATION_PROVIDER_DISABLED);
                return;
            }

            this.mapView.getOverlays().add(this.navigationOverlay);
            this.mapView.getOverlays().add(this.myLocationOverlay);
            this.snapToLocationView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param showToast defines whether a toast message is displayed or not.
     */
    private void enableSnapToLocation(boolean showToast) {
        if (!this.myLocationOverlay.isSnapToLocationEnabled()) {
            this.myLocationOverlay.setSnapToLocationEnabled(true);
            this.snapToLocationView.setChecked(true);
            this.mapView.setClickable(false);
            if (showToast) {
                showToastOnUiThread(getString(R.string.snap_to_location_enabled));
            }
        }
    }

    /**
     * Centers the map to the last known position as reported by the most accurate location provider.
     * If the last location is unknown, a toast message is displayed instead.
     */
    private void gotoLastKnownPosition() {
        Location bestLocation = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        for (String provider : locationManager.getProviders(true)) {
            Location currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null)
                continue;
            if (bestLocation == null || bestLocation.getAccuracy() > currentLocation.getAccuracy()) {
                bestLocation = currentLocation;
            }
        }

        // check if a location has been found
        if (bestLocation != null) {
            GeoPoint geoPoint = new GeoPoint(bestLocation.getLatitude(), bestLocation.getLongitude());
            this.mapView.getMapViewPosition().setCenter(geoPoint);
        } else {
            showToastOnUiThread(getString(R.string.error_last_location_unknown));
        }
    }

    private void invertSnapToLocation() {
        if (this.myLocationOverlay.isSnapToLocationEnabled()) {
            disableSnapToLocation(true);
        } else {
            enableSnapToLocation(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SELECT_MAP_FILE) {
            if (resultCode == RESULT_OK) {
                disableSnapToLocation(true);
                if (intent != null && intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
                    this.mapView.setMapFile(new File(intent.getStringExtra(FilePicker.SELECTED_FILE)));
                    setMapGenerator(MapGeneratorInternal.DATABASE_RENDERER);
                }
            } else if (resultCode == RESULT_CANCELED && this.mapView.getMapFile() == null) {
                // finish();
            }
        } else if (requestCode == SELECT_RENDER_THEME_FILE && resultCode == RESULT_OK && intent != null
                && intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
            try {
                this.mapView.setRenderTheme(new File(intent.getStringExtra(FilePicker.SELECTED_FILE)));
            } catch (FileNotFoundException e) {
                showToastOnUiThread(e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.screenshotCapturer = new ScreenshotCapturer(this);
        this.screenshotCapturer.start();

        setContentView(R.layout.activity_mapsforge);
        this.mapView = (MyMapView) findViewById(R.id.mapView);
        configureMapView();

        this.snapToLocationView = (ToggleButton) findViewById(R.id.snapToLocationView);
        this.snapToLocationView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                invertSnapToLocation();
            }
        });

        Drawable drawable = getResources().getDrawable(R.drawable.my_location_chevron);
        drawable = Marker.boundCenter(drawable);
        this.myLocationOverlay =
                new SensorMyLocationOverlay(this, this.mapView, new RotationMarker(null, drawable));
        this.navigationOverlay = new NavigationOverlay(this.myLocationOverlay);
        this.listOverlay = new PointListOverlay();
        this.listOverlay.registerOnTapEvent(tapListener);

    /* what is shown */
        if (savedInstanceState != null) {
            this.showPins = savedInstanceState.getBoolean(BUNDLE_SHOW_PINS, true);
            this.showLabels = savedInstanceState.getBoolean(BUNDLE_SHOW_LABELS, true);
            this.allowStartCartridge = savedInstanceState.getBoolean(BUNDLE_ALLOW_START_CARTRIDGE, false);
        } else {
            this.showPins = sharedPreferences.getBoolean(BUNDLE_SHOW_PINS, true);
            this.showLabels = sharedPreferences.getBoolean(BUNDLE_SHOW_LABELS, true);
        }
        mapView.getOverlays().add(listOverlay);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AMV");

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BUNDLE_SHOW_MY_LOCATION, true)) {
                enableShowMyLocation(savedInstanceState.getBoolean(BUNDLE_CENTER_AT_FIRST_FIX, false));
                if (savedInstanceState.getBoolean(BUNDLE_SNAP_TO_LOCATION, false)) {
                    enableSnapToLocation(false);
                }
            }
        } else {
            if (sharedPreferences.getBoolean(BUNDLE_SHOW_MY_LOCATION, true)) {
                enableShowMyLocation(sharedPreferences.getBoolean(BUNDLE_CENTER_AT_FIRST_FIX, false));
                if (sharedPreferences.getBoolean(BUNDLE_SNAP_TO_LOCATION, false)) {
                    enableSnapToLocation(false);
                }
            }
        }

        ToggleButton showPinsButton = ((ToggleButton) findViewById(R.id.showPinsView));
        showPinsButton.setChecked(this.showPins);
        showPinsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPins = ((ToggleButton) v).isChecked();
                visibilityChanged();
            }
        });
        ToggleButton showLabelsButton = ((ToggleButton) findViewById(R.id.showLabelsView));
        showLabelsButton.setChecked(this.showLabels);
        showLabelsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showLabels = ((ToggleButton) v).isChecked();
                visibilityChanged();
            }
        });

        // restore map generator
        try {
            MapGeneratorInternal type =
                    MapGeneratorInternal.valueOf(sharedPreferences.getString(KEY_MAP_GENERATOR, getString(R.string.mapgenerator_default)));
            setMapGenerator(type);
        } catch (Exception e) {
            setMapGenerator(MapGeneratorInternal.valueOf(getString(R.string.mapgenerator_default)));
        }

        // check if offline map file is set
        checkOfflineMapFile(false);

        // add items received via Intent or from provider
        refreshItems();
    }

    private void checkOfflineMapFile(final boolean forceAndFeedback) {
        // if no local mapFile is set: query c:geo for current mapFile
        if (forceAndFeedback || this.mapView.getMapFile() == null) {
            try {
                final Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setComponent(new ComponentName(getString(R.string.cgeo_package), getString(R.string.cgeo_action_queryMapFile)));
                intent.putExtra(getString(R.string.cgeo_queryMapFile_actionParam), forceAndFeedback);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                if (forceAndFeedback) {
                    Toast.makeText(this, R.string.receivemapfile_cgeonotfound, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Deprecated
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (id == DIALOG_ENTER_COORDINATES) {
            builder.setIcon(android.R.drawable.ic_menu_mylocation);
            builder.setTitle(R.string.menu_position_enter_coordinates);
            LayoutInflater factory = LayoutInflater.from(this);
            switch (Preferences.FORMAT_COO_LATLON) {
                case PreferenceValues.VALUE_UNITS_COO_LATLON_SEC: {
                    final View view = factory.inflate(R.layout.dialog_enter_coordinates_dms, null);
                    builder.setView(view);
                    builder.setPositiveButton(R.string.go_to_position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // disable GPS follow mode if it is enabled
                            disableSnapToLocation(true);
                            // set the map center and zoom level
                            double latitude_d = Double.parseDouble(((EditText) view.findViewById(R.id.latitude_d)).getText().toString());
                            double latitude_m = Double.parseDouble(((EditText) view.findViewById(R.id.latitude_m)).getText().toString());
                            double latitude_s = Double.parseDouble(((EditText) view.findViewById(R.id.latitude_s)).getText().toString());
                            double latitude = latitude_d + latitude_m / 60 + latitude_s / 3600;
                            double longitude_d = Double.parseDouble(((EditText) view.findViewById(R.id.longitude_d)).getText().toString());
                            double longitude_m = Double.parseDouble(((EditText) view.findViewById(R.id.longitude_m)).getText().toString());
                            double longitude_s = Double.parseDouble(((EditText) view.findViewById(R.id.longitude_s)).getText().toString());
                            double longitude = longitude_d + longitude_m / 60 + longitude_s / 3600;
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            SeekBar zoomLevelView = (SeekBar) view.findViewById(R.id.zoomLevel);
                            MapPosition newMapPosition =
                                    new MapPosition(geoPoint, (byte) zoomLevelView.getProgress());
                            MapsforgeActivity.this.mapView.getMapViewPosition().setMapPosition(newMapPosition);
                        }
                    });
                }
                break;
                case PreferenceValues.VALUE_UNITS_COO_LATLON_MIN: {
                    final View view = factory.inflate(R.layout.dialog_enter_coordinates_dm, null);
                    builder.setView(view);
                    builder.setPositiveButton(R.string.go_to_position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // disable GPS follow mode if it is enabled
                            disableSnapToLocation(true);
                            // set the map center and zoom level
                            double latitude_d = Double.parseDouble(((EditText) view.findViewById(R.id.latitude_d)).getText().toString());
                            double latitude_m = Double.parseDouble(((EditText) view.findViewById(R.id.latitude_m)).getText().toString());
                            double latitude = latitude_d + latitude_m / 60;
                            double longitude_d = Double.parseDouble(((EditText) view.findViewById(R.id.longitude_d)).getText().toString());
                            double longitude_m = Double.parseDouble(((EditText) view.findViewById(R.id.longitude_m)).getText().toString());
                            double longitude = longitude_d + longitude_m / 60;
                            try {
                                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                                SeekBar zoomLevelView = (SeekBar) view.findViewById(R.id.zoomLevel);
                                MapPosition newMapPosition =
                                        new MapPosition(geoPoint, (byte) zoomLevelView.getProgress());
                                MapsforgeActivity.this.mapView.getMapViewPosition().setMapPosition(newMapPosition);
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    });
                }
                break;
                default: {
                    final View view = factory.inflate(R.layout.dialog_enter_coordinates, null);
                    builder.setView(view);
                    builder.setPositiveButton(R.string.go_to_position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // disable GPS follow mode if it is enabled
                            disableSnapToLocation(true);
                            // set the map center and zoom level
                            double latitude = Double.parseDouble(((EditText) view.findViewById(R.id.latitude)).getText().toString());
                            double longitude = Double.parseDouble(((EditText) view.findViewById(R.id.longitude)).getText().toString());
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            SeekBar zoomLevelView = (SeekBar) view.findViewById(R.id.zoomLevel);
                            MapPosition newMapPosition =
                                    new MapPosition(geoPoint, (byte) zoomLevelView.getProgress());
                            MapsforgeActivity.this.mapView.getMapViewPosition().setMapPosition(newMapPosition);
                        }
                    });
                }
                break;
            }
            builder.setNegativeButton(R.string.cancel, null);
            return builder.create();
        } else if (id == DIALOG_LOCATION_PROVIDER_DISABLED) {
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.no_location_provider_available);
            builder.setPositiveButton(R.string.ok, null);
            return builder.create();
        } else if (id == DIALOG_INFO_MAP_FILE) {
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setTitle(R.string.menu_info_map_file);
            LayoutInflater factory = LayoutInflater.from(this);
            builder.setView(factory.inflate(R.layout.dialog_info_map_file, null));
            builder.setPositiveButton(R.string.ok, null);
            return builder.create();
        } else {
            // do dialog will be created
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapsforge_menu, menu);
        this.menu = menu;
        SubMenu mapgeneratorMenu = menu.findItem(R.id.menu_mapgenerator).getSubMenu();
        String[] keys = getResources().getStringArray(R.array.mapgenerator_keys);
        String[] values = getResources().getStringArray(R.array.mapgenerator_values);
        for (int i = 0; i < keys.length; i++) {
            final MapGeneratorInternal generator = MapGeneratorInternal.valueOf(keys[i]);
            MenuItem item =
                    mapgeneratorMenu.add(R.id.menu_mapgenerator_group, Menu.NONE, Menu.NONE, values[i]);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (generator == MapGeneratorInternal.DATABASE_RENDERER
                            && MapsforgeActivity.this.mapView.getMapFile() == null) {
                        startMapFilePicker();
                    } else {
                        setMapGenerator(generator);
                        item.setChecked(true);
                    }
                    return true;
                }
            });
            if (mapGeneratorInternal != null && mapGeneratorInternal.name().equals(generator.name())) {
                item.setChecked(true);
            }
        }
        mapgeneratorMenu.setGroupCheckable(R.id.menu_mapgenerator_group, true, true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.screenshotCapturer.interrupt();
        Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        preferences.putBoolean(BUNDLE_SHOW_MY_LOCATION, this.myLocationOverlay.isMyLocationEnabled());
        preferences.putBoolean(BUNDLE_CENTER_AT_FIRST_FIX, this.myLocationOverlay.isCenterAtNextFix());
        preferences.putBoolean(BUNDLE_SNAP_TO_LOCATION,
                this.myLocationOverlay.isSnapToLocationEnabled());
        preferences.putBoolean(BUNDLE_SHOW_PINS, this.showPins);
        preferences.putBoolean(BUNDLE_SHOW_LABELS, this.showLabels);
        preferences.putString(KEY_MAP_GENERATOR, this.mapGeneratorInternal.name());
        preferences.commit();
        disableShowMyLocation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        refreshItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;
            case R.id.menu_info:
                return true;

            case R.id.menu_info_map_file:
                if (this.mapGeneratorInternal == MapGeneratorInternal.DATABASE_RENDERER
                        && this.mapView.getMapFile() != null)
                    showDialog(DIALOG_INFO_MAP_FILE);
                return true;

            case R.id.menu_info_about:
                startActivity(new Intent(this, InfoView.class));
                return true;

            case R.id.menu_position:
                return true;

            case R.id.menu_position_my_location_enable:
                enableShowMyLocation(true);
                onPrepareOptionsMenu(this.menu);
                return true;

            case R.id.menu_position_my_location_disable:
                disableShowMyLocation();
                onPrepareOptionsMenu(this.menu);
                return true;

            case R.id.menu_position_last_known:
                gotoLastKnownPosition();
                return true;

            case R.id.menu_position_target:
                GeoPoint geoPoint = navigationOverlay.getTarget();
                if (geoPoint != null)
                    this.mapView.getMapViewPosition().setCenter(geoPoint);
                return true;

            case R.id.menu_position_enter_coordinates:
                showDialog(DIALOG_ENTER_COORDINATES);
                return true;

            case R.id.menu_position_map_center:
                if (this.mapGeneratorInternal == MapGeneratorInternal.DATABASE_RENDERER) {
                    // disable GPS follow mode if it is enabled
                    disableSnapToLocation(true);
                    MapFileInfo mapFileInfo = this.mapView.getMapDatabase().getMapFileInfo();
                    this.mapView.getMapViewPosition().setCenter(mapFileInfo.boundingBox.getCenterPoint());
                }
                return true;

            case R.id.menu_screenshot:
                return true;

            case R.id.menu_screenshot_jpeg:
                this.screenshotCapturer.captureScreenshot(CompressFormat.JPEG);
                return true;

            case R.id.menu_screenshot_png:
                this.screenshotCapturer.captureScreenshot(CompressFormat.PNG);
                return true;

            case R.id.menu_preferences:
                startActivity(new Intent(this, EditPreferences.class));
                return true;

            case R.id.menu_render_theme:
                return true;

            case R.id.menu_render_theme_osmarender:
                this.mapView.setRenderTheme(InternalRenderTheme.OSMARENDER);
                return true;

            case R.id.menu_render_theme_select_file:
                startRenderThemePicker();
                return true;

            case R.id.menu_mapfile:
                startMapFilePicker();
                return true;

            case R.id.menu_acquire_from_cgeo:
                checkOfflineMapFile(true);
                return true;

            case R.id.menu_mapgenerator:
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }

        if (PreferenceValues.getCurrentActivity() == this) {
            PreferenceValues.setCurrentActivity(null);
        }
        // disable location
        MainApplication.onActivityPause();
    }

    @Deprecated
    @Override
    protected void onPrepareDialog(int id, final Dialog dialog) {
        if (id == DIALOG_ENTER_COORDINATES) {
            MapViewPosition mapViewPosition = this.mapView.getMapViewPosition();
            GeoPoint mapCenter = mapViewPosition.getCenter();
            double latitude = mapCenter.latitude;
            double longitude = mapCenter.longitude;
            switch (Preferences.FORMAT_COO_LATLON) {
                case PreferenceValues.VALUE_UNITS_COO_LATLON_SEC: {
                    int latitude_d = (int) latitude;
                    int latitude_m = (int) ((latitude - latitude_d) * 60);
                    double latitude_s = (latitude - latitude_d - latitude_m / 60) * 3600;
                    ((EditText) dialog.findViewById(R.id.latitude_d)).setText(Integer.toString(latitude_d));
                    ((EditText) dialog.findViewById(R.id.latitude_m)).setText(Integer.toString(latitude_m));
                    ((EditText) dialog.findViewById(R.id.latitude_s)).setText(Double.toString(latitude_s));
                    int longitude_d = (int) longitude;
                    int longitude_m = (int) ((longitude - longitude_d) * 60);
                    double longitude_s = (longitude - longitude_d - longitude_m / 60) * 3600;
                    ((EditText) dialog.findViewById(R.id.longitude_d)).setText(Integer.toString(longitude_d));
                    ((EditText) dialog.findViewById(R.id.longitude_m)).setText(Integer.toString(longitude_m));
                    ((EditText) dialog.findViewById(R.id.longitude_s)).setText(Double.toString(longitude_s));
                }
                break;
                case PreferenceValues.VALUE_UNITS_COO_LATLON_MIN: {
                    int latitude_d = (int) latitude;
                    double latitude_m = (latitude - latitude_d) * 60;
                    ((EditText) dialog.findViewById(R.id.latitude_d)).setText(Integer.toString(latitude_d));
                    ((EditText) dialog.findViewById(R.id.latitude_m)).setText(Double.toString(latitude_m));
                    int longitude_d = (int) longitude;
                    double longitude_m = (longitude - longitude_d) * 60;
                    ((EditText) dialog.findViewById(R.id.longitude_d)).setText(Integer.toString(longitude_d));
                    ((EditText) dialog.findViewById(R.id.longitude_m)).setText(Double.toString(longitude_m));
                }
                break;
                default: {
                    ((EditText) dialog.findViewById(R.id.latitude)).setText(Double.toString(latitude));
                    ((EditText) dialog.findViewById(R.id.longitude)).setText(Double.toString(longitude));
                }
                break;
            }

            // zoom level
            SeekBar zoomlevel = (SeekBar) dialog.findViewById(R.id.zoomLevel);
            zoomlevel.setMax(this.mapView.getDatabaseRenderer().getZoomLevelMax());
            zoomlevel.setProgress(mapViewPosition.getZoomLevel());

            // zoom level value
            final TextView textView = (TextView) dialog.findViewById(R.id.zoomlevelValue);
            textView.setText(String.valueOf(zoomlevel.getProgress()));
            zoomlevel.setOnSeekBarChangeListener(new SeekBarChangeListener(textView));
        } else if (id == DIALOG_INFO_MAP_FILE
                && this.mapGeneratorInternal == MapGeneratorInternal.DATABASE_RENDERER) {
            MapFileInfo mapFileInfo = this.mapView.getMapDatabase().getMapFileInfo();

            // map file name
            TextView textView = (TextView) dialog.findViewById(R.id.infoMapFileViewName);
            textView.setText(this.mapView.getMapFile().getAbsolutePath());

            // map file size
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewSize);
            textView.setText(FileUtils.formatFileSize(mapFileInfo.fileSize, getResources()));

            // map file version
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewVersion);
            textView.setText(String.valueOf(mapFileInfo.fileVersion));

            // map file debug
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewDebug);
            if (mapFileInfo.debugFile) {
                textView.setText(R.string.info_map_file_debug_yes);
            } else {
                textView.setText(R.string.info_map_file_debug_no);
            }

            // map file date
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewDate);
            Date date = new Date(mapFileInfo.mapDate);
            textView.setText(DateFormat.getDateTimeInstance().format(date));

            // map file area
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewArea);
            BoundingBox boundingBox = mapFileInfo.boundingBox;
            textView.setText(boundingBox.minLatitude + ", " + boundingBox.minLongitude + " – \n"
                    + boundingBox.maxLatitude + ", " + boundingBox.maxLongitude);

            // map file start position
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewStartPosition);
            GeoPoint startPosition = mapFileInfo.startPosition;
            if (startPosition == null) {
                textView.setText(null);
            } else {
                textView.setText(startPosition.latitude + ", " + startPosition.longitude);
            }

            // map file start zoom level
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewStartZoomLevel);
            Byte startZoomLevel = mapFileInfo.startZoomLevel;
            if (startZoomLevel == null) {
                textView.setText(null);
            } else {
                textView.setText(startZoomLevel.toString());
            }

            // map file language preference
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewLanguagePreference);
            textView.setText(mapFileInfo.languagePreference);

            // map file comment text
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewComment);
            textView.setText(mapFileInfo.comment);

            // map file created by text
            textView = (TextView) dialog.findViewById(R.id.infoMapFileViewCreatedBy);
            textView.setText(mapFileInfo.createdBy);
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.myLocationOverlay.isMyLocationEnabled()) {
            menu.findItem(R.id.menu_position_my_location_enable).setVisible(false);
            menu.findItem(R.id.menu_position_my_location_enable).setEnabled(false);
            menu.findItem(R.id.menu_position_my_location_disable).setVisible(true);
            menu.findItem(R.id.menu_position_my_location_disable).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_position_my_location_enable).setVisible(true);
            menu.findItem(R.id.menu_position_my_location_enable).setEnabled(true);
            menu.findItem(R.id.menu_position_my_location_disable).setVisible(false);
            menu.findItem(R.id.menu_position_my_location_disable).setEnabled(false);
        }

        if (mapGeneratorInternal == MapGeneratorInternal.DATABASE_RENDERER
                && this.mapView.getMapFile() != null) {
            menu.findItem(R.id.menu_info_map_file).setEnabled(true);
            menu.findItem(R.id.menu_position_map_center).setEnabled(true);
            menu.findItem(R.id.menu_render_theme).setEnabled(true);
            menu.findItem(R.id.menu_mapfile).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_info_map_file).setEnabled(false);
            menu.findItem(R.id.menu_position_map_center).setEnabled(false);
            menu.findItem(R.id.menu_render_theme).setEnabled(false);
            menu.findItem(R.id.menu_mapfile).setEnabled(false);
        }

        if (navigationOverlay.getTarget() == null) {
            menu.findItem(R.id.menu_position_target).setEnabled(false);
        }

        menu.findItem(R.id.menu_acquire_from_cgeo).setVisible(CgeoUtils.isInstalled(this));

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MapScaleBar mapScaleBar = this.mapView.getMapScaleBar();
        mapScaleBar.setShowMapScaleBar(sharedPreferences.getBoolean("showScaleBar", false));
        String scaleBarUnitDefault = getString(R.string.preferences_scale_bar_unit_default);
        String scaleBarUnit = sharedPreferences.getString("scaleBarUnit", scaleBarUnitDefault);
        mapScaleBar.setImperialUnits(scaleBarUnit.equals("imperial"));

        try {
            String textScaleDefault = getString(R.string.preferences_text_scale_default);
            this.mapView.setTextScale(Float.parseFloat(sharedPreferences.getString("textScale",
                    textScaleDefault)));
        } catch (NumberFormatException e) {
            this.mapView.setTextScale(1);
        }

        if (sharedPreferences.getBoolean("fullscreen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        if (sharedPreferences.getBoolean("wakeLock", false) && !this.wakeLock.isHeld()) {
            this.wakeLock.acquire();
        }

        boolean persistent = sharedPreferences.getBoolean("cachePersistence", false);
        int capacity =
                Math.min(sharedPreferences.getInt("cacheSize", FILE_SYSTEM_CACHE_SIZE_DEFAULT),
                        FILE_SYSTEM_CACHE_SIZE_MAX);
        TileCache fileSystemTileCache = this.mapView.getFileSystemTileCache();
        fileSystemTileCache.setPersistent(persistent);
        fileSystemTileCache.setCapacity(capacity);

        float moveSpeedFactor =
                Math.min(sharedPreferences.getInt("moveSpeed", MOVE_SPEED_DEFAULT), MOVE_SPEED_MAX) / 10f;
        this.mapView.getMapMover().setMoveSpeedFactor(moveSpeedFactor);

        this.mapView.getFpsCounter().setFpsCounter(
                sharedPreferences.getBoolean("showFpsCounter", false));

        boolean drawTileFrames = sharedPreferences.getBoolean("drawTileFrames", false);
        boolean drawTileCoordinates = sharedPreferences.getBoolean("drawTileCoordinates", false);
        boolean highlightWaterTiles = sharedPreferences.getBoolean("highlightWaterTiles", false);
        DebugSettings debugSettings =
                new DebugSettings(drawTileCoordinates, drawTileFrames, highlightWaterTiles);
        this.mapView.setDebugSettings(debugSettings);

        PreferenceValues.setCurrentActivity(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_SHOW_MY_LOCATION, this.myLocationOverlay.isMyLocationEnabled());
        outState.putBoolean(BUNDLE_CENTER_AT_FIRST_FIX, this.myLocationOverlay.isCenterAtNextFix());
        outState.putBoolean(BUNDLE_SNAP_TO_LOCATION, this.myLocationOverlay.isSnapToLocationEnabled());
        outState.putBoolean(BUNDLE_SHOW_PINS, this.showPins);
        outState.putBoolean(BUNDLE_SHOW_LABELS, this.showLabels);
        outState.putBoolean(BUNDLE_ALLOW_START_CARTRIDGE, this.allowStartCartridge);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        // forward the event to the MapView
        return this.mapView.onTrackballEvent(event);
    }

    @Override
    public void refresh() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                VectorMapDataProvider mdp = VectorMapDataProvider.getInstance();
                mdp.addAll();
                showMapPack(mdp.getItems());
                mapView.getOverlayController().redrawOverlays();
            }
        });
    }

    private void refreshItems() {
        Bundle bundle = getIntent().getExtras();

        // received mapfile from c:geo? (triggered by checkOfflineMapFile() above)
        if (null != bundle) {
            final String newMapfile = bundle.getString(getString(R.string.cgeo_queryMapFile_resultParam), "");
            boolean forceAndFeedback = bundle.getBoolean(getString(R.string.cgeo_queryMapFile_actionParam), false);
            if (!"".equals(newMapfile)) {
                FileOpenResult result = mapView.setMapFile(new File(newMapfile));
                if (result == FileOpenResult.SUCCESS) {
                    setMapGenerator(MapGeneratorInternal.DATABASE_RENDERER);
                }
                if (forceAndFeedback) {
                    Toast.makeText(this, result == FileOpenResult.SUCCESS ? R.string.receivemapfile_success : R.string.receivemapfile_error, Toast.LENGTH_SHORT).show();
                }
            } else if (forceAndFeedback) {
                Toast.makeText(this, R.string.receivemapfile_notset, Toast.LENGTH_SHORT).show();
            }
        }

        boolean center = bundle != null && bundle.getBoolean(BUNDLE_CENTER, false);
        boolean navigate = bundle != null && bundle.getBoolean(BUNDLE_NAVIGATE, false);
        allowStartCartridge = bundle != null && bundle.getBoolean(BUNDLE_ALLOW_START_CARTRIDGE, false);
        if (bundle != null && bundle.containsKey(BUNDLE_ITEMS)) {
            ArrayList<MapPointPack> items = bundle.getParcelableArrayList(BUNDLE_ITEMS);
            showMapPack(items);
        } else {
            showMapPack(VectorMapDataProvider.getInstance().getItems());
        }
        if (center && itemsLatitude != 0 && itemsLongitude != 0) {
            GeoPoint geoPoint;
            if (navigate && this.navigationOverlay.getTarget() != null)
                geoPoint = this.navigationOverlay.getTarget();
            else
                geoPoint = new GeoPoint(itemsLatitude, itemsLongitude);
            MapPosition newMapPosition =
                    new MapPosition(geoPoint, mapView.getMapViewPosition().getZoomLevel());
            mapView.getMapViewPosition().setMapPosition(newMapPosition);
        }
    }

    private void setMapGenerator(MapGeneratorInternal type) {
        if (this.mapGeneratorInternal != type) {
            this.mapGeneratorInternal = type;
            MapGenerator generator = MapGeneratorFactory.createMapGenerator(type);
            this.mapView.setMapGenerator(generator, type.ordinal());
            TextView attributionView = (TextView) findViewById(R.id.attribution);
            if (generator instanceof TileDownloader) {
                String attribution = ((TileDownloader) generator).getAttribution();
                attributionView.setText(attribution == null ? "" : Html.fromHtml(attribution, null, null));
            } else {
                attributionView.setText("");
            }
        }
    }

    private void showMapPack(ArrayList<MapPointPack> packs) {
        synchronized (lock) {
            this.navigationOverlay.setTarget(null);
            itemsLatitude = itemsLongitude = 0;
            int count = 0;
            listOverlay.clear();
            List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
            List<OverlayItem> overlayLines = new ArrayList<>();
            List<OverlayItem> overlayPoints = new ArrayList<>();
            // overlayItems.clear();
            for (MapPointPack pack : packs) {
                if (pack.isPolygon()) {
                    List<GeoPoint> geoPoints = new ArrayList<>();
                    for (MapPoint mp : pack.getPoints()) {
                        GeoPoint geoPoint = new GeoPoint(mp.getLatitude(), mp.getLongitude());
                        geoPoints.add(geoPoint);
                    }
                    overlayLines.add(createPolyline(geoPoints));
                } else {
                    Drawable icon;
                    if (pack.getIcon() == null) {
                        icon =
                                getResources().getDrawable(
                                        pack.getResource() != 0 ? pack.getResource() : R.drawable.marker_red);
                    } else {
                        Bitmap b = pack.getIcon();
                        if (b.getWidth() > ICON_SIZE_MAX && b.getWidth() >= b.getHeight()) {
                            b =
                                    Bitmap.createScaledBitmap(b, ICON_SIZE_MAX,
                                            ICON_SIZE_MAX * b.getHeight() / b.getWidth(), false);
                        } else if (b.getHeight() > ICON_SIZE_MAX) {
                            b =
                                    Bitmap.createScaledBitmap(b, ICON_SIZE_MAX * b.getWidth() / b.getHeight(),
                                            ICON_SIZE_MAX, false);
                        }
                        icon = new BitmapDrawable(getResources(), b);
                    }
                    icon = Marker.boundCenterBottom(icon);
                    for (MapPoint mp : pack.getPoints()) {
                        GeoPoint geoPoint = new GeoPoint(mp.getLatitude(), mp.getLongitude());
                        PointOverlay pointOverlay = new PointOverlay(geoPoint, icon, mp);
                        pointOverlay.setMarkerVisible(showPins);
                        pointOverlay.setLabelVisible(showLabels);
                        overlayPoints.add(pointOverlay);
                        if (mp.isTarget())
                            this.navigationOverlay.setTarget(geoPoint);
                        itemsLatitude += mp.getLatitude();
                        itemsLongitude += mp.getLongitude();
                        ++count;
                    }
                }
            }
            overlayItems.addAll(overlayLines);
            overlayItems.addAll(overlayPoints);
            if (count > 0) {
                itemsLatitude /= count;
                itemsLongitude /= count;
            }
        }
    }

    /**
     * Uses the UI thread to display the given text message as toast notification.
     *
     * @param text the text message to display
     */
    void showToastOnUiThread(final String text) {
        if (AndroidUtils.currentThreadIsUiThread()) {
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(MapsforgeActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    /**
     * Sets all file filters and starts the FilePicker to select a map file.
     */
    private void startMapFilePicker() {
        FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_MAP);
        FilePicker.setFileSelectFilter(new ValidMapFile());
        startActivityForResult(new Intent(this, FilePicker.class), SELECT_MAP_FILE);
    }

    /**
     * Sets all file filters and starts the FilePicker to select an XML file.
     */
    private void startRenderThemePicker() {
        FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_XML);
        FilePicker.setFileSelectFilter(new ValidRenderTheme());
        startActivityForResult(new Intent(this, FilePicker.class), SELECT_RENDER_THEME_FILE);
    }

    private void visibilityChanged() {
        synchronized (lock) {
            List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
            for (int i = 0; i < overlayItems.size(); i++) {
                OverlayItem item = overlayItems.get(i);
                if (item instanceof LabelMarker) {
                    LabelMarker labelMarker = (LabelMarker) item;
                    labelMarker.setMarkerVisible(this.showPins);
                    labelMarker.setLabelVisible(this.showLabels);
                }
            }
        }
        this.mapView.getOverlayController().redrawOverlays();
    }
}
