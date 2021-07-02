package ru.hapyl.classesfight.menu.collectibles;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.EnumEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemShop {

    private static final Map<UUID, ItemShop> itemShops = new HashMap<>();

    private int reRollCount;
    private EnumEffect[] items;

    private ItemShop(Player player) {
        this.generateItems();
        itemShops.put(player.getUniqueId(), this);
    }

    public int getReRollCount() {
        return reRollCount;
    }

    public void incrementReRollCount() {
        ++reRollCount;
    }

    public void generateItems() {
        this.items = new EnumEffect[5];
        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = randomEffect();
        }
    }

    public EnumEffect[] getItems() {
        return items;
    }

    private EnumEffect randomEffect() {
        EnumEffect effect = EnumEffect.values()[ThreadLocalRandom.current().nextInt(EnumEffect.values().length)];
        return (hasEffect(effect) || !effect.availableInShop()) ? randomEffect() : effect;
    }

    public boolean hasEffect(EnumEffect effect) {
        for (EnumEffect item : this.items) {
            if (effect == item) {
                return true;
            }
        }
        return false;
    }

    public static ItemShop getShop(Player player) {
        final ItemShop shop = itemShops.getOrDefault(player.getUniqueId(), null);
        return shop == null ? new ItemShop(player) : shop;
    }

}
