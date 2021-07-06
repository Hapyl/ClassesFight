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

import kz.hapyl.spigotutils.module.inventory.ChestInventory;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.function.Consumer;

public class ConfirmMenu {

	private static final ItemStack acceptButton = new ItemBuilder(Material.EMERALD_BLOCK).setName("&aAccept").toItemStack();
	private static final ItemStack cancelButton = new ItemBuilder(Material.REDSTONE_BLOCK).setName("&cCancel").toItemStack();

	public ConfirmMenu(Player player, String name, Consumer<Player> accept, Consumer<Player> cancel) {
		ChestInventory chest = new ChestInventory(name, 3);
		OldPlayerLib.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 1.25f);

		for (int i = 0; i < chest.getSize(); i++) {
			// accept button
			final int mod = i % 9;
			if (mod == 1 || mod == 2 || mod == 3) {
				chest.setItem(i, acceptButton, () -> accept.accept(player));
			}

			else if (mod == 5 || mod == 6 || mod == 7) {
				chest.setItem(i, cancelButton, () -> cancel.accept(player));
			}
		}
		chest.openInventory(player);
	}

	public ConfirmMenu(Player player, String name, Consumer<Player> accept, ChestInventory cancelMenuOpen) {
		this(player, name, accept, cancelMenuOpen::openInventory);
	}

}
