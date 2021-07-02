package ru.hapyl.classesfight.quest.relic;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.quest.eye.TheEyeMenu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RelicMenu extends PlayerGUI {

    public RelicMenu(Player player) {
        super(player, "The Eye ➜ Relic Hunt", 2 + (RelicHunt.current().getTotalZonesScaled()));
//        this.countStuff();
        this.setArrowBack(this.getSize() - 5, new TheEyeMenu(player));
        this.setUpItems();
        this.openInventory();
    }

    private void setUpItems() {

        final SmartComponent smart = this.newSmartComponent();
        RelicHunt.current().getZones().forEach(zone -> smart.add(createGameMapIcon(zone)));
        smart.fillItems(this);

        this.setItem(this.getSize() - 3, createTotalRelicCountIcon());
        this.setItem(this.getSize() - 7, new ItemBuilder(Material.CHEST_MINECART).setName("&aRelic Hunt Rewards").setSmartLore("&7Trade your relics for unique cosmetic rewards and perks!").build(), t -> new RelicRewardMenu(t, this));

    }

    private ItemStack createGameMapIcon(GameMap next) {
        if (next == null) {
            return new ItemStack(Material.SWEET_BERRIES);
        }

        final RelicHunt current = RelicHunt.current();
        final Set<Relic> inZone = current.getByZone(next);
        final Set<Relic> foundInZone = current.getFoundIn(getPlayer(), next);

        final int found = foundInZone.size();
        double percent = ((double)(found * 20) / inZone.size());
        double percent100 = ((double)(found * 100) / inZone.size());

        return new ItemBuilder(next.getIcon())
                .setName(ChatColor.GREEN + next.getMapName())
                .addLore()
                .addSmartLore("Relics are hidden treasures found everywhere on the map, some of them might require playing certain class to gather!")
                .addLore()
                .addLore("There are %s &bRelics&7 in &a%s&7.", inZone.size(), next.getMapName())
                .addLore()
                // not found at att
                .addLoreIf("&cYou haven't found any Relics!", found == 0 && percent != 20)
                // found all of them
                .addLoreIf("You have found %s &bRelics&7.", found > 0 && percent != 20, found)
                // found some but neither all nor none
                .addLoreIf("&aYou found all the Relics!", percent == 20)
                // found
                .addLore(buildBars(percent) + " &e%s%%", new DecimalFormat("0.0").format(percent100))
                .toItemStack();
    }

    private ItemStack createTotalRelicCountIcon() {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.CREEPER_BANNER_PATTERN)
                .hideFlags()
                .setName("&aTotal Relics")
                .addLore("&8By Types")
                .addLore();
        for (RelicType value : RelicType.values()) {
            final int amount = RelicHunt.current().getRelicAmount(value);
            if (amount > 0) {
                final int found = RelicHunt.current().getFoundRelicAmount(value, getPlayer());
                String color = found == 0 ? Chat.RED : (found > 0 && found <= (amount / 2)) ? Chat.YELLOW : found < amount ? Chat.GREEN : Chat.GREEN_BOLD;
                itemBuilder.addLore(String.format("%s%s %s/%s", color, Chat.capitalize(value.name()), found, amount));
            }
        }
        return itemBuilder.toItemStack();
    }

    private String buildBars(double percent) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; ++i) {
            if (percent > i) {
                builder.append(ChatColor.GREEN);
            }
            else {
                builder.append(ChatColor.GRAY);
            }
            builder.append(ChatColor.ITALIC).append('■');
        }
        return builder.toString();
    }

}
