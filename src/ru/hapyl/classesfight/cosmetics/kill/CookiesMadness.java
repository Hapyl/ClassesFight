package ru.hapyl.classesfight.cosmetics.kill;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.concurrent.ThreadLocalRandom;

public class CookiesMadness extends AbstractEffect {

    public CookiesMadness() {
        super("Cookie Madness", "More cookies! Mo-o-ore!", 375, Material.COOKIE, ShopItemRarity.RARE);
    }

    @Override
    public void display(Player player) {
        final Location location = player.getLocation();
        for (int i = 0; i < 10; i++) {
            final Item item = location.getWorld().dropItemNaturally(location.clone().subtract(0.5d, -1.0d, 0.5d),
                    new ItemBuilder(Material.COOKIE).setName(ThreadLocalRandom.current().nextInt(100, 5000) + "").toItemStack());
            item.setPickupDelay(5000);
            item.setTicksLived(5940);
        }
    }
}
