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

package ru.hapyl.classesfight.feature;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;

import java.util.function.Consumer;

public class AbilitiesCooldown {

	public static void add(final Player player, String name, String cdKey, int ticks, Consumer<Player> finish) {
		startCooldown(player, name, cdKey, ticks, finish);
	}

	public static void add(final Player player, final String name, final String cdKey, final int ticks, final ItemStack returnItem) {
		add(player, name, cdKey, ticks, g -> g.getInventory().addItem(returnItem));
	}

	public static void add(final Player player, String name, String cdKey, int ticks) {
		add(player, name, cdKey, ticks, (Consumer<Player>)null);
	}

	public static void startCooldown(Player player, String name, String id, int tick, Consumer<Player> whenFinished) {
		if (GameManager.current().isDebugMode()) {
			whenFinished.accept(player);
			return;
		}
		new Cooldown(player, id, name, tick) {
			@Override
			public void onStop() {
				whenFinished.accept(player);
			}
		};
	}

}
