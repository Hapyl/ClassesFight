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

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TechnoClass;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TrapCage extends Ability implements Listener {

	private final Map<ArmorStand, Player> cages;
	private final Map<Player, Integer> perPlayerCages;

	private final int cageWindupDelay = 40;

	public TrapCage() {
		super("Cage", "Place a cage trap that charges for &b2s&7. When charged, becomes almost invisible. Activates &eautomatically &7if there is an enemy nearby; Stunning, revealing their location and applying Vulnerability. &e&lSNEAK &7near a cage to pick it up.");
		this.setItem(Material.IRON_TRAPDOOR, "PLACE");
		this.setAllowLeftClick(false);
		this.setRemoveItemOnUse(true);
		this.setCooldownSec(2);
		this.perPlayerCages = new HashMap<>();
		this.cages = new ConcurrentHashMap<>();
		this.setShowCooldown(false);
		this.setCastMessage("This is a nice spot.", "This will work.", "That's good.");
	}

	@Override
	public void onDeath(Player player) {
		for (final ArmorStand stand : cages.keySet()) {
			if (cages.get(stand) == player) {
				stand.remove();
				cages.remove(stand);
			}
		}
	}

	@Override
	public void onStop() {
		this.cages.keySet().forEach(Entity::remove);
		this.cages.clear();
		this.perPlayerCages.clear();
	}

	private void placeCage(final Player player, Block block) {

		Location location = block.getRelative(BlockFace.UP).getLocation().add(0.5, -1.4, 0.5);

		ArmorStand stand = createArmorStand(location, me -> {
			if (me.getEquipment() != null) {
				me.getEquipment().setHelmet(new ItemStack(Material.IRON_TRAPDOOR));
			}
		});

		this.perPlayerCages.put(player, this.perPlayerCages.getOrDefault(player, 0) + 1);

		new GameTask() {
			@Override
			public void run() {
				stand.setSmall(true);
				stand.teleport(stand.getLocation().add(0, 0.735, 0));
				PlayerLib.spawnParticle(stand.getLocation(), Particle.SNOWBALL, 3, 0, 0, 0, 0);
				// This will allow cage ability to work
				cages.put(stand, player);
			}
		}.runTaskLater(cageWindupDelay);

		SoundLib.play(player, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.2f);

	}

	@Override
	public Response useAbility(Player player) {
		final Block block = player.getTargetBlockExact(5);

		if (block == null) {
			return Response.error("Invalid block!");
		}

		if (perPlayerCages.getOrDefault(player, 0) > 3) {
			return Response.error("You cannot place more than 3 cages!");
		}

		if (block.getType().isAir()) {
			return Response.error("Cannot place cage here!");
		}

		placeCage(player, block);
		return Response.OK;
	}

	@EventHandler
	public void handleSneak(PlayerToggleSneakEvent ev) {
		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}

		final Player player = ev.getPlayer();
		if (Spectator.isSpectator(player) || ClassManager.getClass(player) != ClassManager.TECHNO || !player.isSneaking()) {
			return;
		}

		final Collection<Entity> nearbyCages = getNearbyCages(player.getLocation(), 2.0d);
		if (nearbyCages == null || nearbyCages.isEmpty()) {
			return;
		}

		for (Entity near : nearbyCages) {
			if (this.cages.getOrDefault((ArmorStand)near, player) == player) {
				pickupCage(player, (ArmorStand)near);
				return;
			}
		}

	}

	@EventHandler
	public void handleMovement(PlayerMoveEvent ev) {
		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}

		final Player player = ev.getPlayer();

		if (Spectator.isSpectator(player)) {
			return;
		}

		final Collection<Entity> cages = getNearbyCages(player.getLocation(), 2.0d);
		if (cages == null || cages.isEmpty()) {
			return;
		}

		for (Entity cage : cages) {
			if (this.cages.getOrDefault((ArmorStand)cage, player) != player) {
				pullToCage(player, (ArmorStand)cage);
				removeCage((ArmorStand)cage);
				return;
			}
		}

	}

	private Collection<Entity> getNearbyCages(Location center, double radius) {
		final World world = center.getWorld();
		if (world != null) {
			return world.getNearbyEntities(center, radius, radius, radius, entity -> entity instanceof ArmorStand && this.cages.containsKey(entity));
		}
		return null;
	}

	private void pickupCage(Player player, ArmorStand cage) {
		player.setCooldown(this.getItem().getType(), 60);
		player.sendMessage(Chat.format("&aYou picked up a cage."));
		player.getInventory().addItem(this.getItem().getItem());
		SoundLib.play(player, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5f);
		removeCage(cage);
	}

	private void removeCage(ArmorStand cage) {
		final Player player = this.cages.get(cage);
		this.perPlayerCages.put(player, perPlayerCages.getOrDefault(player, 1) - 1);
		this.cages.remove(cage);
		cage.remove();
	}

	private void pullToCage(Player player, ArmorStand cage) {

		final Location playerLocation = player.getLocation();
		final Location cageLocation = cage.getLocation();
		final Player owner = this.cages.getOrDefault(cage, null);
		cage.remove();

		// owner if left?
		if (owner != null) {
			owner.sendTitle(Chat.format("&aCage Triggered!"), Chat.format("&7You caught %s", player.getName()), 10, 20, 10);
		}

		// apply effects
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 300));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
		player.damage(1d);
		SoundLib.play(player, Sound.ENTITY_ENDERMAN_HURT, 0.8f);
		GameEffectManager.applyEffect(player, GameEffectType.VULNERABLE, 80);
		((TechnoClass)ClassManager.TECHNO.getTheClass()).glowEntitySilhouette(player, owner, 80);

		// pull to the cage
		player.setVelocity(cageLocation.toVector().subtract(playerLocation.toVector()).normalize());

	}

	private ArmorStand createArmorStand(Location loc, Consumer<ArmorStand> pre) {
		return GarbageCollector.spawnEntity(loc, ArmorStand.class, me -> {
			me.setMarker(true);
			me.setInvulnerable(true);
			me.setVisible(false);
			pre.accept(me);
		});
	}
}
