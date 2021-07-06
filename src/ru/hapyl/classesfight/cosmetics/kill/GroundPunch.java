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

package ru.hapyl.classesfight.cosmetics.kill;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.concurrent.ThreadLocalRandom;

public class GroundPunch extends AbstractEffect implements Listener {

	public GroundPunch() {
		super("Ground Punch", "Me smash ground!", 750, Material.COBBLESTONE, ShopItemRarity.RARE);
	}

	@Override
	public void display(Player player) {

		final Location location = player.getLocation().clone().subtract(1, 1, 1);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 1 && j == 1) {
					continue;
				}
				location.add(i, 0, j);
				propelGround(location);
				location.subtract(i, 0, j);
			}
		}

		new GameTask() {
			@Override
			public void run() {
				location.subtract(1.0d, 0.0d, 1.0d).add(0.0d, 0.35d, 0.0d);
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 5; j++) {
						if (((i == 0 || i == 4) && j > 0 && j < 4) || j % 4 == 0) {
							location.add(i, 0, j);
							propelGround(location);
							location.subtract(i, 0, j);
						}
					}
				}
			}
		}.runTaskLater(2);

		new GameTask() {
			@Override
			public void run() {
				location.subtract(1.0d, 0.0d, 1.0d).add(0.0d, 0.35f, 0.0d);
				for (int i = 0; i < 7; i++) {
					for (int j = 0; j < 7; j++) {
						if ((i == 0 || i == 6) && (j == 0 || j == 6)) {
							continue;
						}
						if (i == 0 || i == 6 || j % 6 == 0) {
							location.add(i, 0, j);
							propelGround(location);
							location.subtract(i, 0, j);
						}
					}
				}
			}
		}.runTaskLater(4);


	}

	private void propelGround(Location location) {
		if (!location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
			return;
		}
		final Material block = location.getBlock().getType().isAir() ? Material.COBBLESTONE : location.getBlock().getType();
		final BlockData blockData = block.createBlockData();
		final FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location.clone().add(0.0d, 1.01d, 0.0d), blockData);
		fallingBlock.addScoreboardTag("Cosmetic");
		fallingBlock.setHurtEntities(false);
		fallingBlock.setDropItem(false);
		fallingBlock.setVelocity(new Vector(
				0.025f * ThreadLocalRandom.current().nextFloat(),
				0.5d,
				0.025f * ThreadLocalRandom.current().nextFloat()));
		final SoundGroup soundGroup = blockData.getSoundGroup();
		location.getWorld().playSound(location,
				soundGroup.getBreakSound(),
				soundGroup.getVolume() * 2,
				soundGroup.getPitch() + Math.max(0.0f, Math.min((0.1f * ThreadLocalRandom.current().nextFloat()), 2.0f)));
	}

	@EventHandler()
	public void handleEntityChangeBlockEvent(EntityChangeBlockEvent ev) {
		// Auto-Generated
		final Entity entity = ev.getEntity();
		if (entity instanceof FallingBlock && entity.getScoreboardTags().contains("Cosmetic")) {
			ev.setCancelled(true);
			entity.remove();
		}
	}

}
