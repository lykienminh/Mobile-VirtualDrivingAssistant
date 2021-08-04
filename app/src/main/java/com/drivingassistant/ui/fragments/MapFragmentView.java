package com.drivingassistant.ui.fragments;

/*
 * Copyright (c) 2011-2020 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.drivingassistant.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.guidance.TrafficUpdater;
import com.here.android.mpa.mapping.MapTrafficLayer;
import com.here.android.mpa.mapping.MapTransitLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * This class encapsulates the properties and functionality of the Map view.
 */
public class MapFragmentView {
    private AndroidXMapFragment m_mapFragment;
    private Map m_map;
    private ImageButton m_settingsBtn;
    private SettingsPanel m_settingsPanel;
    private LinearLayout m_settingsLayout;
    private GeoCoordinate current_postion = null;;

    private PositioningManager positioningManager = null;
    private PositioningManager.OnPositionChangedListener positionListener;

    private AppCompatActivity m_activity;

    public MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        initMapFragment();
        initSettingsPanel();
    }

    private AndroidXMapFragment getMapFragment() {
        return (AndroidXMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);

    }

    private void initMapFragment() {
        m_mapFragment = getMapFragment();
        // This will use external storage to save map cache data, it is also possible to set
        // private app's path
        String path = new File(m_activity.getExternalFilesDir(null), ".here-map-data")
                .getAbsolutePath();
        // This method will throw IllegalArgumentException if provided path is not writable
        com.here.android.mpa.common.MapSettings.setDiskCacheRootPath(path);

        if (m_mapFragment != null) {
            /* Initialize the AndroidXMapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    /*
                     * if no error returned from map fragment initialization, then set map center
                     * and zoom level.
                     */
                    if (error == Error.NONE) {
                        /* get the map object */
                        m_map = m_mapFragment.getMap();

                        m_map.getMapTransitLayer().setMode(MapTransitLayer.Mode.EVERYTHING);
                        m_map.setTrafficInfoVisible(true);
                        m_map.getMapTrafficLayer().setEnabled(MapTrafficLayer.RenderLayer.FLOW, true);
                        /* Set the map center to the Berlin region (no animation). */
                        m_map.setCenter(new GeoCoordinate(52.500556, 13.398889, 0.0),
                                Map.Animation.NONE);

                        /* Set the zoom level to the average between min and max zoom level. */
                        m_map.setZoomLevel((m_map.getMaxZoomLevel() + m_map.getMinZoomLevel()) * 0.8);

                        positioningManager = PositioningManager.getInstance();
                        positionListener = new PositioningManager.OnPositionChangedListener() {
                            @Override
                            public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
                                current_postion = position.getCoordinate();
                                m_map.setCenter(position.getCoordinate(), Map.Animation.NONE);
                                Log.wtf("Hoang", "Location change");
                            }
                            @Override
                            public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) { }
                        };
//
                        try {
                            positioningManager.addListener(new WeakReference<>(positionListener));
                            if(!positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK)) {
                                Log.e("HERE", "PositioningManager.start: Failed to start...");
                            }
                        } catch (Exception e) {
                            Log.e("HERE", "Caught: " + e.getMessage());
                        }
                        m_map.getPositionIndicator().setVisible(true);

//                        m_map.getMapTransitLayer().setMode(MapTransitLayer.Mode.EVERYTHING);
//                        m_map.setTrafficInfoVisible(true);
//                        m_map.getMapTrafficLayer().setEnabled(MapTrafficLayer.RenderLayer.FLOW, true);
                        TrafficUpdater.getInstance().setRefreshInterval(6000);

                    } else {
                        new AlertDialog.Builder(m_activity).setMessage(
                                "Error : " + error.name() + "\n\n" + error.getDetails())
                                .setTitle(R.string.engine_init_error)
                                .setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                m_activity.finish();
                                            }
                                        }).create().show();
                    }
                }
            });

        }

        Log.wtf("Hoang", "Im running");

    }

    private void initSettingsPanel() {
        m_settingsBtn = (ImageButton) m_activity.findViewById(R.id.settingButton);

        /* click settings panel button to open or close setting panel. */
        m_settingsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m_settingsLayout = (LinearLayout) m_activity.findViewById(R.id.settingsPanelLayout);
                if (m_settingsLayout.getVisibility() == View.GONE) {
                    m_settingsLayout.setVisibility(View.VISIBLE);
                    if (m_settingsPanel == null) {
                        m_settingsPanel = new SettingsPanel(m_activity, m_map);
                    }
                } else {
                    m_settingsLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}

