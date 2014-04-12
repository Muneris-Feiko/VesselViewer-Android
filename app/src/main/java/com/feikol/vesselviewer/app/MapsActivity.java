package com.feikol.vesselviewer.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.feikol.vesselviewer.config.VesselViewerConfig;
import com.feikol.vesselviewer.data.DataSource;
import com.feikol.vesselviewer.data.DataSourceQueryVesselCallback;
import com.feikol.vesselviewer.data.DataSourceSearchCallback;
import com.feikol.vesselviewer.data.DataSourceStatusListener;
import com.feikol.vesselviewer.data.WebViewDataSource;
import com.feikol.vesselviewer.model.Vessel;
import com.feikol.vesselviewer.ui.SearchDialog;
import com.feikol.vesselviewer.ui.SearchResultDialog;
import com.feikol.vesselviewer.ui.VesselClusterItem;
import com.feikol.vesselviewer.ui.VesselClusterRenderer;
import com.feikol.vesselviewer.util.NetworkUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {

    private VesselViewerConfig config; //App-wide config
    private DataSource dataSource; // From which app get vessel info and search result
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private WebView webView;
    private ClusterManager<VesselClusterItem> clusterManager;
    private Timer timer;//For periodical update


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load config object, from any source
        config = new VesselViewerConfig(
                new LatLng(22.3700556, 114.1535941),
                9.0f,
                R.drawable.vessellogo,
                60);

        //composition/injection point
        dataSource = new WebViewDataSource(this);
        setUpDataSourceHandler();
        dataSource.start();

        //set up ui window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_maps);

        //set up map
        setUpMapIfNeeded();

        checkNetwork();

    }

    protected void setUpDataSourceHandler() {
        dataSource.setDataSourceStatusListener(new DataSourceStatusListener() {
            @Override
            public void onDataSourceReady() {
                restartUpdateSchedule();
            }

            @Override
            public void onDataSourceFail() {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(MapsActivity.this, "Data source failed, please reconnect later", Toast.LENGTH_SHORT).show();
            }
        });
        dataSource.setDataSourceQueryVesselCallback(new DataSourceQueryVesselCallback() {
            @Override
            public void onQueryVesselComplete(final List<Vessel> vessels, Exception e) {
                if (e == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(vessels.size() > 0) {
                                clusterManager.clearItems();
                                for (Vessel vessel : vessels) {
                                    clusterManager.addItem(new VesselClusterItem(vessel));
                                }
                                clusterManager.cluster();
                            }

                        }
                    });

                } else {
                    Toast.makeText(MapsActivity.this, "Query Vessel Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBarIndeterminateVisibility(false);
                    }
                });

            }
        });

        dataSource.setDataSourceSearchCallback(new DataSourceSearchCallback() {
            @Override
            public void onSearchComplete(final List<Pair<String, String>> valueContentPairs, Exception e) {
                if (e == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FragmentManager fm = getSupportFragmentManager();
                            SearchResultDialog searchResultDialog = new SearchResultDialog(valueContentPairs);
                            searchResultDialog.show(fm, "fragment_search_result");
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Search Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBarIndeterminateVisibility(false);
                    }
                });
            }
        });
    }

    private void checkNetwork()
    {
        if(!NetworkUtil.isNetworkOnline(this))
        {
            Toast.makeText(MapsActivity.this, "Network is not available, please connect Internet and reconnect data source.", Toast.LENGTH_SHORT).show();
        } else {
            //start progress bar until data source is ready
            setProgressBarIndeterminateVisibility(true);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            } else {
                Toast.makeText(this, "play service is not available, please set up it and relaunch.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpMap() {

        setUpClusterManager();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(config.initPosition, config.initZoom));

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                restartUpdateSchedule();
                startQuery(cameraPosition);
            }
        });

        googleMap.setOnMarkerClickListener(clusterManager);
    }

    private void setUpClusterManager() {
        clusterManager = new ClusterManager<VesselClusterItem>(this, googleMap);
        clusterManager.setRenderer(new VesselClusterRenderer(this, googleMap, clusterManager, config));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            FragmentManager fm = getSupportFragmentManager();
            SearchDialog searchDialog = new SearchDialog(dataSource);
            searchDialog.show(fm, "fragment_search_dialog");
        }

        if (id == R.id.action_reconnect) {
            dataSource.start();
            Toast.makeText(this, "Restart data source", Toast.LENGTH_SHORT).show();

        }


        return super.onOptionsItemSelected(item);
    }

    private void restartUpdateSchedule() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(googleMap != null) {
                            startQuery(googleMap.getCameraPosition());
                        }
                    }
                });
            }
        }, 500, config.updateInterval * 1000);

    }

    private void startQuery(CameraPosition cameraPosition) {
        if (googleMap != null) {
            setProgressBarIndeterminateVisibility(true);
            dataSource.queryVessels(googleMap.getProjection().getVisibleRegion().latLngBounds, googleMap.getCameraPosition().zoom);
            clusterManager.onCameraChange(cameraPosition);
        }

    }





}
