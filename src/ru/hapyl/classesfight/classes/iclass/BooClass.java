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

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.Icons;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

import java.util.Collection;

public class BooClass extends IClass implements Listener {

	private final int SHADOW_SHIFT_CD = 200;
	private final int NIGHTMARE_TIME = 240;
	private final int PARANOIA_TIME = 60;


	public BooClass() {
		super("Nightmare", Material.WITHER_SKELETON_SKULL);
		this.setRating(ClassRating.A);
		this.setAttack(EnumInfo.HIGH);
		this.setDefense(EnumInfo.LOW);
		this.setRole(ClassRole.ASSASSIN);
		this.setInfo("A spirit from the worst dreams and nightmares, blinds enemies and strikes from behind!__" + Icons.ABILITY_DOT + "Paranoia &7Casts an energy blast in the direction you looking. When touches enemy applies blindness and plays decoy sounds for &b" + (PARANOIA_TIME / 20) + "s&7.__" + Icons.ABILITY_DOT + "&eShadow Shift &7Instantly teleports behind target player, but loses ability to move for the teleport time.", "Your Worst Nightmare", "Applies Paranoia to every enemy alive for &b" + (NIGHTMARE_TIME / 20) + "s&7.", 5);
		this.setUltimateSound(Sound.ENTITY_WITCH_CELEBRATE, 0.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzljNTVlMGU0YWY3MTgyNGU4ZGE2OGNkZTg3ZGU3MTdiMjE0ZjkyZTk5NDljNGIxNmRhMjJiMzU3Zjk3YjFmYyJ9fX0=");
		eq.setChestplate(50, 0, 153);
		eq.setLeggings(40, 0, 153);
		eq.setBoots(30, 0, 153);

		eq.addItem(new CFItemBuilder(Material.NETHERITE_SWORD).setName("&aBrigade")
				.setPureDamage(3.5)
				.applyDefaultSettings()
				.build());
		eq.addItem(new CFItemBuilder(Material.LEAD, "shadow_shift").setName("&aShadow Shift &e&lRIGHT CLICK")
				.addClickEvent(this::useShadowShift)
				.build());
		eq.addItem(new CFItemBuilder(Material.COAL, "paranoia_item").setName("&aParanoia &e&lRIGHT CLICK")
				.withCooldown(360)
				.addClickEvent(this::useParanoia)
				.build());
		// A

	}

	@Override
	public void onStart(Player player) {
	}

	@Override
	public void onPlayerRevealed(Player player) {
	}

	@Override
	public void useUltimate(Player player) {
		GameManager.current().forEachBut(who ->
				GameEffectManager.applyEffect(who, GameEffectType.PARANOIA, NIGHTMARE_TIME, true), player);
	}

	private void useParanoia(Player player) {
		if (player.hasCooldown(Material.CHARCOAL)) {
			return;
		}

		final ArmorStand stand = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0), ArmorStand.class, i -> {
			i.setInvulnerable(true);
			i.setVisible(false);
			i.setSmall(true);
			i.setMarker(true);
			i.getLocation().setYaw(player.getLocation().getYaw());
			i.getLocation().setPitch(player.getLocation().getPitch());
		});

		player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, SoundCategory.MASTER, 1000, 1);

		new GameTask() {

			private double currentIteration = 0;

			@Override
			public void run() {

				double MAX_DISTANCE = 2.5;
				double SHIFT = 0.1;
				if ((currentIteration += SHIFT) >= MAX_DISTANCE) {
					stand.remove();
					this.cancel();
					return;
				}

				// Teleport forward
				stand.teleport(stand.getLocation().add(stand.getLocation().getDirection()).multiply(1));

				// Fx
				stand.getWorld().spawnParticle(Particle.SQUID_INK, stand.getLocation(), 2, .1, .1, .1, 0.1);
				stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_ANVIL_STEP, SoundCategory.HOSTILE, 20, 1.5f);

				// Apply blindness
				stand.getLocation().getWorld().getNearbyEntities(stand.getLocation(), 2, 2, 2).iterator().forEachRemaining(entity -> {
					if (entity instanceof Player) {
						if (entity != player && !Spectator.isSpectator((Player)entity) && !GameEffectManager.playerHasEffect((Player)entity, GameEffectType.PARANOIA)) {
							GameEffectManager.applyEffect((Player)entity, GameEffectType.PARANOIA, PARANOIA_TIME);
						}
					}
				});

			}
		}.runTaskTimer(0, 1);

	}

	private void useShadowShift(Player player) {
		if (player.hasCooldown(Material.LEAD)) {
			return;
		}

		final TargetLocation behind = getLocationAndCheck0(player);

		if (behind.error == ErrorCode.NULL) {
			player.setCooldown(Material.LEAD, SHADOW_SHIFT_CD);

			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 20));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 20));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 250));

			player.teleport(behind.location);
			PlayerLib.playSound(behind.location, Sound.ENTITY_ENDERMAN_SCREAM, 1.0f);
			new ParticlesBuilder(Particle.EXPLOSION_NORMAL, behind.location, 0.1, 0.1, 0.1, 3, 0.04).show();

			return;
		}

		Chat.sendActionbar(player, "&cUnable to teleport, " + behind.error.errorMessage);
		PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);

	}

	private TargetLocation getLocationAndCheck0(Player player) {
		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();
		float radius = 1.25f;

		for (double i = 0; i < 50; i += 0.75) {

			double x = vector.getX() * i;
			double y = vector.getY() * i;
			double z = vector.getZ() * i;
			location.add(x, y, z);

			final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
			if (!entities.isEmpty()) {
				for (Entity target : entities) {
					if (target != player && target instanceof Player && !Spectator.isSpectator((Player)target)) {
						if (player.hasLineOfSight(target)) {
							final Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1).setY(0.0d));
							behind.setYaw(behind.getYaw());
							behind.setPitch(behind.getPitch());

							if (behind.getBlock().getType().isOccluding()) {
								return new TargetLocation(null, ErrorCode.OCCLUDING);
							}
							else {
								return new TargetLocation(behind, ErrorCode.NULL);
							}
						}
						else {
							return new TargetLocation(null, ErrorCode.NO_LOS);
						}
					}
				}
			}

			location.subtract(x, y, z);
		}

		return new TargetLocation(null, ErrorCode.NO_TARGET);
	}

	@EventHandler()
	public void handleEntityLeash(PlayerLeashEntityEvent ev) {
		// Auto-Generated
		if (GameManager.current().isGameInProgress()) {
			ev.setCancelled(true);
		}
	}

	public static class TargetLocation {

		private final Location location;
		private final ErrorCode error;

		TargetLocation(Location l, ErrorCode r) {
			this.location = l;
			this.error = r;
		}

		public ErrorCode getError() {
			return error;
		}

		public Location getLocation() {
			return location;
		}
	}

	public enum ErrorCode {

		NO_TARGET("no valid target!"),
		NO_LOS("no line of sight with target!"),
		OCCLUDING("location is not safe!"),
		NULL("");

		private final String errorMessage;

		ErrorCode(String s) {
			this.errorMessage = s;
		}
	}

}
