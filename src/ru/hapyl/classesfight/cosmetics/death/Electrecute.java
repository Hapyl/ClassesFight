package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class Electrecute extends Box {

    public Electrecute() {
        super(Material.ICE, Sound.BLOCK_GLASS_BREAK, 0.0f, 40, "Electrocute", "Bzz~t", 1000, Material.LIGHT_BLUE_STAINED_GLASS, ShopItemRarity.LEGENDARY);
    }

    @Override
    public void displayExtra(Location location) {
        GameTask.runTaskTimerTimes((task, tick) -> location.getWorld().strikeLightningEffect(location.clone().add(0.0d, 2.0d, 0.0d)), 20, 2, 10);
    }
}
