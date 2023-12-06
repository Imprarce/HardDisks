package com.example.harddisks.MainPages.HelpFunc;

public class DiskDataClass {
    public String imageUrl;
    public String model;
    public String manufacturerCode;
    public int capacityGB;

    public int speedInterface;
    public int rotationSpeed;
    public int cacheSizeMB;
    public boolean raidSupport;

    public DiskDataClass() {}

    public DiskDataClass(String imageUrl, String model, String manufacturerCode, int capacityGB, int speedInterface, int rotationSpeed, int cacheSizeMB, boolean raidSupport) {
        this.imageUrl = imageUrl;
        this.model = model;
        this.manufacturerCode = manufacturerCode;
        this.capacityGB = capacityGB;
        this.speedInterface = speedInterface;
        this.rotationSpeed = rotationSpeed;
        this.cacheSizeMB = cacheSizeMB;
        this.raidSupport = raidSupport;
    }

    public int getCacheSize() {
        return cacheSizeMB;
    }

    public int getCapacity() {
        return capacityGB;
    }

    public int getSpeedInterface() {
        return speedInterface;
    }

    public int getSpindleSpeed() {
        return rotationSpeed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public String getModel() {
        return model;
    }

    public boolean hasRaid(){
        return raidSupport;
    }

    public void setCacheSize(int cacheSizeMB) {
        this.cacheSizeMB = cacheSizeMB;
    }

    public void setCapacity(int capacityGB) {
        this.capacityGB = capacityGB;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setHasRaid(boolean raidSupport) {
        this.raidSupport = raidSupport;
    }

    public void setSpindleSpeed(int rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setSpeedInterface(int speedInterface) {
        this.speedInterface = speedInterface;
    }
}