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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnderClass extends IClass implements Listener {

	private final int PEARL_DELAY = 160;
	private final int PORT_KEY_DELAY = 160;
	private final int PORT_KEY_MAX_DISTANCE = 25;
	private final int BEACON_BROKE_DELAY = 600;

	public double HEALING_AMOUNT = 1.75d; // in half-hearts obviously
	private final double locationError = 1.3;

	private final ItemStack CLASS_ITEM = new CFItemBuilder(Material.ENDER_PEARL).setName("&aRideable Pearl")
			.setAmount(1)
			.applyDefaultSettings(false)
			.build();
	private final ItemStack BEACON_ITEM = new CFItemBuilder(Material.BEACON).setName("&aTransmission Beacon &e&lPLACE TO USE")
			.applyDefaultSettings()
			.build();
	private final ItemStack BEACON_HEART = new CFItemBuilder(Material.CONDUIT).setName("&aBeacon Heart")
			.setSmartLore("This heart is linked to your beacon, &6&lULT &7to link them together and teleport to the beacon.")
			.build();

	private final Map<Player, Entity> beaconPlaced = new HashMap<>();

	public EnderClass() {
		super("Ender", Material.ENDER_PEARL);
		this.setRating(ClassRating.A);
		this.setRole(ClassRole.ASSASSIN);
		this.setAttack(EnumInfo.HIGH);
		this.setInfo("Strong and Fast warrior with teleportaion ability anywhere he looks. Hits you with an arm but hurts like a brick.__&e○ Rideable Pearl &7Throws and rides an ender pearls that heales you for &c" + HEALING_AMOUNT + "❤ &7upon landing.____&e&e○ Transmission Beacon &7Place this beacon whenever you want, &6&lULT &7to teleport to the beacon and get it back. The beacon can be destroyed, but in that case you will have to wait to place it again.",
				"Transmission!",
				"Instantly teleports you to a placed transmission beacon and gives it back.",
				5);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWFjYjM1NzcwOWQ4Y2RmMWNkOWM5ZGJlMzEzZTdiYWIzMjc2YWU4NDIzNDk4MmU5M2UxMzgzOWFiN2NjNWQxNiJ9fX0=");
		eq.setChestplate(85, 0, 102);
		eq.setLeggings(128, 0, 128);
		eq.setBoots(136, 0, 204);

		eq.addItem(new CFItemBuilder(Material.SPECTRAL_ARROW, "teleport_ability").setName("&aPort Key &e&lRIGHT CLICK")
				.addClickEvent(this::usePortKey)
				.applyDefaultSettings()
				.build());
		eq.addItem(CLASS_ITEM);
		eq.addItem(BEACON_ITEM);

	}

	@Override
	public void onStart(Player player) {

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60000, 1, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60000, 1, true));

		new GameTask() {
			@Override
			public void run() {
				if (player.hasCooldown(Material.SPECTRAL_ARROW)) {
					return;
				}

				// Port Key visualization
				if (player.getInventory().getItemInMainHand().getType() == Material.SPECTRAL_ARROW) {
					final Location location = getTargetLocation(player, PORT_KEY_MAX_DISTANCE);
					if (location != null) {
						GeometryLib.drawCircle(location.add(0, 0.2, 0), 30, 1,
								new GeometryLib.TinyParticle(Particle.CRIT_MAGIC, 1, 0, 0, 0, 0), player);
						for (double d = 0; d < 0.3; d += 0.02) {
							player.spawnParticle(Particle.CRIT, location.add(0, d, 0), 1, 0, 0, 0, 0);
						}
					}
				}

				// Water damage controller
				if (player.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER
						|| player.getLocation().getBlock().getType() == Material.WATER) {

					DamageFeature.damage(player, 1d, EnumDamageCause.OTHER);
					player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_HURT, 1, 1.2f);
					player.sendTitle("", Chat.format("&cBeware of Water!"), 0, 10, 0);
				}
			}
		}.runTaskTimer(0, 5);

	}

	@Override
	public void onStopOnce() {
		beaconPlaced.values().forEach(Entity::remove);
		beaconPlaced.clear();
	}

	@EventHandler
	public void handleBeaconRemove(EntityDamageByEntityEvent ev) {

		final Entity entity = ev.getEntity();
		if (ev.getDamager() instanceof Player player && entity instanceof ArmorStand) {

			if (entity.getCustomName() == null || Spectator.isSpectator((Player)ev.getDamager())) {
				return;
			}

			Player owner;

			// NPE fix
			try {
				owner = Bukkit.getPlayer(UUID.fromString((entity.getCustomName())));
			}
			catch (IllegalArgumentException ex) {
				return;
			}

			if (beaconPlaced.get(owner) != null && owner != null) {
				SoundLib.play(player, Sound.BLOCK_GLASS_BREAK, 0);
				beaconPlaced.remove(owner);
				entity.remove();
				Chat.sendMessage(player, "&cYou broke %s's beacon!", owner.getName());
				Chat.sendTitle(owner, "", "&cBeacon Destroyed!", 10, 20, 10);
				returnBeacon(owner, BEACON_BROKE_DELAY);
			}

		}
	}

	private void returnBeacon(Player player, int coolDown) {
		final PlayerInventory inventory = player.getInventory();
		player.setCooldown(Material.BEACON, coolDown);
		inventory.setItem(2, BEACON_ITEM);
	}


	@EventHandler
	public void handlePlaceBeacon(BlockPlaceEvent ev) {

		final Player player = ev.getPlayer();
		final Location location = ev.getBlock().getLocation();

		final ItemStack mainHandItem = player.getInventory().getItemInMainHand();
		if (mainHandItem.isSimilar(BEACON_ITEM)) {

			ev.setCancelled(true);
			player.getInventory().setItemInMainHand(BEACON_HEART);

			ArmorStand stand = player.getWorld().spawn(location.add(0.5, -locationError, 0.5), ArmorStand.class, i -> {
				i.setSilent(true);
				i.setVisible(false);
				i.setGravity(false);
				i.setMaxHealth(2048);
				i.setHealth(2048);
				i.setCustomName(player.getUniqueId().toString());
				i.getEquipment().setHelmet(new ItemStack(Material.BEACON));
				GameUtils.lockArmorStandSlots(i);
			});

			Chat.sendMessage(player, "&aYou placed a Beacon! Use your ultimate to teleport.");
			beaconPlaced.put(player, stand);

		}

	}

	@Override
	public void useUltimate(Player player) {

		final Entity entity = beaconPlaced.get(player) == null ? player : beaconPlaced.get(player);

		final Location location = entity.getLocation().add(0, locationError, 0);
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		player.teleport(location);
		returnBeacon(player, 0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 20));
		entity.remove();
		beaconPlaced.remove(player);

	}

	@Override
	public String predicateMessage() {
		return "Beacon isn't placed!";
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return beaconPlaced.containsKey(player);
	}

	private void usePortKey(Player player) {
		if (player.hasCooldown(Material.SPECTRAL_ARROW))
			return;
		final Location location = getTargetLocation(player, PORT_KEY_MAX_DISTANCE);
		if (location != null) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
			player.setCooldown(Material.SPECTRAL_ARROW, PORT_KEY_DELAY);
			GeometryLib.drawLine(player.getLocation()
					.add(0, 1, 0), location.add(0, 1, 0), 0.25, new GeometryLib.TinyParticle(Particle.PORTAL, 2, 0, 0, 0, 0.01));
			player.teleport(location);
			SoundLib.play(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.73984f);
			location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
		}
		else
			Chat.sendMessage(player, "&cUnable to teleport, no valid blocks in sight.");
	}

	private Location getTargetLocation(Player player, int max) {

		// Thanks Benz56 for this face test.
		final List<Block> blocks = player.getLastTwoTargetBlocks(null, max);

		if (blocks.size() != 2 || blocks.get(1).getType().isAir()) {
			return null;
		}

		final Block targetBlock = blocks.get(1);
		final Block adjacentBlock = blocks.get(0);
		final BlockFace face = targetBlock.getFace(adjacentBlock);

		// don't let teleport through barriers or if looking not at UP face
		if (targetBlock.getType() == Material.BARRIER || face != BlockFace.UP) {
			return null;
		}

		final Location location = targetBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5);

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		return (location.clone().add(0, 1, 0)
				.getBlock().getType().isAir() && location.clone().add(0, 2, 0)
				.getBlock().getType().isAir()) ? location : null;
	}

	@EventHandler
	public void handleNoPearlDamage(PlayerTeleportEvent ev) {
		final Player player = ev.getPlayer();
		final Location teleportTo = ev.getTo();

		if (GameUtils.isValidPlayer(player, ClassManager.ENDER) && ev.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			ev.setCancelled(true);
			player.eject();
			heal(player);
			player.setNoDamageTicks(1);

			if (teleportTo == null) {
				player.sendMessage(Chat.format("&cUnable to teleport, location is invalid!"));
				return;
			}

			player.teleport(teleportTo);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 10, 1);
		}

	}

	private void heal(Player player) {
		DamageFeature.addHealth(player, HEALING_AMOUNT);
		player.sendMessage(Chat.format("&aEnder Pearl healed you for &c%s ❤&a!", HEALING_AMOUNT));

	}

	private void ride(Player player, Entity entity) {
		player.eject();
		entity.addPassenger(player);
	}

	@EventHandler
	public void handleEnderPearlThrow(ProjectileLaunchEvent ev) {
		if (ev.getEntity() instanceof EnderPearl && ev.getEntity().getShooter() instanceof Player player) {
			if (player.getInventory().getItemInMainHand().isSimilar(CLASS_ITEM)) {
				ride(player, ev.getEntity());

				new GameTask() {
					@Override
					public void run() {
						player.setCooldown(Material.ENDER_PEARL, PEARL_DELAY);
						player.getInventory().setItemInMainHand(CLASS_ITEM);

					}
				}.runTaskLater(1);

			}
		}
	}

}
