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
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.extra.TamerData;
import ru.hapyl.classesfight.classes.iclass.extra.TamerPack;
import ru.hapyl.classesfight.classes.iclass.extra.TamerPacks;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashMap;
import java.util.Map;

public class TamerClass extends IClass implements Listener {

	private final Map<Player, TamerData> playerData = new HashMap<>();

	private final double LEASH_DAMAGE = 1.25d;

	public TamerClass() {
		super("Tamer", Material.FISHING_ROD);

		this.setRating(ClassRating.NOT_YET);
		this.setAttack(EnumInfo.LOW);
		this.setDefense(EnumInfo.LOW);
		this.setRole(ClassRole.STRATEGIST);

		this.setInfo("A former circus pet trainer, with pets that loyal to him only!", Icons.abilityDot("Lash", "A unique lash that was used to train all these pets."), "Mine o Ball",
				"Spawns a random pets around the Tamer. Pets can be recalled and respawned every &b30s&7.", 2);
		this.setUltimateSound(Sound.ENTITY_PARROT_IMITATE_BLAZE, 2.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setChestplate(ItemBuilder.leatherTunic(Color.fromRGB(14557974)).addEnchant(Enchantment.THORNS, 1).cleanToItemSack());
		eq.setLeggings(ItemBuilder.leatherPants(Color.fromRGB(3176419)).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).cleanToItemSack());
		eq.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(2490368))
				.addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, -0.15d, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET)
				.cleanToItemSack());

		eq.addItem(new CFItemBuilder(Material.FISHING_ROD).setName("&aLash")
				.setUnbreakable()
				.addEnchant(Enchantment.KNOCKBACK, 1)
				.applyDefaultSettings(false)
				.build());

	}

	private void lash(Player player) {

		//PlayerLib.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 2.0f);
		//final Location location = player.getLocation().add(0.0d, 1.0d, 0.0d);
		//final Vector direction = location.getDirection();
		//final ParticleBuilder particle = ParticleBuilder.redstoneDust(Color.fromRGB(26, 26, 0));
		//final ParticleBuilder particle2 = ParticleBuilder.redstoneDust(Color.fromRGB(123, 123, 30));
		//
		//final double sqrt2 = Math.sqrt(2.0d);
		//
		//for (double i = Math.PI / 2; i < Math.PI; i += 0.05d) {
		//	final double x = direction.getX() * i;
		//	final double y = direction.getY() + ((0.5d / sqrt2) * Math.cos(i));
		//	final double z = direction.getZ() * i;
		//	//final double x = i * direction.getX();
		//	//final double y = Math.sin(direction.getY() * i);
		//	//final double z = i * direction.getZ();
		//
		//	location.add(x, y, z);
		//	particle.display(location);
		//	particle2.display(location);
		//
		//}

		//
		//for (double i = Math.PI / 2; i < Math.PI; i += 0.05d) {
		//	double x = i * direction.getX();
		//	double y = Math.cos(direction.getY() * i) / (Math.PI / 4);
		//	double z = i * direction.getZ();
		//	location.add(x, y, z);
		//
		//	GameUtils.getPlayerInRange(location, 1.0d).forEach(range -> {
		//		if (player == range) {
		//			return;
		//		}
		//
		//		DamageFeature.damage(range, player, LEASH_DAMAGE, EnumDamageCause.ENTITY_ATTACK);
		//
		//	});
		//
		//	particle.display(location);
		//	particle2.display(location);
		//
		//}

	}

	@Override
	public void onStopOnce() {
		this.playerData.values().forEach(TamerData::removeMinions);
		this.playerData.clear();
	}

	@Override
	public void onStart(Player player) {
		this.playerData.put(player, new TamerData(player));
	}

	@Override
	public void onStartOnce() {
		// AI Controller
		new GameTask() {
			@Override
			public void run() {
				playerData.forEach((player, data) -> {
					final TamerPack pack = data.getPack();
					if (pack != null) {
						pack.updateName(player);
						pack.getEntities().forEach(garbageEntity -> {
							final Entity entity = garbageEntity.getEntity();
							final Location location = entity.getLocation();
							// teleport if minion is too far away
							if (location.distance(player.getLocation()) > 50) {
								entity.teleport(player);
							}
							// if entity doesn't have a target then agro to a nearest player
							final LivingEntity creatureTarget = ((Creature)entity).getTarget();
							if (creatureTarget == null || (creatureTarget instanceof Player && Spectator.isSpectator((Player)creatureTarget))) {
								Player target = null;

								for (Entity nearbyEntity : entity.getNearbyEntities(30, 30, 30)) {
									if (nearbyEntity instanceof Player && nearbyEntity != player && !Spectator.isSpectator((Player)nearbyEntity)) {
										if (target == null)
											target = (Player)nearbyEntity;
										else if (player.getLocation().distance(location)
												<= target.getLocation().distance(location))
											target = (Player)nearbyEntity;
									}
								}
								// if target is still null there is no players
								if (target == null) {
									return;
								}
								((Creature)entity).setTarget(target);
								entity.getWorld().spawnParticle(Particle.LAVA, location, 4, 0.2, 0.8, 0.2, 0);
								entity.getWorld().playSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, SoundCategory.HOSTILE, 20, 2);
								Bukkit.getPluginManager()
										.callEvent(new EntityTargetLivingEntityEvent(entity, target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER));
							}
						});
					}
				});
			}
		}.runTaskTimer(0, 20);

	}

	@EventHandler
	public void handleEntityDamage(EntityTargetLivingEntityEvent ev) {
		final Entity entity = ev.getEntity();
		final LivingEntity target = ev.getTarget();
		if (isTamerEntity(entity) && !(target instanceof Player)) {
			ev.setTarget(null);
			ev.setCancelled(true);
		}
	}

	private boolean isTamerEntity(Entity entity) {
		return entity.getScoreboardTags().contains("TAMER_ENTITY");
	}

	@Override
	public void useUltimate(Player player) {
		final TamerData data = getData(player);
		data.newTamerPack(TamerPacks.random());

	}

	public void removeMinions(Player player) {
		getData(player).removeMinions();
	}

	public TamerData getData(Player player) {
		return this.playerData.getOrDefault(player, new TamerData(player));
	}

	@EventHandler
	public void handleAgro(EntityTargetEvent ev) {
		final Entity entity = ev.getEntity();
		final Entity target = ev.getTarget();

		if (isTamerEntity(entity)) {
			if (target instanceof Player && getData((Player)target).hasPackAndEntityIsInPack(entity)) {
				ev.setCancelled(true);
				ev.setTarget(null);
			}
		}
	}

	@EventHandler
	public void handleMinionDamage(EntityDamageEvent ev) {
		final Entity entity = ev.getEntity();

		if (ev instanceof EntityDamageByEntityEvent) {
			final Entity damager = ((EntityDamageByEntityEvent)ev).getDamager();
			if (damager instanceof Player) {
				// No damage to own minion.
				if (getData((Player)damager).hasPackAndEntityIsInPack(entity)) {
					ev.setCancelled(true);
					ev.setDamage(0.0);
					((LivingEntity)entity).setNoDamageTicks(1);
					damager.sendMessage(ChatColor.RED + "You can't damage your own minion!");
				}
			}
		}

	}

	@EventHandler
	public void handleLash(ProjectileHitEvent ev) {
		if (ev.getEntity().getShooter() instanceof final Player player) {
			if (ClassManager.getClass(player) == ClassManager.TAMER) {
				player.setCooldown(Material.FISHING_ROD, 10);

				if (ev.getHitBlock() != null) {
					ev.getEntity().remove();
					return;
				}

				if (ev.getHitEntity() != null) {
					if (ev.getHitEntity() instanceof Player) {
						DamageFeature.damage((Player)ev.getHitEntity(), player, LEASH_DAMAGE, EnumDamageCause.LEASHED);
						//((Player) ev.getHitEntity()).damage(2.0);
						ev.getEntity().remove();
					}
				}
			}
		}
	}

}