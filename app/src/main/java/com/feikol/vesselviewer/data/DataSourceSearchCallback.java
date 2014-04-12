package com.feikol.vesselviewer.data;

import android.util.Pair;

import java.util.List;

/**
 * Created by FeikoLai on 12/4/14.
 */
public interface DataSourceSearchCallback {
    public void onSearchComplete(List<Pair<String, String>> valueContentPairs, Exception e);
}
