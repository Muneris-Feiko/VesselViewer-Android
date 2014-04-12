package com.feikol.vesselviewer.util;

/**
 * Created by FeikoLai on 13/4/14.
 */
public class DebugUtil {
    public static void Log(String msg)
    {
        android.util.Log.d("[VesselViewer Debug Log]", msg);
    }

    public static void Log(Throwable t)
    {
        android.util.Log.d("[VesselViewer Debug Log]","[Throwable]", t);
    }

}
