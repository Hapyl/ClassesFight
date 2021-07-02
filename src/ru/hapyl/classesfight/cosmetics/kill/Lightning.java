package ru.hapyl.classesfight.cosmetics.kill;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class Lightning extends AbstractEffect {

    public Lightning() {
        super("Lightning Strike", "Strikes a lightning effect.", 100, Material.NETHER_STAR, ShopItemRarity.RARE);
    }

    @Override
    public void display(Player player) {
        player.getWorld().strikeLightningEffect(player.getLocation());
    }
}
