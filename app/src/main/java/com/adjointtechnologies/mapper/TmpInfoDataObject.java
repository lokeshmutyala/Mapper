package com.adjointtechnologies.mapper;

/**
 * Created by lokeshmutyala on 18-03-2018.
 */

public class TmpInfoDataObject {
    int totalStores,insideStores;
    String agentName,lastSync,agentId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }
}
