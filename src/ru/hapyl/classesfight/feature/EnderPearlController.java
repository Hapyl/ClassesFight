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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlController implements Listener {

	public static final Map<UUID, ItemStack> savedMaterial = new HashMap<>();

	@EventHandler()
	public void handleProjectileThrow(ProjectileLaunchEvent ev) {
		// Auto-Generated
		final Projectile projectile = ev.getEntity();
		if (projectile instanceof EnderPearl
				&& projectile.getShooter() != null
				&& projectile.getShooter() instanceof Player
				&& projectile.getPassengers().isEmpty()) {
			// save item so we can give it back
			final Player player = (Player)ev.getEntity().getShooter();
			savedMaterial.put(player.getUniqueId(), ((EnderPearl)projectile).getItem());
		}
	}

	@EventHandler
	public void handleProjectileLand(ProjectileHitEvent ev) {
		final Projectile projectile = ev.getEntity();
		if (projectile instanceof EnderPearl
				&& projectile.getShooter() != null
				&& projectile.getShooter() instanceof Player
				&& projectile.getPassengers().isEmpty()) {
			final Player player = (Player)projectile.getShooter();

			player.eject();
			player.setNoDamageTicks(2);

			if (!isValidLocation(projectile.getLocation())) {
				projectile.remove();
				if (player.getGameMode() != GameMode.CREATIVE) {
					player.getInventory().addItem(getHash(player));
				}
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
				Chat.sendMessage(player, "&cYou cannot travel there using ender pearls!");
			}
			else {
				final Location location = projectile.getLocation();
				location.setYaw(player.getLocation().getYaw());
				location.setPitch(player.getLocation().getPitch());
				player.teleport(location);
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
			}

		}
	}

	public ItemStack getHash(Player player) {
		return savedMaterial.get(player.getUniqueId());
	}

	private boolean isValidLocation(Location location) {
		return isSafeBlock(location.getBlock()) && isSafeBlock(location.getBlock().getRelative(BlockFace.UP));
	}

	private boolean isSafeBlock(Block block) {
		final Material type = block.getType();
		final String typeName = type.name().toLowerCase();
		// allow air
		if (type.isAir()) {
			return true;
		}
		// allow signs
		if (typeName.contains("sign")) {
			return true;
		}
		// allow other types
		switch (type) {
			case WATER:
			case SCAFFOLDING: {
				return true;
			}
			default: {
				return false;
			}
		}
	}

}