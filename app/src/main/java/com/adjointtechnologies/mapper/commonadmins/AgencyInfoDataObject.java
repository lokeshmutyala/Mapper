package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 21-03-2018.
 */

public class AgencyInfoDataObject {
    String supervisorName,supervisorId;
    int mapperWorkingNo,storesNo,insideNo,avgNo;

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
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
        return avgNo;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public void setAvgNo(int avgNo) {
        this.avgNo = avgNo;
    }
}
