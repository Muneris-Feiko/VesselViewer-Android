package com.feikol.vesselviewer.data;

/**
 * Created by FeikoLai on 12/4/14.
 */
public interface DataSourceStatusListener {
    public void onDataSourceReady();
    public void onDataSourceFail();
}
