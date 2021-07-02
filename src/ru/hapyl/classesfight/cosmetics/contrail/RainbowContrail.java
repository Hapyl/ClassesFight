package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Material;
import ru.hapyl.classesfight.cosmetics.BlockContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class RainbowContrail extends BlockContrail {

    public RainbowContrail() {
        super("Rainbow", "There are all the colors!__&8Or are there?", -1, Material.RED_STAINED_GLASS, ShopItemRarity.LEGENDARY,
                Material.RED_STAINED_GLASS, Material.ORANGE_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                Material.LIGHT_BLUE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.BLACK_STAINED_GLASS);
    }
}
