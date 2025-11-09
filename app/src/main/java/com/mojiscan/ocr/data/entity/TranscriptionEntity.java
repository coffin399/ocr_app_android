package com.mojiscan.ocr.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transcriptions")
public class TranscriptionEntity {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    
    private String title;
    private String text;
    private String filename;
    private String processType; // "ocr" or "transcribe"
    private String model;
    private long timestamp;
    private double processingTime;

    public TranscriptionEntity() {
        this.timestamp = System.currentTimeMillis();
    }

    public TranscriptionEntity(String title, String text, String filename, String processType, String model, long timestamp, double processingTime) {
        this.title = title;
        this.text = text;
        this.filename = filename;
        this.processType = processType;
        this.model = model;
        this.timestamp = timestamp;
        this.processingTime = processingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(double processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptionEntity that = (TranscriptionEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

