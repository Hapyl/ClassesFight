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
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.GrimoireCooldownApplier;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.classes.iclass.inst.Instance;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

public class BlackHole extends Ability implements GrimoireCooldownApplier {
	public BlackHole() {
		super("Black Hole", "Creates a black hole at your target block. Pulling enemies in and dealing &b3&7/&b5&7/&b7&7/&b9&7 damage per second.");
		this.setItem(Material.BLACK_CANDLE);
	}

	@Override
	public Response useAbility(Player player) {

		final Block block = player.getTargetBlockExact(10);
		if (block == null) {
			return Response.error("&cNo valid target block!");
		}

		final Location location = block.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
		PlayerLib.spawnParticle(location, Particle.GLOW, 1, 0, 0, 0, 0);


		final double suckRadius = 3.0d;
		GameTask.runTaskTimerTimes((task, tick) -> {

			// FX
			final double tick60 = tick / 60d;
			final double tick16 = tick / 16d;
			final double tick120 = tick / 120d;
			for (double i = 0; i < Math.PI * 2; i += (Math.PI / 4)) {
				final double x = (tick16 * Math.sin(i + tick60));
				final double z = (tick16 * Math.cos(i + tick60));
				location.add(x, tick120, z);
				PlayerLib.spawnParticle(location, Particle.SPELL_WITCH, 1, 0, 0, 0, 0);
				//this.summonArmorStandAndAffect(location);
				location.subtract(x, tick120, z);
			}

			PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, (float)Math.min(1.2f + (tick120), 2.0f));

			GameUtils.getEntitiesInRange(location, suckRadius).forEach(entity -> {

				final Location entityLocation = entity.getLocation();
				if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand || entity instanceof FallingBlock || entity == player) {
					return;
				}

				entity.setVelocity(location.toVector()
						.subtract(entityLocation.toVector())
						.multiply(0.20d));

				if (tick % 20 == 0) {
					DamageFeature.damageEntity(entity, player, Instance.LIBRARIAN.calculateDamage(player, 3.0d), EnumDamageCause.BLACK_HOLE);
					PlayerLib.spawnParticle(entity.getLocation(), Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
					PlayerLib.playSound(entity.getLocation().add(0.0d, 1.0d, 0.0d), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
				}

			});

		}, 1, 60);

		Instance.LIBRARIAN.removeSpellItems(player, Abilities.BLACK_HOLE);
		GrimmoreBook.applyCooldown(player, this.getGrimmoreCooldown());
		return Response.OK;
	}

	@Override
	public int getGrimmoreCooldown() {
		return 60 * 20;
	}

	private void summonArmorStandAndAffect(Location location) {
		final GarbageEntity<ArmorStand> garbage = new GarbageEntity<>(ArmorStand.class);
		final FallingBlock falling = location.getWorld().spawnFallingBlock(location, Material.OBSIDIAN.createBlockData());
		falling.setHurtEntities(false);
		falling.setDropItem(false);
		falling.setTicksLived(20);
		falling.setGravity(false);
		GarbageCollector.add(falling);

		garbage.spawn(location, me -> {
			me.setSmall(true);
			me.setSilent(true);
			//me.setMarker(true);
			me.setInvisible(true);
			me.addPassenger(falling);
		});

		GameTask.runTaskTimerTimes((task, time) -> garbage.getEntity()
				.teleport(garbage.getEntity().getLocation().subtract(0.0d, 0.05d, 0.0d)), 1, 10);

		GameTask.runLater(() -> {
			falling.remove();
			garbage.kill();
		}, 10);

	}

}
