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

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.MultiPlayerParticle;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.entity.Entities;

import java.util.*;

public class StarClass extends IClass {

	private final Map<Player, Set<LivingEntity>> playerStars;

	private final double SLASH_DAMAGE_DISTANCE = 2.0d;
	private final double SLASH_DAMAGE = 2.5d;

	private final int MAX_STARS = 5;
	private final int SOTS_DELAY = 800;

	private final double THOUSANDS_SLASH_DAMAGE = 0.5d;

	public StarClass() {
		super("Vortex", Material.NETHER_STAR);

		this.setRating(ClassRating.S);
		this.setLvlRequired(2);
		this.setRole(ClassRole.STRATEGIST);
		this.setInfo("A young boy with power of speaking to stars...__",
				"__&e○ Sword of Thousands Stars &7A sword with ability to summon thousands stars that follows your crosshair! These stars rapidly hit and knocking enemies back.____&e○ Placeable Star &7Click to place star on your current location. (Use near already placed star to pick it up)____&e○ Star Aligner &7Align two stars by standing near one of them then teleport to the next closest, slashing the line between them.",
				"All the Stars",
				"Instantly creates &b10 &7stars around you, " +
						"then rapidly slash between them dealing double the damage, after, " +
						"performs the final blow with &b360° &7attack that applies slowness to enemies. " +
						"This will not affect already placed stars.", 6);

		this.setUltimateSound(Sound.ENTITY_WITHER_HURT, 0.75f);
		this.playerStars = new HashMap<>();
		this.buildItems();

	}

	private void buildItems() {
		final ClassEquipment equipment = this.getClassEquipment(true);

		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTc1NGFlY2Y3Yjk3NzFlZDhkODUyZjVmMTY0YWJlYjgxODgxYWRhNzEzNTU3NjJkZmQ1OTEzMThkMDQzIn19fQ==");
		equipment.setChestplate(102, 51, 0);
		equipment.setLeggings(179, 89, 0);
		equipment.setBoots(255, 140, 26);

		equipment.addItem(new CFItemBuilder(Material.STONE_SWORD, "vortex_sots").setName("&aSword of Thousands Stars &e&lCLICK")
				.setPureDamage(2.0d)
				.addClickEvent(this::launchSots)
				.applyDefaultSettings(false)
				.build());
		equipment.addItem(new CFItemBuilder(Material.NETHER_STAR, "vortex_star").setName("&aStar").addClickEvent(this::placeStar).build());
		equipment.addItem(new CFItemBuilder(Material.BEETROOT_SEEDS, "vortex_link").setName("&aStar Aligner").addClickEvent(this::blinkStar).build());
	}

