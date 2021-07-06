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

package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.ArrowShield;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Icons;
import ru.hapyl.classesfight.utils.Weapon;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class JujuArcher extends IClass implements Listener {

	private final Map<Arrow, Player> arrows;

	public JujuArcher() {
		super("Juju the Archer", Material.OAK_SAPLING);
		this.setInfo("A bandit from the depths of the jungle. Highly skilled in range combat.",
				Icons.abilityDot("Elusive Burst &e&lPASSIVE", "Fully charged shots while sneaking will infuse your arrow. Infused arrows explode in AoE upon hit.")
						+ Abilities.ARROW_SHIELD.getAbout() + Abilities.CLIMB.getAbout(),
				"Kiss of Death, Call of Thunder",
				"Creates a field of arrows at your target block, then, summons thunder to electrolyze everyone who is still in the zone.",
				7);
		this.setRole(ClassRole.RANGE);

		this.arrows = Maps.newHashMap();

		final ClassEquipment eq = this.getClassEquipment();
		eq.addItem(new Weapon(Material.BOW).withName("&aTwisted").withEnchant(Enchantment.ARROW_INFINITE, 1).build());

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRjZmY0NjU4OGYzOTQ5ODc5NzliN2RkNzcwYWRlYTk0ZDhlZTFmYjFmN2I4NzA0ZTFiYWY5MTIyN2Y2YTRkIn19fQ==");
		eq.setChestplate(62, 51, 40);
		eq.setLeggings(62, 51, 40);
		eq.setBoots(16, 13, 10);

		Abilities.ARROW_SHIELD.addItemIfExists(eq);
		Abilities.CLIMB.addItemIfExists(eq);

	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		final ArrowShield arrowShield = (ArrowShield)Abilities.ARROW_SHIELD.getAbility();
		if (arrowShield.getCharges(player) > 0) {
			arrowShield.removeCharge(player);
			event.setCancelled(true);
		}
	}

	@Override
	public void onStart(Player player) {
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				if (arrows.isEmpty()) {
					return;
				}
				arrows.forEach((arrow, player) -> PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0));
			}
		}.runTaskTimer(0, 3);
	}

	@EventHandler()
	public void handleProjectileLaunch(ProjectileLaunchEvent ev) {
		if (!(ev.getEntity() instanceof Arrow arrow)) {
			return;
		}
		if (!(arrow.getShooter() instanceof Player player)) {
			return;
		}

		if (GameUtils.isValidPlayer(player, ClassManager.JUJU) && player.isSneaking() && arrow.isCritical()) {
			arrows.put(arrow, player);
		}
	}

	@EventHandler()
	public void handleProjectileLand(ProjectileHitEvent ev) {
		if (!(ev.getEntity() instanceof Arrow arrow)) {
			return;
		}
		if (!arrows.containsKey(arrow)) {
			return;
		}

		final Player shooter = arrows.get(arrow);
		arrows.remove(arrow);
		createExplosion(shooter, arrow.getLocation());

	}

	private void createExplosion(Player player, Location location) {
		final double y = -1.5d;
		final double spread = 1.25d;
		location.add(0, 2, 0);
		spawnArrow(player, location, new Vector(-spread, y, 0));
		spawnArrow(player, location, new Vector(spread, y, 0));
		spawnArrow(player, location, new Vector(0, y, spread));
		spawnArrow(player, location, new Vector(0, y, -spread));
		spawnArrow(player, location, new Vector(spread, y, spread));
		spawnArrow(player, location, new Vector(spread, y, -spread));
		spawnArrow(player, location, new Vector(-spread, y, spread));
		spawnArrow(player, location, new Vector(-spread, y, -spread));
	}

	private void spawnArrow(Player player, Location location, Vector vector) {
		if (location.getWorld() == null) {
			return;
		}
		final Arrow arrow = location.getWorld().spawnArrow(location, vector, 0.75f, 0.25f);
		arrow.setDamage(4.0d);
		arrow.setShooter(player);
	}

	private final double ultimateRadius = 7.5d;

	@Override
	public void useUltimate(Player player) {
		final Location location = getTargetLocation(player);
		if (location == null || location.getWorld() == null) {
			return;
		}
		location.add(0.0d, 0.1d, 0.0d);
		setUsingUltimate(player, true);
		new GameTask() {
			private final World world = location.getWorld();
			private double theta = 0.0d;

			@Override
			public void run() {
				drawUltimateRadius(location);
				if (theta >= Math.PI * 2) {
					GameTask.runTaskTimerTimes((task, tick) -> {
						drawUltimateRadius(location);
						final double addX = ThreadLocalRandom.current().nextDouble(ultimateRadius);
						final double addZ = ThreadLocalRandom.current().nextDouble(ultimateRadius);
						final double finalAddX = ThreadRandom.nextBoolean() ? addX : -addX;
						final double finalAddZ = ThreadRandom.nextBoolean() ? addZ : -addZ;
						location.add(finalAddX, 0, finalAddZ);
						world.strikeLightning(location);
						location.subtract(finalAddX, 0, finalAddZ);
						if (tick == 0) {
							setUsingUltimate(player, false);
						}
					}, 3, 40);
					this.cancel();
					return;
				}

				final double x = ultimateRadius * Math.sin(theta);
				final double z = ultimateRadius * Math.cos(theta);

				location.add(x, 5, z);
				world.spawnArrow(location, new Vector(0.0d, -1.0d, 0.0d), 0.75f, 0f);
				location.subtract(x, 5, z);

				theta += Math.PI / 16;

			}
		}.runTaskTimer(0, 1);
	}

	private void drawUltimateRadius(Location location) {
		Geometry.drawCircle(location, ultimateRadius, Quality.VERY_HIGH, new WorldParticle(Particle.CRIT));
	}

	@Override
	public String predicateMessage() {
		return "No valid blocks in sight!";
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getTargetLocation(player) != null;
	}

	private Location getTargetLocation(Player player) {
		final Block targetBlockExact = player.getTargetBlockExact(20);
		return targetBlockExact == null ? null : targetBlockExact.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
	}
}
