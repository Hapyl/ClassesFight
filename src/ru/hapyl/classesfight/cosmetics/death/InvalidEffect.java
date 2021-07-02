package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;

public class InvalidEffect extends AbstractEffect {

    public InvalidEffect() {
        super(ChatColor.RED + "Invalid Item", Integer.MIN_VALUE, Material.BARRIER, "Sorry, this item is unavailable! Sorry for the inconvenience.");
    }

    @Override
    public void display(Player player) {
        player.sendMessage("Cannot use that!");
    }
}
