package ru.hapyl.classesfight.parkour;

import org.bukkit.Material;

public enum ParkourBlockType {

    START, CHECKPOINT, FINISH;

    public Material getMaterial() {
        return this == CHECKPOINT ? Material.HEAVY_WEIGHTED_PRESSURE_PLATE : Material.LIGHT_WEIGHTED_PRESSURE_PLATE;
    }

}
