package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class CityInfoDataObjectTotal {
    String agencyName,agencyId;
    int poeopleNo,storesNo,insideNo,totalAvg;

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public int getPoeopleNo() {
        return poeopleNo;
    }

    public void setPoeopleNo(int poeopleNo) {
        this.poeopleNo = poeopleNo;
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

    public int getTotalAvg() {
        if(poeopleNo>0)
            totalAvg=storesNo/poeopleNo;
        return totalAvg;
    }

    public void setTotalAvg(int totalAvg) {
        this.totalAvg = totalAvg;
    }
}
