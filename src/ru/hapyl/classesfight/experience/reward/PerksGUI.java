package ru.hapyl.classesfight.experience.reward;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.experience.ExpGUI;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public class PerksGUI extends PlayerGUI {
    public PerksGUI(Player player) {
        super(player, "Perks", 4);
        this.updateMenu();
    }

    private void updateMenu() {

        this.setItem(31, new ItemBuilder(Material.ARROW).setName("&aGo Back").setLore("&7To Levelling").toItemStack(), ExpGUI::new);

        int slot = 10;
        for (Perk perk : Perk.getPerks(this.getPlayer())) {
            final boolean enabled = perk.isEnabled(this.getPlayer());
            final String enabledString = enabled ? "enabled" : "disabled";
            this.setItem(slot, new CFItemBuilder(Material.DIAMOND).setName("&a" + perk.getName()).setSmartLore(perk.getInfo()).addLore().addLore(enabled ? "&aThis perk is enabled!" : "&cThis perk is disabled!").build());
            this.setItem(slot + 9, new ItemBuilder(enabled ? Material.LIME_DYE : Material.GRAY_DYE).setName((enabled ? "&aEnabled" : "&cDisabled")).addSmartLore(String.format("%s perk is currently %s.", perk.getName(), enabledString)).addLore().addLore("&eClick to " + (enabled ? "disable" : "enable")).toItemStack());
            this.setClick(slot + 9, player -> {
                perk.setEnabled(player, !enabled);
                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                this.updateMenu();
            });
            slot++;
        }

        this.openInventory();
    }

}
