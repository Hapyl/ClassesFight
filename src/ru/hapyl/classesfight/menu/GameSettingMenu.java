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

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.GameSettings;

public class GameSettingMenu extends PlayerGUI {

	private final GameSettings settings;

	public GameSettingMenu(Player player) {
		super(player, "Game Settings", 3);
		this.settings = GameManager.current().getSettings();
		this.updateMenu();
	}

	private void updateMenu() {
		this.setCloseMenuItem(this.getSize() - 5);

		this.setItem(11, new ItemBuilder(Material.SKELETON_SKULL).setName("&aDisasters")
				.setSmartLore("Disasters can make your game more fun and hardcode!")
				.addLore()
				.addLore("&eClick to manage")
				.toItemStack());
		this.setClick(11, DisasterMenu::new);

		this.openInventory();
	}
}
