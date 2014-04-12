package com.feikol.vesselviewer.config;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by FeikoLai on 12/4/14.
 */
public class VesselViewerConfig {
    public LatLng initPosition ;
    public float initZoom;
    public int markerImageResourceId;
    public int updateInterval;

    public VesselViewerConfig(LatLng initPosition, float initZoom, int markerImageResourceId, int updateInterval) {
        this.initPosition = initPosition;
        this.initZoom = initZoom;
        this.markerImageResourceId = markerImageResourceId;
        this.updateInterval = updateInterval;
    }
}
