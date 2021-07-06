/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.Action;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.database.entry.Setting;

public class SettingsMenu extends PlayerGUI {

	public SettingsMenu(Player player) {
		super(player, "Settings", 6);
		this.setArrowBack("Main Menu", GameMenuGUI::new);
		this.updateMenu();
	}

	private void updateMenu() {

		for (Setting setting : Setting.values()) {

			final boolean isEnabled = setting.isEnabled(this.getPlayer());
			final String cuteName = Chat.capitalize(setting.getName());
			this.setItem(setting.getMenuPos(), this.buildMenuItem(setting));
			this.setItem(setting.getMenuPos() + 9, new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE).setName((isEnabled ? "&a" : "&c") + cuteName).setLore("&7Click to " + (isEnabled ? "disable" : "enable")).toItemStack());

			final Action action = player -> {
				setting.setEnabled(player, !isEnabled);
				Chat.sendMessage(player, "&a&lSettings: %s%s is now %s.", (!isEnabled ? "&a" : "&c"), cuteName, (!isEnabled ? "enabled" : "disabled"));
				PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
				this.updateMenu();
			};

			this.setClick(setting.getMenuPos(), action);
			this.setClick(setting.getMenuPos() + 9, action);

		}

		this.openInventory();

	}

	public ItemStack buildMenuItem(Setting setting) {
		final boolean isEnabled = setting.isEnabled(this.getPlayer());
		return new ItemBuilder(setting.getMenuIcon())
				.setName((isEnabled ? ChatColor.GREEN : ChatColor.RED) + Chat.capitalize(setting.name()))
				.addLore("&8This setting is currently " + (isEnabled ? "enabled" : "disabled"))
				.addLore()
				.addSmartLore(setting.getInfo())
				.addLore()
				.addLore("&eClick to " + (isEnabled ? "disable" : "enable"))
				.toItemStack();
	}

}
