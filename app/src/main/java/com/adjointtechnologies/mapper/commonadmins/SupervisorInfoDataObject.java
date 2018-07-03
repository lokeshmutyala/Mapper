package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 18-03-2018.
 */

public class SupervisorInfoDataObject {
    private String mapperName,mapperId,mapperMobile,lastSyncTime,workingTime;
    private int totalStores,insideStores;

    public String getMapperName() {
        return mapperName;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(String workingTime) {
        this.workingTime = workingTime;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getMapperId() {
        return mapperId;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    public String getMapperMobile() {
        return mapperMobile;
    }

    public void setMapperMobile(String mapperMobile) {
        this.mapperMobile = mapperMobile;
    }

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public int getTotalStores() {
        return totalStores;
    }

    public void setTotalStores(int totalStores) {
        this.totalStores = totalStores;
    }

    public int getInsideStores() {
        return insideStores;
    }

    public void setInsideStores(int insideStores) {
        this.insideStores = insideStores;
    }
}
