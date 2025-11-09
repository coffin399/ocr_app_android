package com.mojiscan.ocr.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TranscribeResponse {
    private boolean success;
    private String text;
    private String filename;
    private String model;
    private String language;
    @SerializedName("processing_time")
    private double processingTime;
    private String timestamp;
    private List<TranscribeSegment> segments;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(double processingTime) {
        this.processingTime = processingTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<TranscribeSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<TranscribeSegment> segments) {
        this.segments = segments;
    }
}