	private void launchSots(Player player) {
		if (player.hasCooldown(Material.STONE_SWORD)) {
			return;
		}

		// Spam Fix
		player.setCooldown(Material.STONE_SWORD, SOTS_DELAY);
		final Location location = player.getEyeLocation();

		new GameTask() {

			private double distanceFlew = 0.0d;
			final double distanceShift = 0.5d;
			final double maxDistance = 100;

			@Override
			public void run() {

				final Location nextLocation = location.add(player.getEyeLocation().getDirection().multiply(distanceShift));
				PlayerLib.spawnParticle(nextLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);

				if ((distanceFlew % 5) == 0) {
					PlayerLib.playSound(nextLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
				}

				GameUtils.getEntitiesInRange(nextLocation, SLASH_DAMAGE_DISTANCE).forEach(entity -> {
					if (entity != player) {
						if (!(entity instanceof LivingEntity)) {
							return;
						}
						((LivingEntity)entity).setMaximumNoDamageTicks(0);
						if (entity instanceof Player) {
							DamageFeature.damage((Player)entity, player, THOUSANDS_SLASH_DAMAGE, EnumDamageCause.SOTS, true);
						}
						else {
							((LivingEntity)entity).damage(THOUSANDS_SLASH_DAMAGE, player);
						}
						((LivingEntity)entity).setMaximumNoDamageTicks(20);
					}
				});

				if (((distanceFlew += distanceShift) >= maxDistance) || nextLocation.getBlock().getType().isOccluding()) {
					if (!GameManager.current().isDebugMode()) {
						player.setCooldown(Material.STONE_SWORD, SOTS_DELAY);
					}
					this.cancel();
				}

			}
		}.runTaskTimer(0, 1);

	}

	@Override
	public void onStart(Player player) {
		if (!GameManager.current().isDebugMode()) {
			player.setCooldown(Material.STONE_SWORD, SOTS_DELAY / 2);
		}
		updateStarVisuals(player);
	}

	@Override
	public void onStopOnce() {
		this.playerStars.clear();
	}

	@Override
	public void useUltimate(Player player) {
		final double spreadDistance = 5.5d;
		final double halfSpreadDistance = spreadDistance / 2.0d;
		final Location location = player.getLocation();
		final Location[] allTheStars = {
				//up
				location.clone().add(0, spreadDistance, 0),
				//vert
				location.clone().add(spreadDistance, 0, 0), location.clone().add(-spreadDistance, 0, 0),
				location.clone().add(0, 0, spreadDistance), location.clone().add(0, 0, -spreadDistance),
				//cor
				location.clone().add(halfSpreadDistance, halfSpreadDistance, halfSpreadDistance), location.clone().add(-halfSpreadDistance, halfSpreadDistance, -halfSpreadDistance),
				location.clone().add(-halfSpreadDistance, halfSpreadDistance, halfSpreadDistance), location.clone().add(halfSpreadDistance, halfSpreadDistance, -halfSpreadDistance),
				//final
				location.clone()
		};

		new GameTask() {
			private int tick = 0;
			private int pos = 0;

			@Override
			public void run() {
				// draw circle
				if (tick % 10 == 0) {
					Geometry.drawCircle(location, spreadDistance, Quality.NORMAL, new WorldParticle(Particle.FIREWORKS_SPARK));
				}
				if (tick++ % 5 == 0) {
					// final slash
					if (pos >= (allTheStars.length - 1)) {
						performFinalSlash(location, player, spreadDistance);
						this.cancel();
						return;
					}
					StarClass.this.slash(allTheStars[pos], allTheStars[pos + 1], player, true);
					++pos;
				}


			}
		}.runTaskTimer(0, 1);

	}

	private void performFinalSlash(Location location, Player executor, double rad) {
		final World world = location.getWorld();
		if (world == null) {
			return;
		}

		for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
			double x = (rad * Math.sin(i));
			double z = (rad * Math.cos(i));
			location.add(x, 0, z);

			// fx
			world.spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
			world.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 10, 0.75f);

			// damage
			final Collection<Entity> entities = world.getNearbyEntities(location, SLASH_DAMAGE_DISTANCE, SLASH_DAMAGE_DISTANCE, SLASH_DAMAGE_DISTANCE);

			if (!entities.isEmpty()) {
				for (Entity entity : entities) {
					if (entity == executor) {
						continue;
					}
					if (entity instanceof Player) {
						if (Spectator.isSpectator((Player)entity)) {
							continue;
						}
						((Player)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
						DamageFeature.damage((Player)entity, executor, 0.5d, EnumDamageCause.ENTITY_ATTACK, true);
						return;
					}
				}
			}

			location.subtract(x, 0, z);
		}

	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {

				if (StarClass.this.playerStars.isEmpty()) {
					return;
				}

				StarClass.this.playerStars.forEach((player, star) -> {
					final MultiPlayerParticle particle = new MultiPlayerParticle(Particle.VILLAGER_HAPPY, player, Particle.CRIT);
					for (LivingEntity livingEntity : star) {
						particle.draw(livingEntity.getLocation().clone().add(0.0d, 0.25d, 0.0d));
					}
				});

			}
		}.runTaskTimer(0, 5);
	}

	private void slash(Location start, Location finish, Player executor, boolean ultimateStar) {
		GameUtils.tracePath(start, finish, 1.0d, SLASH_DAMAGE_DISTANCE, executor,
				touch -> DamageFeature.damage(touch, executor, (ultimateStar ?
						SLASH_DAMAGE * 2 :
						SLASH_DAMAGE), EnumDamageCause.STAR_SLASH, true), loc -> {
					final World world = loc.getWorld();
					if (world == null) {
						return; // ?
					}
					world.spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0, 0, 0, 0);
					world.playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 10, 0.75f);
				});
	}

	private void blinkStar(Player player) {
		if (hasLinkCooldown(player)) {
			return;
		}

		if (getPlayerStars(player).size() <= 1) {
			Chat.sendMessage(player, "&cThere must be at least 2 stars!");
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return;
		}

		final LivingEntity currentStar = getNearbyPlayerStar(player);

		if (currentStar == null) {
			Chat.sendMessage(player, "No nearby star?");
			return;
		}

		final Location currentStarLocation = currentStar.getLocation();
		final double distance = currentStarLocation.distance(player.getLocation());
		if (distance >= 3.5d) {
			Chat.sendMessage(player, "&cYou are too far away from a star!");
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return;
		}

		removePlayerStar(player, currentStar);
		updateStarVisuals(player);
		setLinkCooldown(player);
		final LivingEntity blinkStar = getNearbyPlayerStar(player);

		if (blinkStar == null) {
			Chat.sendMessage(player, "&cNo star to blink?");
			return;
		}

		final Location finalLocation = blinkStar.getLocation();
		finalLocation.setYaw(player.getLocation().getYaw());
		finalLocation.setPitch(player.getLocation().getPitch());
		player.teleport(finalLocation);
		PlayerLib.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);

		this.slash(currentStarLocation, finalLocation, player, false);

	}

	private void placeStar(Player player) {
		if (hasStarCooldown(player)) {
			return;
		}

		final Location location = player.getLocation();
		final LivingEntity nearStar = getNearbyPlayerStar(player);

		// pickup range is 5
		if (nearStar != null && (nearStar.getLocation().distance(location) <= 5)) {
			removePlayerStar(player, nearStar);
			updateStarVisuals(player);
			setStarCooldown(player, 60);
			Chat.sendMessage(player, "&aPicked up a star.");
			PlayerLib.playSound(player, Sound.BLOCK_BELL_RESONATE, 1.95f);
			return;
		}

		// check for star
		if (getPlayerStars(player).size() >= MAX_STARS) {
			Chat.sendMessage(player, "&cOut of stars!");
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return;
		}

		final Bat entity = createStarEntity(player);
		this.addPlayerStar(player, entity);
		this.updateStarVisuals(player);
		PlayerLib.playSound(player, Sound.BLOCK_BELL_USE, 1.75f);
		Chat.sendMessage(player, "&aCreated new star!");
		setStarCooldown(player, 40);

	}

	private boolean hasStarCooldown(Player player) {
		return player.hasCooldown(this.getClassEquipment().getItems().get(1).getType());
	}

	private boolean hasLinkCooldown(Player player) {
		return player.hasCooldown(this.getClassEquipment().getItems().get(2).getType());
	}

	private void setStarCooldown(Player player, int cd) {
		player.setCooldown(this.getClassEquipment().getItems().get(1).getType(), cd);
	}

	private void setLinkCooldown(Player player) {
		player.setCooldown(this.getClassEquipment().getItems().get(2).getType(), 5);
	}

	private void updateStarVisuals(Player player) {
		final Set<LivingEntity> stars = getPlayerStars(player);
		final PlayerInventory inventory = player.getInventory();

		final String lore = "This indicate how many stars you have placed. You may not place more than " + MAX_STARS + " stars at once!";
		final int size = stars.size();
		if (size == 0) {
			inventory.setItem(4, new ItemBuilder(Material.GRAY_DYE).setName("&aNo Stars").setSmartLore(lore).build());
		}
		else {
			inventory.setItem(4, new ItemBuilder(Material.GREEN_DYE).setName(String.format("&a%s Stars Placed", size))
					.setSmartLore(lore)
					.setAmount(size)
					.build());
		}
	}

	@NULLABLE
	private LivingEntity getNearbyPlayerStar(Player player) {
		final Location location = player.getLocation();
		final Set<LivingEntity> stars = this.getPlayerStars(player);

		if (stars.isEmpty()) {
			return null;
		}

		float dist = 0;
		LivingEntity star = null;

		for (LivingEntity entity : stars) {
			if (star == null) {
				star = entity;
				dist = (float)entity.getLocation().distance(location);
				continue;
			}
			final float distance = (float)entity.getLocation().distance(location);

			if (distance <= dist) {
				star = entity;
				dist = distance;
			}

		}

		return star;

	}

	private Bat createStarEntity(Player owner) {
		final Bat bat = (Bat)Entities.BAT.spawn(owner.getEyeLocation(), me -> {
			me.setSilent(true);
			me.setInvisible(true);
			me.setInvulnerable(true);
			me.setAI(false);
			me.setAwake(false);
			me.addScoreboardTag("vortex_star");
		});
		for (final Player online : Bukkit.getOnlinePlayers()) {
			if (online == owner) {
				continue;
			}
			Reflect.hideEntity(bat, online);
		}
		final Team team = getOrCreateTeam(owner);
		team.addEntry(bat.getUniqueId().toString());
		bat.setGlowing(true);
		return bat;
	}

	private Team getOrCreateTeam(Player player) {
		final Scoreboard score = player.getScoreboard();
		Team team = score.getTeam("STAR_TEAM");
		if (team == null) {
			team = score.registerNewTeam("STAR_TEAM");
			team.setColor(ChatColor.YELLOW);
		}
		return team;
	}

	private Set<LivingEntity> getPlayerStars(Player player) {
		return this.playerStars.getOrDefault(player, new HashSet<>());
	}

	private void addPlayerStar(Player player, LivingEntity star) {
		this.starOperation(player, star, true);
	}

	private void removePlayerStar(Player player, LivingEntity star) {
		this.starOperation(player, star, false);
		star.remove();
	}

	private void starOperation(Player player, LivingEntity star, boolean flag) {
		final Set<LivingEntity> set = this.getPlayerStars(player);
		if (flag) {
			set.add(star);
		}
		else {
			set.remove(star);
		}
		this.playerStars.put(player, set);
	}

}
