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

package ru.hapyl.classesfight.menu.clazz;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.menu.GameMenuGUI;

import java.util.Comparator;
import java.util.List;

public class ClassLegacySelectGUI extends PlayerGUI {

	private final List<ClassManager> classes;
	private final GUI from;

	public ClassLegacySelectGUI(Player player, GUI from) {
		super(player, "Class Select ▶ Legacy", 6);
		this.classes = ClassManager.getValidClasses();
		this.classes.sort(Comparator.comparingInt(a -> (a.isUnlocked(getPlayer()) ? 0 : 1)));
		this.from = from;
		this.updateMenu(0);
	}

	private void updateMenu(int start) {

		this.clearItems();
		this.setArrowBack("Main Menu", GameMenuGUI::new);

		// Buttons
		if (start >= 28) {
			this.setItem(this.getSize() - 6, ItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==").setName("&aPrevious Page").build());
			this.setClick(this.getSize() - 6, player -> this.updateMenu(start - 28));
		}

		if (this.classes.size() - (start + 28) >= 0) {
			this.setItem(this.getSize() - 4, ItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19").setName("&aNext Page").build());
			this.setClick(this.getSize() - 4, player -> this.updateMenu(start + 28));

		}

		for (int i = start, slot = 10; i < start + 28; ++i, slot += slot % 9 == 7 ? 3 : 1) {
			if (i >= this.classes.size()) {
				break;
			}

			final ClassManager clazz = this.classes.get(i);
			final boolean isUnlocked = clazz.isUnlocked(getPlayer());
			this.setItem(slot, clazz.getCachedInfo().getItem(getPlayer()));

			if (isUnlocked) {
				this.setClick(slot, player -> ClassManager.setClass(player, clazz, true));
			}
			else {
				this.setClick(slot, player -> {
					Chat.sendMessage(player, "&cThis class is not yet unlocked!");
					PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
				});
			}
		}

		this.openInventory();

	}

}
