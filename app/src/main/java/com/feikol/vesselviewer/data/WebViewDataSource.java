package com.feikol.vesselviewer.data;

import android.app.Activity;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.feikol.vesselviewer.model.Vessel;
import com.feikol.vesselviewer.util.DebugUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by FeikoLai on 12/4/14.
 * <p/>
 * According to http://developer.android.com/guide/webapps/migrating.html
 * WebView code must be run on ui thread
 *
 */
//WebView based impl of DataSource
public class WebViewDataSource extends AbstractDataSource {

    private static final String SOURCE_URL = "https://www.marinetraffic.com/";

    private static final String JS_OBJECT_NAME = "Android";

    private WebView webView;

    private Activity activity;

    private AtomicBoolean loaded = new AtomicBoolean(false);

    public WebViewDataSource(Activity activity) {
        this.webView = new WebView(activity);
        this.activity = activity;
    }

    @Override
    public void start() {
        loaded.set(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {

                        if (!loaded.getAndSet(true)) {
                            if (WebViewDataSource.this.dataSourceStatusListener != null) {
                                WebViewDataSource.this.dataSourceStatusListener.onDataSourceReady();
                            }
                        }
                        super.onPageFinished(view, url);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                        if (WebViewDataSource.this.dataSourceStatusListener != null) {
                            WebViewDataSource.this.dataSourceStatusListener.onDataSourceFail();
                        }
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.startsWith(JS_OBJECT_NAME)) {
                            return false;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                });
                webView.getSettings().setJavaScriptEnabled(true);
                webView.addJavascriptInterface(WebViewDataSource.this, JS_OBJECT_NAME);
                webView.loadUrl(SOURCE_URL);
            }
        });
    }

    @Override
    public void queryVessels(LatLngBounds bounds, float zoom) {
        if(isReady()) {
            final String path = String.format(
                    "javascript:downloadUrl('/map/getjson/sw_x:%f/sw_y:%f/ne_x:%f/ne_y:%f/zoom:&f/fleet:/station:0','text', function (data, responseCode) {Android.onQueryVesselComplete(data, responseCode)})",
                    bounds.southwest.longitude,
                    bounds.southwest.latitude,
                    bounds.northeast.longitude,
                    bounds.northeast.latitude,
                    zoom);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(path);
                }
            });
        }
    }

    @Override
    public void searchVesselsAndPorts(String keyword) {
        if(isReady()) {
            final String path = String.format("javascript:downloadUrl('/map/searchjson/?what=map&term=%s','text', function (data, responseCode) {Android.onSearchComplete(data, responseCode)})", keyword);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(path);
                }
            });
        }
    }

    @Override
    public boolean isReady() {
        return loaded.get();
    }


    @JavascriptInterface
    public void onQueryVesselComplete(String data, String responseCode) {
        if (dataSourceQueryVesselCallback != null) {
            if ("200".equals(responseCode)) {
                try {
                    List<Vessel> vessels = new LinkedList<Vessel>();
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONArray entry = jsonArray.getJSONArray(i);
                            double lat = entry.getDouble(0);
                            double lng = entry.getDouble(1);
                            Vessel vessel = new Vessel();
                            vessel.position = new LatLng(lat, lng);
                            vessels.add(vessel);

                        } catch (JSONException e) {
                            DebugUtil.Log(e);
                        }
                    }
                    dataSourceQueryVesselCallback.onQueryVesselComplete(vessels, null);

                } catch (Exception e) {
                    dataSourceQueryVesselCallback.onQueryVesselComplete(new LinkedList<Vessel>(), e);
                }
            } else {
                dataSourceQueryVesselCallback.onQueryVesselComplete(new LinkedList<Vessel>(), new Exception("response code: " + responseCode));
            }
        }
    }


    @JavascriptInterface
    public void onSearchComplete(String data, String responseCode) {

        if (dataSourceSearchCallback != null) {
            if ("200".equals(responseCode)) {
                try {
                    List<Pair<String, String>> pairs = new LinkedList<Pair<String, String>>();
                    JSONArray result = new JSONArray(data);
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject entry = result.getJSONObject(i);
                            String value = entry.getString("value");
                            String content = toContentString(entry);
                            Pair<String, String> valueContentPair = new Pair<String, String>(value, content);
                            pairs.add(valueContentPair);
                        } catch (Exception e) {
                            DebugUtil.Log(e);
                        }
                    }
                    dataSourceSearchCallback.onSearchComplete(pairs, null);
                } catch (Exception e) {
                    dataSourceSearchCallback.onSearchComplete(new LinkedList<Pair<String, String>>(), e);
                }
            } else {
                dataSourceSearchCallback.onSearchComplete(new LinkedList<Pair<String, String>>(), new Exception("response code: " + responseCode));
            }
        }
    }

    private static String toContentString(JSONObject entry) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = entry.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = entry.optString(key);
            sb.append(key + ": " + value + "\n");
        }
        return sb.toString();
    }
}
