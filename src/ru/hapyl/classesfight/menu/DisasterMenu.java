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
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.disaster.Disaster;
import ru.hapyl.classesfight.disaster.Disasters;

public class DisasterMenu extends PlayerGUI {
	public DisasterMenu(Player player) {
		super(player, "Disasters", 4);
		this.updateMenu();
	}

	private void updateMenu() {
		this.setCloseMenuItem(this.getSize() - 5);
		this.setArrowBack(this.getSize() - 6, new GameSettingMenu(getPlayer()));

		final SmartComponent smart = newSmartComponent();

		for (final Disasters value : Disasters.values()) {
			final Disaster disaster = value.getDisaster();
			final boolean enabled = disaster.isEnabled();
			smart.add(new ItemBuilder(disaster.getMaterial())
					.setName((enabled ? "&a" : "&c") + disaster.getName())
					.setSmartLore(disaster.getAbout())
					.addLore()
					.addLore("&eClick to " + (enabled ? "disable" : "enable"))
					.predicate(enabled, ItemBuilder::glow)
					.toItemStack(), (player) -> {
				disaster.setEnabled(!enabled);
				broadcastMessage("%s %s %s disaster.", player.getName(), (!enabled ? "enabled" : "disabled"), disaster.getName());
				updateMenu();
			});
		}

		smart.fillItems(this);
		this.openInventory();

	}

	private static void broadcastMessage(String message, Object... dot) {
		Chat.broadcast("&c&lDISASTERS &7" + Chat.format(message, dot));
	}

}
