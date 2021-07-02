package ru.hapyl.classesfight.cosmetics.prefix;

import org.bukkit.Material;
import ru.hapyl.classesfight.cosmetics.Prefix;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class Fighter extends Prefix {
    public Fighter() {
        super("Fighter", "Show everyone who you really are!", "&a[&lFighter&a]", 100, Material.WOODEN_SWORD, ShopItemRarity.COMMON);
    }
}
