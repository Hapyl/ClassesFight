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

package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record PerfectSound(Sound sound, float pitch) {

	public void play(Location location) {
		PlayerLib.playSound(location, this.sound, this.pitch);
	}

	public void play(Player player) {
		PlayerLib.playSound(player, this.sound, this.pitch);
	}

	public static PerfectSound ps(Sound sound, float pitch) {
		return new PerfectSound(sound, pitch);
	}

}
