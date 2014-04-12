package com.feikol.vesselviewer.data;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by FeikoLai on 12/4/14.
 */
public interface DataSource {
    public void start();
    public void setDataSourceStatusListener(DataSourceStatusListener listener);
    public void setDataSourceQueryVesselCallback(DataSourceQueryVesselCallback callback);
    public void setDataSourceSearchCallback(DataSourceSearchCallback callback);
    public void queryVessels(LatLngBounds bounds, float zoom);
    public void searchVesselsAndPorts(String keyword);
    public boolean isReady();
}
