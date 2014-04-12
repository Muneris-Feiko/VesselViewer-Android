package com.feikol.vesselviewer.data;

/**
 * Created by FeikoLai on 12/4/14.
 */
public abstract class AbstractDataSource implements DataSource{

    protected DataSourceQueryVesselCallback dataSourceQueryVesselCallback;
    protected DataSourceSearchCallback dataSourceSearchCallback;
    protected DataSourceStatusListener dataSourceStatusListener;

    public void setDataSourceQueryVesselCallback(DataSourceQueryVesselCallback dataSourceQueryVesselCallback) {
        this.dataSourceQueryVesselCallback = dataSourceQueryVesselCallback;
    }

    public void setDataSourceSearchCallback(DataSourceSearchCallback dataSourceSearchCallback) {
        this.dataSourceSearchCallback = dataSourceSearchCallback;
    }

    public void setDataSourceStatusListener(DataSourceStatusListener dataSourceStatusListener) {
        this.dataSourceStatusListener = dataSourceStatusListener;
    }
}
