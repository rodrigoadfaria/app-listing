package com.example.rfaria.backgrounddata;

import android.content.pm.ApplicationInfo;

public class AppsInfo implements Comparable<AppsInfo> {

    private ApplicationInfo applicationInfo;
    private String label;

    public String getPakage() {
        return pakage;
    }

    public void setPakage(String pakage) {
        this.pakage = pakage;
    }

    private String pakage;

    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;

    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }


    @Override
    public int compareTo(AppsInfo another) {
        return this.label.compareToIgnoreCase(another.getLabel());
    }
}
