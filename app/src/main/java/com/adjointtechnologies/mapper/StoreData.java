package com.adjointtechnologies.mapper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lokeshmutyala on 31-10-2017.
 */

public class StoreData {
    private String store_name,storeid,landmark,mobile,ownerName,category,mapperId;
    double latitude,longitude,distance;
    boolean isComplete;
    int surveyStatus=0;
    public StoreData(String store_name, double latitude, double longitude, double distance, String storeid, String landmark, String mobile, String ownerName, String category, boolean isComplete,String mapperId) {
        this.store_name = store_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance=distance;
        this.storeid=storeid;
        this.landmark=landmark;
        this.mobile=mobile;
        this.ownerName=ownerName;
        this.category=category;
        this.isComplete=isComplete;
        this.mapperId=mapperId;
    }
    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
    public StoreData(String store_name, double latitude, double longitude, double distance, String storeid, String landmark, String mobile, String ownerName, String category, boolean isComplete,String mapperId,int surveyStatus) {
        this.store_name = store_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance=distance;
        this.storeid=storeid;
        this.landmark=landmark;
        this.mobile=mobile;
        this.ownerName=ownerName;
        this.category=category;
        this.isComplete=isComplete;
        this.mapperId=mapperId;
        this.surveyStatus=surveyStatus;
    }

    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public String getMapperId() {
        return mapperId;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
