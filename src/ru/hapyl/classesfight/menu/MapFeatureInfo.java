package ru.hapyl.classesfight.menu;

public class MapFeatureInfo {

    private final String name, info;

    public MapFeatureInfo(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }
}