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

package ru.hapyl.classesfight.parkour;

import kz.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFightException;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.parkour.parkour.LobbyParkour;
import ru.hapyl.classesfight.parkour.parkour.RootsParkour;

import java.util.ArrayList;
import java.util.List;

public enum ParkourCourse {

	LOBBY(new LobbyParkour("Lobby")),
	ROOTS(new RootsParkour("The Roots"));

	private final Parkour parkour;
	private final List<Hologram> cachedHolograms;

	ParkourCourse(Parkour parkour) {
		validateParkour(parkour);
		this.parkour = parkour;
		this.parkour.spawnBlocks(Bukkit.getWorlds().get(0));
		this.cachedHolograms = new ArrayList<>();
		this.buildHolograms();
	}

	public Parkour getParkour() {
		return this.parkour;
	}

	private void buildHolograms() {

		final String parkourName = ChatColor.GREEN + this.parkour.getName();
		final Hologram startHologram
				= new Hologram().addLine(parkourName).addLine("&6&lSTART").create(normalize(this.parkour.getStart()));

		final Hologram finishHologram
				= new Hologram().addLine(parkourName).addLine("&6&lFINISH").create(normalize(this.parkour.getFinish()));

		this.cachedHolograms.add(startHologram);
		this.cachedHolograms.add(finishHologram);

		int currentCheckpoint = 1;
		for (BlockLocation checkpoint : this.parkour.getCheckpoints()) {
			final Hologram checkPointHolo = new Hologram().addLine(parkourName)
					.addLine(String.format("&7Checkpoint %s/%s", currentCheckpoint, parkour.getCheckpoints().size()))
					.create(normalize(checkpoint));
			cachedHolograms.add(checkPointHolo);
			++currentCheckpoint;
		}

	}

	private void validateParkour(Parkour parkour) throws ClassesFightException {
		if (parkour == null) {
			throw new ClassesFightException("Parkour cannot be null");
		}
		if (parkour.getStart() == null) {
			throw new ClassesFightException("Parkour start cannot be null");
		}
		if (parkour.getFinish() == null) {
			throw new ClassesFightException("Parkour finish cannot be null");
		}
	}

	private Location normalize(BlockLocation bl) {
		return bl.toLocation().clone().subtract(0, 0.75, 0);
	}

	public void showHolograms(Player player) {
		for (Hologram cachedHologram : this.cachedHolograms) {
			cachedHologram.show(player);
		}
	}

	public void hideHolograms(Player player) {
		for (Hologram cachedHologram : cachedHolograms) {
			cachedHologram.hide(player);
		}
	}

	public void destroyHolograms() {
		cachedHolograms.clear();
	}

	@Nullable
	public static ParkourCourse getByParkour(Parkour parkour) {
		for (ParkourCourse value : values()) {
			if (value.getParkour().equals(parkour)) {
				return value;
			}
		}
		return null;
	}

	public static Parkour getInstanceOf(int[] array) {
		for (ParkourCourse value : values()) {
			final Parkour parkour = value.getParkour();
			if (parkour.getStart().compare(array)) {
				return parkour;
			}
			else if (parkour.getFinish().compare(array)) {
				return parkour;
			}
			else {
				if (parkour.getCheckpoints().size() > 0) {
					for (BlockLocation checkpoint : parkour.getCheckpoints()) {
						if (checkpoint.compare(array)) {
							return parkour;
						}
					}
				}
			}
		}
		return null;
	}

}
