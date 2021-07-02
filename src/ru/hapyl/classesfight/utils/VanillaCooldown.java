package ru.hapyl.classesfight.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VanillaCooldown {

    public static final VanillaCooldown CONTRAIL_ENTRY = new VanillaCooldown(Material.MUSIC_DISC_CAT, 2);

    private final Material material;
    private final int cooldown;

    public VanillaCooldown(Material material, int cooldown) {
        this.material = material;
        this.cooldown = cooldown;
    }

    public static boolean hasCooldown(Player player, Material material) {
        return player.hasCooldown(material);
    }

    public static void setCooldown(Player player, Material material, int cooldown) {
        player.setCooldown(material, cooldown);
    }

    public static void setCooldown(Player player, VanillaCooldown cooldown) {
        setCooldown(player, cooldown.getMaterial(), cooldown.getCooldown());
    }

    public static boolean hasCooldown(Player player, VanillaCooldown cooldown) {
        return hasCooldown(player, cooldown.getMaterial());
    }

    public Material getMaterial() {
        return material;
    }

    public int getCooldown() {
        return cooldown;
    }
}
