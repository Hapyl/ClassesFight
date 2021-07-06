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

package ru.hapyl.classesfight.cosmetics;

import com.google.common.util.concurrent.AtomicDouble;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Laser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashSet;
import java.util.Set;

public class LaserShow extends AbstractEffect {

	public LaserShow() {
		super("Laser Show", "It was all worth the show!", 2500, Material.END_ROD, ShopItemRarity.CURSED);
		this.lasers = new HashSet<>();
	}

	private final Set<Laser> lasers;

	@Override
	public void onStop() {
		this.lasers.forEach(Laser::remove);
		this.lasers.clear();
	}

	@Override
	public void display(Player player) {

		final Location spawnLocation = player.getLocation();
		PlayerLib.playSound(spawnLocation, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.25f);
		final AtomicDouble pitch = new AtomicDouble(0.0d);
		final AtomicDouble pitchFx = new AtomicDouble(0.0d);
		//npc.showAll();
		//npc.setDataWatcherByteValue(0, (byte)0x80);

		// Npc
		//        final NPC npc = new NPC(spawnLocation, player.getName(), player.getName());
		//        GameTask.runTaskTimerTimes((task, tick) -> {
		//    npc.teleport(npc.getLocation().getX(), npc.getLocation().getY() + 0.085d, npc.getLocation().getZ(), npc.getLocation().getYaw(), -90);
		//
		//    // Fx
		//    if (tick % 5 == 0) {
		//        pitchFx.addAndGet(0.035d);
		//        PlayerLib.playSound(npc.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, (float)(Math.min(1.0f + pitchFx.get(), 2.0f)));
		//        PlayerLib.playSound(npc.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, (float)(Math.min(1.0f + pitchFx.get(), 2.0f)));
		//        PlayerLib.playSound(npc.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, (float)(Math.min(1.0f + pitchFx.get(), 2.0f)));
		//    }
		//
		//    if (tick == 0) {
		//        npc.remove();
		//    }
		//}, 1, 7 * 20).addCancelEvent(npc::remove);

		// Lasers
		GameTask.runTaskTimerTimes((task, tick) -> {

			pitch.addAndGet(0.1d);
			final Location location = spawnLocation.clone();
			final Laser laser = new Laser(spawnLocation, spawnLocation);
			laser.spawn();
			lasers.add(laser);

			new GameTask() {

				private final float thisPitch = (float)pitch.get();
				private double theta = 0.0d;

				@Override
				public void run() {

					final double x = (1.0d + (theta / 10)) * Math.sin(theta);
					final double z = (1.0d + (theta / 10)) * Math.cos(theta);

					location.add(x, theta / 50, z);
					laser.move(spawnLocation, location);

					if ((theta += Math.PI / 24) >= Math.PI * 4) {
						PlayerLib.spawnParticle(location, Particle.FLASH, 1, 0, 0, 0, 0);
						PlayerLib.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, (float)Math.min(1.0d + thisPitch, 2.0f));
						laser.remove();
						this.cancel();
					}

					location.subtract(x, 0, z);

				}
			}.runTaskTimer(0, 1);

		}, 5, 10);

	}
}
