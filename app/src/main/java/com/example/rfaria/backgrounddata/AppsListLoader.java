package com.example.rfaria.backgrounddata;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppsListLoader
        extends AsyncTask<String, Object, Object> {

    private static String TAG = "BatteryGuru[ListApps]";

    private Map<String, AppsInfo> appsMap;
    private ArrayList<Integer> bgAppsUIDs = null;
    private AsyncResponse delegate = null;
    private Context mContext = null;
    private PackageManager packageManager = null;
    private ArrayList<String> pkgBlacklist = new ArrayList(
            Arrays.asList(new String[]{Build.MANUFACTURER.toLowerCase(), "qualcomm"}));

    public AppsListLoader(Context paramContext, AsyncResponse paramAsyncResponse) {
        this.mContext = paramContext;
        this.delegate = paramAsyncResponse;
        this.packageManager = this.mContext.getPackageManager();
        this.bgAppsUIDs = new ArrayList();
        this.appsMap = new HashMap<>();
        this.packageManager = mContext.getPackageManager();
    }

    protected Void doInBackground(String... paramVarArgs) {
        Log.d(TAG, "System app is not installed. Loading the application list from PackageManager.");
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (isUserApp(packageInfo.uid)) {    //System UID must not be considered
                bgAppsUIDs.add(packageInfo.uid);
            }
        }

        //For each UID returned, get all package names and its application info
        for (Integer uid : bgAppsUIDs) {

            String[] pkgNames = packageManager.getPackagesForUid(uid);

            //Check all package names related for each UID.
            for (int i = 0; i < pkgNames.length; i++) {
                boolean isRestricted = false;
                try {
                    ApplicationInfo localApplicationInfo = packageManager.getApplicationInfo(pkgNames[i], 0);
                    Log.d(TAG, "name: " + localApplicationInfo.loadLabel(this.packageManager) +
                            " pkgName: " + localApplicationInfo.packageName +
                            " uid: " + localApplicationInfo.uid);

                    //System app verification
                    if (isSystemPackage(localApplicationInfo)) {
                        Log.d(TAG, "Package " + localApplicationInfo.packageName +
                                " was restricted because it is system package");
                        isRestricted = false;
                    } else {
                        //Blacklist verification
                        for (String restrictedPkg : pkgBlacklist) {
                            if (localApplicationInfo.packageName.contains(restrictedPkg)) {
                                Log.d(TAG, "Package " + localApplicationInfo.packageName +
                                        " was restricted because it contains " + restrictedPkg);
                                isRestricted = true;
                                break;
                            }
                        }
                    }

                    if (!isRestricted) {
                        AppsInfo app = new AppsInfo();
                        app.setApplicationInfo(localApplicationInfo);
                        app.setLabel(localApplicationInfo.loadLabel(this.packageManager).toString());
                        app.setPakage(localApplicationInfo.packageName);
                        appsMap.put(app.getPakage(), app);
                    }

                } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
                    Log.d(TAG, localNameNotFoundException.toString());
                }

            }
        }

        return null;
    }

    protected void onPostExecute(Object paramObject) {
        if (!appsMap.isEmpty()) {
            List<AppsInfo> apps = new ArrayList<AppsInfo>(appsMap.values());
            Collections.sort(apps);
            delegate.processFinish(apps);
        }
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {

        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    private static boolean isUserApp(int uid) {
        if (uid > 0) {
            final int appId = uid % 100000;
            return appId >= android.os.Process.FIRST_APPLICATION_UID &&
                    appId <= android.os.Process.LAST_APPLICATION_UID;
        } else {
            return false;
        }
    }

}
