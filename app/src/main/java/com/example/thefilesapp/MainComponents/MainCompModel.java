package com.example.thefilesapp.MainComponents;

import android.content.pm.PackageInfo;
import java.util.List;

public class MainCompModel {

    List<String> pathList;
    List<PackageInfo> packageInfoList;

    public MainCompModel(List<String> pathList, List<PackageInfo> packageInfoList) {
        this.pathList = pathList;
        this.packageInfoList = packageInfoList;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    public List<PackageInfo> getPackageInfoList() {
        return packageInfoList;
    }

    public void setPackageInfoList(List<PackageInfo> packageInfoList) {
        this.packageInfoList = packageInfoList;
    }
}
