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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityMountEvent;
import ru.hapyl.classesfight.ActionbarUI;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.AbilitiesCooldown;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashMap;
import java.util.Map;

public class BlastKnightClass extends IClass implements Listener {

	private final Map<Player, Horse> horseLoyal = new HashMap<>();
	private final Map<Player, Integer> shieldChargeLevel = new HashMap<>();

	private final ItemStack ITEM_SHIELD = new CFItemBuilder(Material.SHIELD).setUnbreakable().build();

	public BlastKnightClass() {
		super("Blast Knight", Material.SHIELD);
		this.setDefense(EnumInfo.HIGH);
		this.setRole(ClassRole.MELEE);
		this.setInfo("Royal Knight with high-end technology shield that can charge by absorbing damage. When charged, " +
						"explodes and creates &cNova Explosion &7that damaging and knocking back enemies. Refactory shield " +
						"time is &b10s&7.__" + Icons.ABILITY_DOT + "Spear &7A knight without a spear is not a knight! &6&lCLICK &7to dash forward and damage foes on your way.",
				"Royal Horse",
				"Summons the Royal Horse for &b60s&7. The horse is fast, strong and comfortable, so comfortable that it multiplies your damage!",
				6);

		this.setUltimateSound(Sound.ENTITY_HORSE_ANGRY, 1.1f);
		this.setLvlRequired(2);
		this.setRating(ClassRating.NOT_YET);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJkZmRlNmMyYzhmMGE3YWRmN2FlNGU5NDlhODA0ZmVkZjk1YzZiOTU2Mjc2N2VhZTZjMjJhNDAxY2QwMmNiZCJ9fX0=");
		eq.setChestplate(Color.BLUE);
		eq.setLeggings(Material.CHAINMAIL_LEGGINGS);
		eq.setBoots(Material.IRON_BOOTS);

		eq.addItem(new CFItemBuilder(Material.IRON_SWORD).setName("&aSword").setPureDamage(7.5d).build());
		eq.addItem(new CFItemBuilder(Material.TIPPED_ARROW, "bk_spear").setTippedArrowColor(Color.WHITE)
				.setName("&aSpear &6&lCLICK")
				.addClickEvent(this::spearDash)
				.withCooldown(100)
				.build());
		eq.addItem(new CFItemBuilder(Material.SPLASH_POTION).setName("&aSlowness Potion")
				.setPotionMeta(PotionEffectType.SLOW, 2, 3 * 20, Color.BLACK)
				.toItemStack());

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (this.isOnTheHorse(player)) {
			event.setDamage(event.getDamage() * 1.5d);
			final Player victim = event.getPlayer();
			victim.setVelocity(victim.getLocation().getDirection().normalize().multiply(-1));
		}
	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		if (player.isBlocking()) {
			this.addCharge(player, 1);
			this.breakShield(player);
			event.setCancelled(true);
		}
	}

	@Override
	public void onStopOnce() {
		horseLoyal.values().forEach(Entity::remove);
		horseLoyal.clear();
		shieldChargeLevel.clear();
	}

