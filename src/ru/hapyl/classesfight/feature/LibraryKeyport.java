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

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LibraryKeyport {

	private final Map<BlockLocation, BlockLocation> portals;

	public LibraryKeyport() {
		this.portals = new HashMap<>();
		this.addAll();
	}

	private void addAll() {

		// Entrances
		final BlockLocation entrance0 = new BlockLocation(75, 21, 99);
		final BlockLocation entrance1 = new BlockLocation(90, 21, 79);
		final BlockLocation entrance2 = new BlockLocation(90, 21, 131);
		final BlockLocation entrance3 = new BlockLocation(108, 21, 78);
		final BlockLocation entrance4 = new BlockLocation(125, 21, 99);
		final BlockLocation entrance5 = new BlockLocation(75, 11, 99);
		final BlockLocation entrance6 = new BlockLocation(124, 11, 99);
		final BlockLocation entrance7 = new BlockLocation(108, 11, 78);

		// Exits
		this.portals.put(entrance0, new BlockLocation(79, 20, 99, -90, 0));
		this.portals.put(entrance1, new BlockLocation(90, 20, 84, 0, 0));
		this.portals.put(entrance2, new BlockLocation(90, 20, 126, 180, 0));
		this.portals.put(entrance3, new BlockLocation(108, 20, 84, 0, 0));
		this.portals.put(entrance4, new BlockLocation(119, 20, 99, 90, 0));
		this.portals.put(entrance5, new BlockLocation(81, 10, 99, -90, 0));
		this.portals.put(entrance6, new BlockLocation(117, 10, 99, 90, 0));
		this.portals.put(entrance7, new BlockLocation(108, 10, 84, 0, 0));
	}


	public boolean testPlayer(Player player) {
		final Location location = player.getLocation();
		for (final BlockLocation entrance : this.portals.keySet()) {
			double distance = 3.0d;
			if (entrance.toLocation().distance(location) <= distance) {
				final Location exit = getRandomExitAndMergePitch(entrance, player);
				player.teleport(exit);
				PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
				PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
				return true;
			}
		}
		return false;
	}

	private BlockLocation getRandomButSelf(BlockLocation enter) {
		final BlockLocation element = this.portals.values().toArray(new BlockLocation[]{})[ThreadRandom.nextInt(this.portals.values().size())];
		return this.portals.get(enter) == element ? getRandomButSelf(enter) : element;
	}

	private Location getRandomExitAndMergePitch(BlockLocation enter, Player player) {
		final Location location = getRandomButSelf(enter).toLocation(true);
		location.setPitch(player.getLocation().getPitch());
		return location;
	}

	public Set<BlockLocation> getEntrances() {
		return this.portals.keySet();
	}

}
