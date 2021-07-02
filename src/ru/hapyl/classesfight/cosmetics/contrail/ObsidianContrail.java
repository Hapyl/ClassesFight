package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Material;
import ru.hapyl.classesfight.cosmetics.BlockContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class ObsidianContrail extends BlockContrail {

    public ObsidianContrail() {
        super("Strong Move", "Not that strong after all.", 1000, Material.OBSIDIAN, ShopItemRarity.EPIC, Material.OBSIDIAN, Material.CRYING_OBSIDIAN);
    }

}
