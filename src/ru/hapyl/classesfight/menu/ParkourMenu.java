package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.quest.eye.TheEyeMenu;

public class ParkourMenu extends PlayerGUI {
    public ParkourMenu(Player player) {
        super(player, 2 + GUI.getSmartMenuSize(ParkourCourse.values()), "The Eye", "Parkour Courses");
        this.setUpItems();
        this.setArrowBack(new TheEyeMenu(player));
        this.openInventory();
    }

    private void setUpItems() {
        final SmartComponent smart = this.newSmartComponent();
        for (ParkourCourse value : ParkourCourse.values()) {

            final Parkour parkour = value.getParkour();

            final ItemBuilder builder = new ItemBuilder(parkour.getIcon())
                    .setName(ChatColor.GREEN + parkour.getName() + " Challenges")
                    .setSmartLore("Complete parkour once to claim once coins time reward. Complete parkour challenges to claim additional rewards.");

            builder.addLore();
            builder.addLore("One time reward for " + parkour.getName());
            builder.addLore(" &e" + parkour.getCoinsReward() + " Coins");
            builder.addLore();
            builder.addLore(parkour.hasCompleteBefore(getPlayer()) ? "&aYou've already received one time reward!" : "&cYou haven't complete parkour yet!");
            builder.addLore();
            builder.addLore("&eClick to open Challenges");

            smart.add(builder.toItemStack(), pl -> new ParkourChallengeMenu(pl, parkour, this));

        }
        smart.fillItems(this);
    }

}
