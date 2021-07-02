package ru.hapyl.classesfight.skin;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class UpgradableWrappedSkin extends WrappedSkin implements Upgradable {

    private final Map<Integer, SkinUpgrade> upgrades = new HashMap<>();

    public UpgradableWrappedSkin(String headTexture, Color chestPlateColor, Color leggingsColor, Color bootsColor) {
        super(headTexture, chestPlateColor, leggingsColor, bootsColor);
    }

    public UpgradableWrappedSkin(ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots) {
        super(helmet, chestPlate, leggings, boots);
    }

    public void setUpgrade(int lvl, SkinUpgrade upgrade) {
        this.upgrades.putIfAbsent(lvl, upgrade);
    }

    public Map<Integer, SkinUpgrade> getUpgrades() {
        return upgrades;
    }
}
