package com.feikol.vesselviewer.data;

import com.feikol.vesselviewer.model.Vessel;

import java.util.List;

/**
 * Created by FeikoLai on 12/4/14.
 */
public interface DataSourceQueryVesselCallback {
    public void onQueryVesselComplete(List<Vessel> vessels, Exception e);
}
