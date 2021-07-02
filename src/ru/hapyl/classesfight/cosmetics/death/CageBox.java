package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.Material;
import org.bukkit.Sound;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class CageBox extends Box {
    public CageBox() {
        super(Material.SPAWNER, Sound.BLOCK_ANVIL_PLACE, 0.75f, 60, "Caged", "Cage yourself before you'll go...", 500, Material.SPAWNER, ShopItemRarity.RARE);
    }
}
