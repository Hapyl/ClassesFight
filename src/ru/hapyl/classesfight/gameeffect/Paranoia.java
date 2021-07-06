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

package ru.hapyl.classesfight.gameeffect;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.Random;

public class Paranoia implements GameEffect {

	private final Sound[] DECOY_SOUNDS = {Sound.BLOCK_STONE_STEP, Sound.BLOCK_WOOD_STEP, Sound.ENTITY_PLAYER_HURT, Sound.AMBIENT_CAVE, Sound.ENTITY_PLAYER_BIG_FALL, Sound.ENTITY_PLAYER_SMALL_FALL};

	@Override
	public void onEffectStart(Player player, Object... extra) {
		OldPlayerLib.addPotionEffect(player, PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 50);
	}

	@Override
	public void onEffectStop(Player player) {
		// This needed for smooth fade-out
		OldPlayerLib.removePotionEffect(player, PotionEffectType.BLINDNESS);
		OldPlayerLib.addPotionEffect(player, PotionEffectType.BLINDNESS, 20, 1);
	}

	@Override
	public long getTaskDelay() {
		return 10L;
	}

	@Override
	public void affectTask(Player player) {

		// Display paranoia for all players but the viewer
		final Location spawnLocation = player.getLocation().clone().add(0, 1.7d, 0);
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online != player) {
				online.spawnParticle(Particle.SQUID_INK, spawnLocation, 5, 0.175d, 0.175d, 0.175d, 0.02d);
			}
		}

		// Get random location to play decoy sound
		Location location = player.getLocation();
		location.add(new Random().nextDouble() * 3, 0, new Random().nextDouble() * 3);
		location.subtract(new Random().nextDouble() * 3, 0, new Random().nextDouble() * 3);

		final Sound sound = DECOY_SOUNDS[new Random().nextInt(DECOY_SOUNDS.length)];
		player.playSound(location, sound, SoundCategory.MASTER, 50, sound == Sound.AMBIENT_CAVE ? 2 : 1);

	}

	@Override
	public String getEffectName() {
		return "Paranoia";
	}
}
