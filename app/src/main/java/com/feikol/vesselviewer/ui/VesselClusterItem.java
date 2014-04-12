package com.feikol.vesselviewer.ui;

import com.feikol.vesselviewer.model.Vessel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by FeikoLai on 12/4/14.
 */
public class VesselClusterItem implements ClusterItem {

    private final Vessel vessel;

    public VesselClusterItem(Vessel vessel) {
        this.vessel = vessel;
    }

    @Override
    public LatLng getPosition() {
        return vessel.position;
    }
}
