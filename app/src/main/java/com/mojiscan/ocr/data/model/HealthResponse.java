package com.mojiscan.ocr.data.model;

import com.google.gson.annotations.SerializedName;

public class HealthResponse {
    private String status;
    private String service;
    @SerializedName("models_available")
    private int modelsAvailable;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getModelsAvailable() {
        return modelsAvailable;
    }

    public void setModelsAvailable(int modelsAvailable) {
        this.modelsAvailable = modelsAvailable;
    }
}

