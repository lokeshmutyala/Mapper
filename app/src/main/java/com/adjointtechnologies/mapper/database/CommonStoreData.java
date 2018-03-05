package com.adjointtechnologies.mapper.database;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.PostInsert;
import retrofit.http.PUT;

/**
 * Created by lokeshmutyala on 18-12-2017.
 */
@Entity
public interface StoreDataKhm {
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

    public boolean getIsSellCigar();

    public boolean getIsNearByTeaShop();

    public boolean getIsNearByWine();

    public boolean getIsNearByDhaba();

    public boolean getisNearByEducation();

    public boolean getIsNearByrailbus();

    public boolean getIsNearByJunction();

    public boolean getIsNearByPetrolPump();

    public boolean getIsNearByTemple();

    public boolean getIsNearByHospital();

    public boolean getIsSellEdition();

    public boolean getIsSellCigarettes();

    public boolean getIsItcSalesmanVisitStore();

    public boolean getIsItcCigaretteSalesmanVisitStore();

    public boolean getIsItcNonCigaretteSalesmanVisitStore();

    public boolean getOtpSent();

    public boolean getOtpVerified();

    public boolean getOtpSent2();

    public boolean getOtpVerified2();

    public String getAlternateMobile();

    public String getSurveyTime();

    public boolean getSyncStatus();

    public boolean getIsItcSalesMan();

    public boolean getIsGlow_Sign_Dealor_Board();

    public boolean getISNon_Lit_Dealor_Board();

    public boolean getIsInside();
}
