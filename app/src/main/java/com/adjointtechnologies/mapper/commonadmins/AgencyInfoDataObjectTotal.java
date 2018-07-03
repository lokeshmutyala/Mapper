package com.adjointtechnologies.mapper.commonadmins;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class AgencyInfoDataObjectTotal {
    int insideNo,storesNo,poeopleNo;
    String supervisorName,supervisorId;
    int storesSum,inStoreSum,peopleSum,daysSum;//,totalAvg,;

    public int getInsideNo() {
        return insideNo;
    }

    public void setInsideNo(int insideNo) {
        this.insideNo = insideNo;
    }

    public int getStoresNo() {
        return storesNo;
    }

    public void setStoresNo(int storesNo) {
        this.storesNo = storesNo;
    }

    public int getPoeopleNo() {
        return poeopleNo;
    }

    public void setPoeopleNo(int poeopleNo) {
        this.poeopleNo = poeopleNo;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public int getStoresSum() {
        return storesSum;
    }

    public void setStoresSum(int storesSum) {
        this.storesSum = storesSum;
    }

    public int getInStoreSum() {
        return inStoreSum;
    }

    public void setInStoreSum(int inStoreSum) {
        this.inStoreSum = inStoreSum;
    }

    public int getPeopleSum() {
        return peopleSum;
    }

    public void setPeopleSum(int peopleSum) {
        this.peopleSum = peopleSum;
    }

    public int getDaysSum() {
        return daysSum;
    }

    public void setDaysSum(int daysSum) {
        this.daysSum = daysSum;
    }
}