	private void spearDash(Player player) {

		PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.25f);
		player.setVelocity(player.getLocation().getDirection().setY(0.0d).multiply(1.5d));

		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick++ >= 15) {
					this.cancel();
					return;
				}

				GameUtils.getLivingEntitiesInRange(player.getLocation(), 1.5d).forEach(entity -> {
					if (entity != player && entity instanceof Player) {
						DamageFeature.damage((Player)entity, player, 1.0d, EnumDamageCause.ENTITY_ATTACK);
					}
				});

			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				ClassManager.getPlayersInClass(ClassManager.BLAST_KNIGHT).forEach(player -> ActionbarUI.getUI(player).setString(0, "&fShield Charge Level: &e%s&7/&e10", getCharge(player)));
			}
		}.runTaskTimer(0, 10);
	}

	@Override
	public void onStart(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setItem(40, ITEM_SHIELD);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600000, 0, true));
		shieldChargeLevel.put(player, 0);
	}

	@Override
	public void useUltimate(Player player) {
		spawnHorse(player);
	}

	public void addCharge(Player player, int i) {
		shieldChargeLevel.put(player, getCharge(player) + i);
	}

	public int getCharge(Player player) {
		return shieldChargeLevel.getOrDefault(player, 0);
	}

	public void breakShield(Player player) {
		if (shieldChargeLevel.getOrDefault(player, 0) >= 10) {
			player.getInventory().setItem(40, new ItemStack(Material.AIR));
			shieldChargeLevel.put(player, 0);
			player.sendMessage(ChatColor.YELLOW + "Your shield was broken while casting the Nova effect!");
			player.sendMessage(ChatColor.YELLOW + "Reassembling new shield for you.");
			explosionFx(player);
			playSound(player);

			GameUtils.getPlayerInRange(player.getLocation(), 10).forEach(ent -> {
				if (ent == player) {
					return;
				}
				DamageFeature.damage(ent, player, 20.0d, EnumDamageCause.NOVA_EXPLOSION);
				ent.setVelocity(ent.getLocation().getDirection().multiply(-2.0));
				playSound(player);
			});

			AbilitiesCooldown.add(player, "Shield Reassembling...", "bn.shield", 200, g -> {
				g.sendMessage(ChatColor.GREEN + "Shield has been reassembled!");
				g.getInventory().setItem(40, ITEM_SHIELD);
			});

		}

	}

	private void explosionFx(Player player) {
		Location loc = player.getLocation().clone().add(0, 1, 0);
		player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 50, 10, .5, 10, 0.5);
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 50, 10, .5, 10, 0.5);
		player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1, 0, .5, 0, 0);
	}

	private void playSound(Player player) {
		player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.MASTER, 10, 0);
		player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, SoundCategory.MASTER, 10, 0);
	}

	@EventHandler
	public void handleHorseInteract(EntityMountEvent ev) {
		if (ev.getEntity() instanceof Player && ev.getMount() instanceof Horse) {
			if (!horseLoyal.containsKey((Player)ev.getEntity()) || horseLoyal.get((Player)ev.getEntity()) != ev.getMount()) {
				ev.setCancelled(true);
				ev.getEntity().sendMessage(ChatColor.RED + "This is not your Horse!");
			}
		}
	}

	private void spawnHorse(Player player) {

		final Horse h = horseLoyal.get(player);
		if (h != null) {
			h.remove();
		}

		Horse horse = player.getWorld().spawn(player.getLocation(), Horse.class);

		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		horse.setHealth(100);

		horse.setColor(Horse.Color.WHITE);
		horse.setTamed(true);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setAdult();
		horse.setStyle(Horse.Style.WHITE);
		horse.setJumpStrength(1.2);

		horseLoyal.put(player, horse);

		new GameTask() {
			int currentTick = 60;

			@Override
			public void run() {
				if (horse.isDead()) {
					this.cancel();
					return;
				}

				// kill the horse
				if (currentTick-- <= 0) {
					horse.getPassengers().forEach(Entity::eject);
					Chat.sendMessage(player, "&aYou horse has gone!");
					final Location particleLocation = horse.getLocation().add(0, 1, 0);
					horse.getWorld().spawnParticle(Particle.SPELL_MOB, particleLocation, 20, 0.5, 0.5, 0.5, 0.1);
					horse.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, particleLocation, 10, 0.5, 0.5, 0.5, 0.2);
					horse.remove();
					this.cancel();
					return;
				}

				horse.setCustomName(Chat.format("&7[&a%s's Horse &7| &c%s❤&7 | &e%s&7]", player.getName(), Math.floor(horse.getHealth()), currentTick));
				horse.setCustomNameVisible(true);

			}
		}.runTaskTimer(0, 20);

	}

	public boolean isOnTheHorse(Player player) {
		return player.getVehicle() != null && player.getVehicle() instanceof Horse;
	}

}
