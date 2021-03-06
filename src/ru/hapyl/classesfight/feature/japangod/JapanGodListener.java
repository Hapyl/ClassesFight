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

package ru.hapyl.classesfight.feature.japangod;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.Spectator;

import java.util.List;
import java.util.stream.Collectors;

public class JapanGodListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void handleItemPickup(PlayerMoveEvent ev) {

		if (!GameManager.current().isGameInProgress()) {
			return;
		}

		final JapanGod japanGod = GameManager.current().getJapanGod();
		if (japanGod == null) {
			return;
		}

		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		final Location to = ev.getTo();

		if (Spectator.isSpectator(player)) {
			return;
		}

		// Reduce calls I guess, only test if moved at last 0.5 blocks
		if (movedAtLeast(from, to)) {

			if (japanGod.getRelicAmount(player) >= japanGod.relicsNeededForBlessing) {
				return;
			}

			final List<Entity> collect = player.getNearbyEntities(1.5, 3, 1.5)
					.stream()
					.filter(entity -> entity instanceof ArmorStand)
					.filter(entity -> entity.getScoreboardTags().contains(japanGod.relicScoreboardTag))
					.collect(Collectors.toList());

			if (!collect.isEmpty()) {
				final Entity nearestStand = collect.get(0);
				japanGod.addRelic(player, 1);
				PlayerLib.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1.2f);
				japanGod.removeByStand((ArmorStand)nearestStand);
			}

		}
	}

	private boolean movedAtLeast(Location from, Location to) {
		if (from == null || to == null) {
			return false;
		}
		final double length = 0.1;
		return to.getX() - from.getX() >= length || to.getY() - from.getY() >= length || to.getZ() - from.getZ() >= length;
	}

}
