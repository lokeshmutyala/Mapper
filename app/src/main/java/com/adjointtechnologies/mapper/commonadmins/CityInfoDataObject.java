package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class CityInfoDataObject {
    String agencyName,workTime,agencyId;
    int mapperWorkingNo,storesNo,insideNo,avgNo;

    public String getWorkTime() {
        return workTime;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public int getMapperWorkingNo() {
        return mapperWorkingNo;
    }

    public void setMapperWorkingNo(int mapperWorkingNo) {
        this.mapperWorkingNo = mapperWorkingNo;
    }

    public int getStoresNo() {
        return storesNo;
    }

    public void setStoresNo(int storesNo) {
        this.storesNo = storesNo;
    }

    public int getInsideNo() {
        return insideNo;
    }

    public void setInsideNo(int insideNo) {
        this.insideNo = insideNo;
    }

    public int getAvgNo() {
        if(mapperWorkingNo>0)
        avgNo=storesNo/mapperWorkingNo;
        return avgNo;

    }

    public void setAvgNo(int avgNo) {
        this.avgNo = avgNo;
    }
}
