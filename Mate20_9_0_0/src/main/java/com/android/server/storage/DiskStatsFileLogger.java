package com.android.server.storage;

import android.content.pm.PackageStats;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import com.android.server.storage.FileCollector.MeasurementResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiskStatsFileLogger {
    public static final String APP_CACHES_KEY = "cacheSizes";
    public static final String APP_CACHE_AGG_KEY = "cacheSize";
    public static final String APP_DATA_KEY = "appDataSizes";
    public static final String APP_DATA_SIZE_AGG_KEY = "appDataSize";
    public static final String APP_SIZES_KEY = "appSizes";
    public static final String APP_SIZE_AGG_KEY = "appSize";
    public static final String AUDIO_KEY = "audioSize";
    public static final String DOWNLOADS_KEY = "downloadsSize";
    public static final String LAST_QUERY_TIMESTAMP_KEY = "queryTime";
    public static final String MISC_KEY = "otherSize";
    public static final String PACKAGE_NAMES_KEY = "packageNames";
    public static final String PHOTOS_KEY = "photosSize";
    public static final String SYSTEM_KEY = "systemSize";
    private static final String TAG = "DiskStatsLogger";
    public static final String VIDEOS_KEY = "videosSize";
    private long mDownloadsSize;
    private List<PackageStats> mPackageStats;
    private MeasurementResult mResult;
    private long mSystemSize;

    public DiskStatsFileLogger(MeasurementResult result, MeasurementResult downloadsResult, List<PackageStats> stats, long systemSize) {
        this.mResult = result;
        this.mDownloadsSize = downloadsResult.totalAccountedSize();
        this.mSystemSize = systemSize;
        this.mPackageStats = stats;
    }

    public void dumpToFile(File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        JSONObject representation = getJsonRepresentation();
        if (representation != null) {
            pw.println(representation);
        }
        pw.close();
    }

    private JSONObject getJsonRepresentation() {
        JSONObject json = new JSONObject();
        try {
            json.put(LAST_QUERY_TIMESTAMP_KEY, System.currentTimeMillis());
            json.put(PHOTOS_KEY, this.mResult.imagesSize);
            json.put(VIDEOS_KEY, this.mResult.videosSize);
            json.put(AUDIO_KEY, this.mResult.audioSize);
            json.put(DOWNLOADS_KEY, this.mDownloadsSize);
            json.put(SYSTEM_KEY, this.mSystemSize);
            json.put(MISC_KEY, this.mResult.miscSize);
            addAppsToJson(json);
            return json;
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private void addAppsToJson(JSONObject json) throws JSONException {
        long cacheSizeSum;
        JSONObject jSONObject = json;
        JSONArray names = new JSONArray();
        JSONArray appSizeList = new JSONArray();
        JSONArray appDataSizeList = new JSONArray();
        JSONArray cacheSizeList = new JSONArray();
        long appSizeSum = 0;
        long appDataSizeSum = 0;
        long cacheSizeSum2 = 0;
        boolean isExternal = Environment.isExternalStorageEmulated();
        Iterator it = filterOnlyPrimaryUser().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, PackageStats> entry = (Entry) it.next();
            PackageStats stat = (PackageStats) entry.getValue();
            Iterator it2 = it;
            long appSize = stat.codeSize;
            JSONArray appDataSizeList2 = appDataSizeList;
            JSONArray cacheSizeList2 = cacheSizeList;
            long appDataSize = stat.dataSize;
            JSONArray names2 = names;
            long cacheSize = stat.cacheSize;
            if (isExternal) {
                cacheSizeSum = cacheSizeSum2;
                appSize += stat.externalCodeSize;
                appDataSize += stat.externalDataSize;
                cacheSize += stat.externalCacheSize;
            } else {
                cacheSizeSum = cacheSizeSum2;
            }
            appDataSizeSum += appDataSize;
            cacheSizeSum2 = cacheSizeSum + cacheSize;
            long appSizeSum2 = appSizeSum + appSize;
            JSONArray names3 = names2;
            names3.put(stat.packageName);
            appSizeList.put(appSize);
            JSONArray appDataSizeList3 = appDataSizeList2;
            appDataSizeList3.put(appDataSize);
            long j = appDataSize;
            appDataSizeList = cacheSizeList2;
            appDataSizeList.put(cacheSize);
            cacheSizeList = appDataSizeList;
            appDataSizeList = appDataSizeList3;
            names = names3;
            it = it2;
            appSizeSum = appSizeSum2;
            jSONObject = json;
        }
        JSONArray names4 = names;
        names = appDataSizeList;
        appDataSizeList = cacheSizeList;
        cacheSizeSum = cacheSizeSum2;
        JSONObject jSONObject2 = json;
        jSONObject2.put(PACKAGE_NAMES_KEY, names4);
        jSONObject2.put(APP_SIZES_KEY, appSizeList);
        jSONObject2.put(APP_CACHES_KEY, appDataSizeList);
        jSONObject2.put(APP_DATA_KEY, names);
        jSONObject2.put(APP_SIZE_AGG_KEY, appSizeSum);
        jSONObject2.put(APP_CACHE_AGG_KEY, cacheSizeSum);
        jSONObject2.put(APP_DATA_SIZE_AGG_KEY, appDataSizeSum);
    }

    private ArrayMap<String, PackageStats> filterOnlyPrimaryUser() {
        ArrayMap<String, PackageStats> packageMap = new ArrayMap();
        for (PackageStats stat : this.mPackageStats) {
            if (stat.userHandle == 0) {
                PackageStats existingStats = (PackageStats) packageMap.get(stat.packageName);
                if (existingStats != null) {
                    existingStats.cacheSize += stat.cacheSize;
                    existingStats.codeSize += stat.codeSize;
                    existingStats.dataSize += stat.dataSize;
                    existingStats.externalCacheSize += stat.externalCacheSize;
                    existingStats.externalCodeSize += stat.externalCodeSize;
                    existingStats.externalDataSize += stat.externalDataSize;
                } else {
                    packageMap.put(stat.packageName, new PackageStats(stat));
                }
            }
        }
        return packageMap;
    }
}