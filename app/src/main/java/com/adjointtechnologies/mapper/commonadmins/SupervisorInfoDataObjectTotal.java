package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class SupervisorInfoDataObjectTotal {
    String mapperName,mapperId,avgWorkTime;
    int totalStores,insideStores;

    public String getMapperName() {
        return mapperName;
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

    public String getAvgWorkTime() {
        return avgWorkTime;
    }

    public void setAvgWorkTime(String avgWorkTime) {
        this.avgWorkTime = avgWorkTime;
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
