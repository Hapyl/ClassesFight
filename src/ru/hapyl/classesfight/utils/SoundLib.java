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

package ru.hapyl.classesfight.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.runnable.GameTask;

public final class SoundLib {

	public static void play(Player listener, Sound sound, float pitch) {
		play(listener, sound, pitch, 20);
	}

	public static void play(Player listener, Sound sound, float pitch, int volume) {
		listener.playSound(listener.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void play(Sound sound, float pitch, Player... listeners) {
		for (Player listener : listeners) {
			play(listener, sound, pitch);
		}
	}

	public static void play(Location loc, Sound sound, float pitch, int volume) {
		loc.getWorld().playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void play(Location loc, Sound sound, float pitch) {
		play(loc, sound, pitch, 20);
	}

	public static void playAndCut(Location loc, Sound sound, float pitch, int cutAfter) {
		play(loc, sound, pitch, 20);
		GameTask.runLater(() -> Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> player.stopSound(sound)), cutAfter);
	}

	public static void playAndCut(Player player, Sound sound, float pitch, int cutAfter) {
		play(player, sound, pitch, 20);
		GameTask.runLater(() -> player.stopSound(sound), cutAfter);
	}


}
