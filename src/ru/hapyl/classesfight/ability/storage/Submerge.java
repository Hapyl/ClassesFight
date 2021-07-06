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

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.Locale;

public class Submerge extends Ability {
	public Submerge() {
		super("Submerge", "Swiftly submerge under ground and dash forward revealing a hidden shark fin that deals damage and knocks back nearby enemies.");
		this.setItem(Material.PRISMARINE_SHARD);
		this.setCooldownSec(10);
	}

	private final float[] finOffset = {0.2f, 1.8f, 0.2f};

	@Override
	public Response useAbility(Player player) {

		player.setAllowFlight(true);
		player.setFlying(true);

		final Location location = player.getLocation();
		final ArmorStand entity = GarbageCollector.spawnEntity(location.subtract(finOffset[0], finOffset[1], finOffset[2]), ArmorStand.class, me -> {
			me.setSilent(true);
			me.setMarker(true);
			me.setInvisible(true);
			me.setGravity(false);
			me.getEquipment().setHelmet(new ItemStack(this.getItem().getType()));
			me.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(45), Math.toRadians(90)));
		});

		PlayerLib.playSound(player.getLocation(), Sound.WEATHER_RAIN_ABOVE, 1.75f);
		PlayerLib.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 1.75f);
		GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, 17);
		GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, 17);

		/**
		 *  Kinda messy I guess
		 */
		GameTask.runTaskTimerTimes((task, tick) -> {

			final Location entityLocation = entity.getLocation();
			final Location fixedLocation = entityLocation.clone().add(finOffset[0], finOffset[1], finOffset[2]);
			final Vector vector = location.getDirection().setY(0.0d).multiply(0.5);
			final Location nextLocation = entityLocation.add(vector);

			// block on the way
			final Location nextCheckLocation = nextLocation.clone().add(0.0, 2.0d, 0.0d);

			// block in front hit detection
			if (!isPassable(nextCheckLocation.getBlock())) {
				task.cancel();
				entity.remove();
				player.setFlying(false);
				player.setAllowFlight(false);
				PlayerLib.playSound(nextLocation, Sound.AMBIENT_UNDERWATER_EXIT, 1.25f);
				player.teleport(entityLocation.subtract(vector).clone().add(0.0d, 1.5d, 0.0d));
				player.setVelocity(entityLocation.getDirection().setY(0.5d));
				return;
			}

			// go down if
			if (nextCheckLocation.clone().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) {
				nextLocation.subtract(0.0d, 1.0d, 0.0d);
			}

			player.teleport(nextLocation.clone().add(0.0d, 0.75d, 0.0d));
			entity.teleport(nextLocation);
			PlayerLib.spawnParticle(fixedLocation, Particle.BUBBLE_COLUMN_UP, 3, 0.2, 0.1, 0.2, 0.1f);
			PlayerLib.spawnParticle(fixedLocation, Particle.WATER_SPLASH, 1, 0.2, 0.1, 0.2, 0.1f);

			// test
			PlayerLib.spawnParticle(fixedLocation.clone()
					.add(fixedLocation.getDirection().setY(0.0d).setZ(0.0d)), Particle.WATER_BUBBLE, 3, 0, 0, 0, 0);
			PlayerLib.spawnParticle(fixedLocation.clone()
					.add(fixedLocation.getDirection().setY(0.0d).setX(0.0d)), Particle.WATER_BUBBLE, 3, 0, 0, 0, 0);

			// Hit detection
			GameUtils.getEntitiesInRange(fixedLocation, 1.0d).forEach(target -> {
				if (target == player) {
					return;
				}

				DamageFeature.damageEntity(target, player, 3.0d, EnumDamageCause.SUBMERGE);
				target.setVelocity(target.getLocation().getDirection().multiply(-1).setY(0.5d));

			});

			// Raise
			if (tick == 0) {
				entity.remove();
				player.setFlying(false);
				player.setAllowFlight(false);
				PlayerLib.playSound(nextLocation, Sound.AMBIENT_UNDERWATER_EXIT, 1.25f);
				player.teleport(nextLocation.clone().add(0.0d, 1.5d, 0.0d));
				player.setVelocity(nextLocation.getDirection().setY(0.5d));
			}

		}, 1, 20);

		return Response.OK;
	}

	private boolean isPassable(Block block) {
		final String name = block.getType().name().toLowerCase(Locale.ROOT);
		if (block.getType().isAir()) {
			return true;
		}

		return stringHasAny(name, "carpet", "rail", "trapdoor", "button", "door");
	}

	private boolean stringHasAny(String string, String... any) {
		string = string.toLowerCase(Locale.ROOT);
		for (String str : any) {
			if (string.contains(str.toLowerCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

}
