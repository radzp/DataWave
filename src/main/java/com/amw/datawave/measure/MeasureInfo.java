package com.amw.datawave.measure;

public class MeasureInfo {
    private String name;
    private String description;

    public MeasureInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}