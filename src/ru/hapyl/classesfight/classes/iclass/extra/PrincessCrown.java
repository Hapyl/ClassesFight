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

package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.entity.Entities;

public interface PrincessCrown {
	default void playCrownAnimation(Player player, final int maxTick, Action<ArmorStand> onExplode) {
		final ArmorStand entity = (ArmorStand)Entities.ARMOR_STAND.spawn(player.getLocation().add(0.0d, 0.25d, 0.0d), me -> {
			me.setMarker(true);
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setVisible(false);
			me.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
		});

		new GameTask() {
			private int currentTick = 0;

			@Override
			public void run() {

				if (currentTick++ >= maxTick) {
					this.cancel();
					onExplode.use(entity);
					entity.remove();
					return;
				}

				final Location location = entity.getLocation();
				location.add(0.0d, Numbers.clamp(0.7d - ((double)currentTick / maxTick), 0.025d, 0.7d), 0.0d);
				location.setYaw(location.getYaw() + 10.0f);
				entity.teleport(location);

			}
		}.runTaskTimer(0, 1);

	}
}
