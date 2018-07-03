package com.adjointtechnologies.mapper.database;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Nullable;

/**
 * Created by lokeshmutyala on 04-03-2018.
 */
@Entity
public interface ExtraStoreData {
    @Key
    @Generated
    public int getId();

    public String getStoreId();

    public String getAudit_Id();

    public boolean getIsSellCigarettes();

    @Nullable
    public boolean getIsSellBiscuits();

    @Nullable
    public boolean getIsSellChipsNamkeen();

    @Nullable
    public boolean getIsSellSoaps();

    @Nullable
    public boolean getIsSellDeodorants();

    @Nullable
    public boolean getIsSellBrandedAtta();

    @Nullable
    public boolean getIsSellNoodles();

    @Nullable
    public boolean getIsPepsiCokeCooler();

    @Nullable
    public boolean getIsCadburyNestleChocolateDispenser();

    public boolean getIsItcCigaretteSalesmanVisitStore();

    public boolean getIsItcNonCigaretteSalesmanVisitStore();

    public String getItcIsuue();

    public String getSurveyTime();

    public boolean getSyncStatus();

}
