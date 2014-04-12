package com.feikol.vesselviewer.ui;

import android.content.Context;

import com.feikol.vesselviewer.config.VesselViewerConfig;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by FeikoLai on 12/4/14.
 */
public class VesselClusterRenderer extends DefaultClusterRenderer<VesselClusterItem>
{

    private final VesselViewerConfig vesselViewerConfig;

    public VesselClusterRenderer(Context context, GoogleMap map, ClusterManager<VesselClusterItem> clusterManager, VesselViewerConfig vesselViewerConfig) {

        super(context, map, clusterManager);

        this.vesselViewerConfig = vesselViewerConfig;
    }

    @Override
    protected void onBeforeClusterItemRendered(VesselClusterItem vesselClusterItem, MarkerOptions markerOptions) {

        if(vesselViewerConfig.markerImageResourceId != 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(vesselViewerConfig.markerImageResourceId));
        }else
        {
            super.onBeforeClusterItemRendered(vesselClusterItem, markerOptions);
        }
    }

}
