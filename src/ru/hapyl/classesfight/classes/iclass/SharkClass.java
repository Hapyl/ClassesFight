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

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SharkClass extends IClass implements Listener {

	private final int ultimateTime = 120;
	private final long delayBetweenHits = 1000L;
	private final Map<Player, Long> lastCrit;

	public SharkClass() {
		super("Shark", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0=");

		this.setRole(ClassRole.STRATEGIST);
		this.setRating(ClassRating.A);
		this.setInfo("Strong warrior from the &bDepth of Waters&7... not that scary, right? Well don't let it touch the water or you'll regret it.__" + Icons.ABILITY_DOT + "Sturdy Claws &e&lPASSIVE &7Your hits have &a10% &7chance to crit! Critical hits summons ancient creature from beneath that deals extra damage. This effect can occur once in &b" + delayBetweenHits / 1000 + "s&7.__" + Abilities.SUBMERGE.getAbout(),
				"Ocean Madness",
				"Creates a &bShark Aura &7that follow you for &b6s &7and imitates water.", 6);

		this.setUltimateSound(Sound.AMBIENT_UNDERWATER_ENTER, 0.0f);
		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0=");
		eq.setChestplate(116, 172, 204);
		eq.setLeggings(116, 172, 204);
		eq.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(116, 172, 204)).addEnchant(Enchantment.DEPTH_STRIDER, 5).cleanToItemSack());
		// B? a!

		eq.addItem(new CFItemBuilder(Material.QUARTZ).setName("&aClaws").setPureDamage(3.0d).applyDefaultSettings(false).build());
		Abilities.SUBMERGE.addItemIfExists(eq);

		this.lastCrit = new HashMap<>();

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		// critical hit
		if (new Random().nextFloat() >= 0.9f) {
			this.executeCriticalHit(event.getWhoGotDamaged(), player);
		}
	}

	public void executeCriticalHit(Entity entity, Player player) {
		final Long lastCritical = this.lastCrit.getOrDefault(player, 0L);
		if (lastCritical == 0L || (System.currentTimeMillis() - lastCritical >= delayBetweenHits)) {
			this.lastCrit.put(player, System.currentTimeMillis());
			GarbageCollector.spawnEntity(entity.getLocation(), EvokerFangs.class, me -> {
				me.setOwner(player);
				DamageFeature.damageEntity(entity, player, 2.0d, EnumDamageCause.FEET_ATTACK);
			});
		}
	}

	@Override
	public void onStart(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
	}

	@Override
	public void useUltimate(Player player) {

		this.setUsingUltimate(player, true, ultimateTime);
		player.setWalkSpeed(0.6f);
		applySharkBuff(player, ultimateTime);

		new GameTask() {

			private int tick = 0;

			@Override
			public void run() {

				if (tick++ >= ultimateTime) {
					player.setWalkSpeed(0.2f);
					this.cancel();
					return;
				}

				drawCircle(player.getLocation().clone().add(0, 1, 0), Particle.WATER_DROP, 3.5f, 100);
				drawCircle(player.getLocation().clone().add(0, 0, 0), Particle.WATER_SPLASH, 1f, 50);

			}
		}.runTaskTimer(0, 1);

	}

	private void drawCircle(Location loc, Particle particle, float rad, float max) {
		for (float angle = 0; angle < max; angle += 1) {
			double x = (rad * Math.sin(angle));
			double z = (rad * Math.cos(angle));
			PlayerLib.spawnParticle(loc.clone().add(x, 0, z), particle, 1, 0, 0, 0, 0);
		}
	}

	@EventHandler
	public void handleMove(PlayerMoveEvent ev) {
		if (GameManager.current().isGameInProgress()) {

			final Player player = ev.getPlayer();
			final Location loc = player.getLocation();

			if (ClassManager.getClass(player) == ClassManager.SHARK) {
				if (!isUsingUltimate(player)) {
					if (loc.clone().getBlock().getType() == Material.WATER
							|| loc.clone().add(0, -1, 0).getBlock().getType() == Material.WATER) {
						player.setWalkSpeed(0.6f);
						applySharkBuff(player, 20);
					}
					else {
						player.setWalkSpeed(0.2f);
					}
				}
			}
		}
	}

	private void applySharkBuff(Player player, int durationTick) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, durationTick, 1, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, durationTick, 1, true));
	}

}
