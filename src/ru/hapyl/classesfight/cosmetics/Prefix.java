package ru.hapyl.classesfight.cosmetics;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class Prefix extends AbstractEffect {

    private final String prefix;

    public Prefix(String name, String lore, String prefix, long cost, Material icon, ShopItemRarity rarity) {
        super(name, lore, cost, icon, rarity);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public final void display(Player player) {
        Chat.sendMessage(player, "&a%s Prefix Preview: &r%s", this.getName(), this.prefix);
    }
}
