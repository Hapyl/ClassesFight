package ru.hapyl.classesfight.cosmetics.kill;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class ScaryPoop extends AbstractEffect {

    public ScaryPoop() {
        super("Scary Pooprise", "&oIt was that scary?", 500, Material.COCOA_BEANS, ShopItemRarity.EPIC);
    }

    @Override
    public void display(Player player) {
        final Location location = player.getLocation();

        // Sound Fx
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_BURP, SoundCategory.RECORDS, 10, 1.25f);
        location.getWorld().playSound(location, Sound.ENTITY_HORSE_SADDLE, SoundCategory.RECORDS, 10, 1.75f);

        // Item Fx
        final Item item = location.getWorld().dropItem(location, new ItemStack(Material.COCOA_BEANS));
        item.setPickupDelay(7000);
        item.setCustomName(Chat.format("&c&l%s's Poop", player.getName()));
        item.setCustomNameVisible(true);
        item.setVelocity(new Vector(0.0d, 0.2d, 0.0d));
        GarbageCollector.add(item);
    }
}
