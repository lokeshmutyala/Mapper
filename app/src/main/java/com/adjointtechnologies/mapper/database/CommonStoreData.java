package com.adjointtechnologies.mapper.database;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.PostInsert;
import retrofit.http.PUT;

/**
 * Created by lokeshmutyala on 18-12-2017.
 */
@Entity
public interface CommonStoreData{
    @Key
    @Generated
    public int getId();

    public String getStoreId();

    public String getAudit_Id();

    public double getLatitude();

    public double getLongitude();

    public float getAccuracy();

    public String getStoreName();

    public String getLandmark();

    public String getMobileNo();

    public String getStoreCondition();

    public String getStoreType();

    public String getPermTemp();

    public String getOwnerName();

    public boolean getIsNearByTeaShop();

    public boolean getIsNearByWine();

    public boolean getIsNearByDhaba();

    public boolean getisNearByEducation();

    public boolean getIsNearByrailbus();

    @Nullable
    public boolean getIsNearByApartments();

    public boolean getIsNearByJunction();

    public boolean getIsNearByPetrolPump();

    public boolean getIsNearByTemple();

    public boolean getIsNearByHospital();

    public boolean getOtpSent();

    public boolean getOtpVerified();

    public boolean getOtpSent2();

    public boolean getOtpVerified2();

    public String getAlternateMobile();

    public String getSurveyTime();

    public boolean getSyncStatus();

    public boolean getIsGlow_Sign_Dealor_Board();

    public boolean getISNon_Lit_Dealor_Board();

    public boolean getIsInside();
}
