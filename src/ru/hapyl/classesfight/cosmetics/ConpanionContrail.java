package ru.hapyl.classesfight.cosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class ConpanionContrail extends AbstractEffect {

    private final String texture;

    public ConpanionContrail(String texture, String name, String lore, long cost, Material icon, ShopItemRarity rarity) {
        super(name, lore + "____&6Companions are little flying things that follow you in the battlefield!", cost, icon, rarity);
        this.texture = texture;
    }

    public void spawnCompanion(Player player) {

    }

    public void removeCompanion(Player player) {

    }

    @Override
    public void display(Player player) {

    }

}
