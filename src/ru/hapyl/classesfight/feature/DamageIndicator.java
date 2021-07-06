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

import kz.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.runnable.GameTask;

import java.text.DecimalFormat;
import java.util.Random;

public class DamageIndicator {

	private static final DecimalFormat decimalFormat = new DecimalFormat("##.##");

	public static void showDamage(Location at, double damage) {

		// don't show the fictional damage
		if (damage <= DamageFeature.abstractNullDamage) {
			return;
		}

		final Hologram hologram = new Hologram();
		hologram.addLine("&b" + decimalFormat.format(damage));
		hologram.create(randomizeLocation(at.clone().subtract(0, 1, 0)));
		hologram.showAll();

		new GameTask() {
			@Override
			public void run() {
				hologram.destroy();
			}
		}.addCancelEvent(new BukkitRunnable() {
			@Override
			public void run() {
				hologram.destroy();
			}
		}).runTaskLater(30);

	}

	private static Location randomizeLocation(Location in) {
		final double x = new Random().nextDouble() * 0.75;
		final double y = new Random().nextDouble() * 0.75;
		final double z = new Random().nextDouble() * 0.75;
		return in.clone().add(x, y, z);
	}


}
