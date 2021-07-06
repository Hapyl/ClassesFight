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

package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.BlastKnightClass;
import ru.hapyl.classesfight.classes.iclass.FireGuy;
import ru.hapyl.classesfight.classes.iclass.MageClass;
import ru.hapyl.classesfight.classes.iclass.TamerClass;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.ClassEntry;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.GameTeam;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.feature.japangod.JapanGod;
import ru.hapyl.classesfight.feature.npc.NpcManager;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.stats.StatsHologramManager;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.Locale;
import java.util.Random;

public class GlobalListener implements Listener {

	public static final String pluginPrefix = "&6&lCLASSES FIGHT!&7";

	@EventHandler
	public void handleInteraction(PlayerInteractEvent ev) {
		final Block block = ev.getClickedBlock();
		final Player player = ev.getPlayer();
		if (GameManager.current().isGameInProgress() && ev.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (player.getInventory().getItemInMainHand().getType() == Material.ARMOR_STAND) {
				ev.setCancelled(true);
				return;
			}

			if (block != null) {
				if (block.getType().isInteractable()) {
					if (!canInteractWith(block.getType())) {
						ev.setCancelled(true);
					}
				}
			}
		}
	}

	private boolean canInteractWith(Material material) {
		final String name = material.name().toLowerCase(Locale.ROOT);
		return name.contains("button");
	}

	@EventHandler
	public void handleCustomDamage(PlayerDamageByPlayerEvent ev) {
		if (ev.getDamage() <= 0.0d) {
			ev.setCancelled(true);
			return;
		}

		final Player whoGotHit = ev.getWhoGotDamaged();
		// If damager is null get last damager
		final Player tempDamager = (ev.getDamager() == null ? DamageFeature.getLastDamager(whoGotHit) : ev.getDamager());
		final Player damager = tempDamager == null ? whoGotHit : tempDamager;
		final EnumDamageCause cause = ev.getCause();

		final ClassManager whoGotHitClass = ClassManager.getClass(whoGotHit);
		final ClassManager damagerClass = ClassManager.getClass(damager);

		if (Spectator.isSpectator(damager)) {
			ev.setCancelled(true);
			return;
		}

		/* 2.6 processors */
		damagerClass.getTheClass().processDamageEventAsDamager(damager, ev);
		whoGotHitClass.getTheClass().processDamageEventAsVictim(whoGotHit, ev);

		if (ev.isCancelled()) {
			return;
		}

		double damage = ev.getDamage();

		/* Check for the cause */
		switch (cause) {
			case FALL -> {
				if (GameEffectManager.playerHasEffect(whoGotHit, GameEffectType.FALL_DAMAGE_RESISTANCE)) {
					GameEffectManager.removeEffect(whoGotHit, GameEffectType.FALL_DAMAGE_RESISTANCE);
					ev.setCancelled(true);
					return;
				}
				if (whoGotHitClass == ClassManager.NINJA || whoGotHitClass == ClassManager.HERCULES) {
					ev.setCancelled(true);
					return;
				}
			}
			case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> {
				if (whoGotHit.isBlocking()) {
					return;
				}
				if (whoGotHitClass == ClassManager.BLAST_KNIGHT) {
					((BlastKnightClass)ClassManager.BLAST_KNIGHT.getTheClass()).addCharge(whoGotHit, 1);
				}
			}
		}

		// Check for Shield charges
		if (GameEffectManager.hasShieldHits(whoGotHit)) {
			ev.setCancelled(true);
			if (GameEffectManager.getShieldCharges(whoGotHit) != -1) {
				GameEffectManager.removeShieldCharge(whoGotHit);
			}
			ev.setCancelled(true);
			return;
		}

		// Check for Invisibility
		if (damager != whoGotHit && GameEffectManager.playerHasEffect(damager, GameEffectType.INVISIBILITY)) {
			Chat.sendTitle(damager, "", "&cCannot damage while Invisible!", 0, 10, 5);
			ev.setCancelled(true);
			return;
		}

		/* Check for the damager class */
		// OOP! Moved into their own classes

		if (whoGotHit.isBlocking()) {
			ev.setCancelled(true);
			return;
		}

		/* Some more tests */

		// check for damage to the teammates
		if (GameManager.current().isTeamMode() && GameTeam.getTeammates(damager).contains(whoGotHit)) {
			Chat.sendMessage(damager, "&cYou cannot damage your teammates!");
			ev.setCancelled(true);
			return;
		}

		// check for the zealot ultimate
		if (GameEffectManager.playerHasEffect(whoGotHit, GameEffectType.GOLDEN_CURSE)) {
			if (whoGotHit != damager) {
				Database.getDatabase(damager).getCoinsEntry().addCoins(1);
				Chat.sendActionbar(damager, "&6&lMIDAS TOUCH! &eYou just gained a coin. &7(%s)", Database.getDatabase(damager)
						.getCoinsEntry()
						.getCoins());
			}
		}

		DamageFeature.updateCombatTime(whoGotHit);

		// check for stun
		if (GameEffectManager.playerHasEffect(whoGotHit, GameEffectType.STUN)) {
			GameEffectManager.removeEffect(whoGotHit, GameEffectType.STUN);
		}

		// check for the critical damage
		if (damager.getFallDistance() > 0.0F && !damager.isOnGround() && !damager.hasPotionEffect(PotionEffectType.BLINDNESS) && damager.getVehicle() == null) {
			damage /= 1.5F;
		}

		if (GameEffectManager.playerHasEffect(whoGotHit, GameEffectType.VULNERABLE)) {
			damage *= 2;
		}

		if (GameEffectManager.playerHasEffect(whoGotHit, GameEffectType.RESISTANCE)) {
			damage = 0.0d;
		}

		// Strength
		if (GameEffectManager.playerHasEffect(damager, GameEffectType.STRENGTH)) {
			damage *= 2;
		}

		final double finalDamage = DamageFeature.calculateDamage(whoGotHit, damager, damage);

		// Damage absorbing shield test
		if (GameEffectManager.hasShieldDamage(whoGotHit)) {
			ev.setCancelled(true);
			final double health = GameEffectManager.getShieldHealthDamage(whoGotHit);
			if (health - finalDamage <= 0.0d) {
				GameEffectManager.removeEffect(whoGotHit, GameEffectType.BREAKABLE_SHIELD);
			}
			else {
				GameEffectManager.removeShieldHealthDamage(whoGotHit, finalDamage);
			}
			return;
		}

		DamageFeature.abstractDamage(whoGotHit, finalDamage);

		// if abstract damage cancel
		if (finalDamage <= 0.0d) {
			ev.setCancelled(true);
			return;
		}

		/** Death Occurred **/
		if (finalDamage >= DamageFeature.getHealth(whoGotHit)) {
			DamageFeature.setHealth(whoGotHit, 20.0d);

			if (ClassManager.getClass(whoGotHit) == ClassManager.FIRE_GUY) {
				final FireGuy fireguy = (FireGuy)ClassManager.FIRE_GUY.getTheClass();

				// Fire guy ultimate
				if (fireguy.isInUltimateForm(whoGotHit)) {
					fireguy.rebornPlayer(whoGotHit);
					ev.setCancelled(true);
					return;
				}
			}

			/**
			 * @Strict
			 * Will die, no cancelling
			 */

			if (ClassManager.getClass(whoGotHit) == ClassManager.TAMER) {
				((TamerClass)ClassManager.TAMER.getTheClass()).removeMinions(whoGotHit);
			}

			// *==* Relics *==*
			final JapanGod japanGod = GameManager.current().getJapanGod();

			if (japanGod != null) {
				final int corpseRelic = japanGod.getRelicAmount(whoGotHit);
				if (corpseRelic > 0 && GameMap.getCurrentMap() == GameMap.JAPAN) {
					japanGod.setRelic(damager, corpseRelic);
					Chat.sendMessage(damager, "&6&lGOD! &dI see you killed someone with relic, it now belongs to you!");
					OldPlayerLib.playSound(damager, Sound.ENTITY_VILLAGER_YES, 0.75f);
					japanGod.setRelic(whoGotHit, 0);
				}
			}

			ev.setCancelled(true);
			DamageFeature.handleKillAndMessage(whoGotHit, damager, isNaturalCause(cause) ? DamageFeature.getLastDamageCause(whoGotHit) : cause);
			DamageFeature.fakeDeath(whoGotHit);
		}

	}

	private boolean isNaturalCause(EnumDamageCause cause) {
		return !cause.isCustomDamage();
	}

	@EventHandler()
	public void handleProjectiles(ProjectileHitEvent ev) {
		// Auto-Generated
		final Entity hitEntity = ev.getHitEntity();
		if (hitEntity instanceof Painting) {
			ev.setCancelled(true);
		}
	}

	@EventHandler()
	public void handleEntityEvent(HangingBreakByEntityEvent ev) {
		Entity entity = ev.getRemover();
		if (entity instanceof Player && ((Player)entity).getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
		}
	}

	@EventHandler()
	public void handleEntityRotate(PlayerInteractEntityEvent ev) {
		final Player player = ev.getPlayer();
		final Entity entity = ev.getRightClicked();
		if (entity instanceof ItemFrame && player.getGameMode() != GameMode.CREATIVE) {
			((ItemFrame)entity).setRotation(((ItemFrame)entity).getRotation().rotateCounterClockwise());
			ev.setCancelled(true);
		}
	}

	@EventHandler()
	public void handleEntityPlace(HangingPlaceEvent ev) {
		if (ev.getPlayer() != null && ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void handleNativeDamage(EntityDamageEvent ev) {
		if (!GameManager.current().isGameInProgress() && ev.getEntity() instanceof Player) {
			ev.setCancelled(true);
			return;
		}

		final Entity entity = ev.getEntity();

		// this used for the visual knock back
		double theDamage = ev.getDamage();
		if (ev instanceof EntityDamageByEntityEvent) {
			final Entity damager = ((EntityDamageByEntityEvent)ev).getDamager();

			if (entity instanceof ItemFrame) {
				if (damager instanceof Player && ((Player)damager).getGameMode() != GameMode.CREATIVE) {
					ev.setCancelled(true);
				}
				if (damager instanceof Projectile) {
					final ProjectileSource shooter = ((Projectile)damager).getShooter();
					if (shooter instanceof Player && ((Player)shooter)
							.getGameMode() != GameMode.CREATIVE) {
						ev.setCancelled(true);
					}
				}
				return;
			}

			if (damager instanceof TNTPrimed) {
				if (damager.getScoreboardTags().contains("UltimateTnt")) {
					ev.setCancelled(true);
				}
				return;
			}

			if (damager instanceof Firework) {
				ev.setCancelled(true);
				return;
			}
		}

		if (ev.getDamage() <= 0.0d) {
			return;
		}

		if (ClassesFight.getSingleton().getMapFeatures().checkVoidCatAndTeleport(entity)) {
			ev.setCancelled(true);
			ev.setDamage(0.0d);
			((LivingEntity)entity).setNoDamageTicks(1);
			return;
		}

		if (entity instanceof final Player player) {
			ev.setDamage(0.0);
			Player playerDmg = player;

			if (ev instanceof EntityDamageByEntityEvent) {
				final Entity dmg = ((EntityDamageByEntityEvent)ev).getDamager();

				if (dmg instanceof Player) {
					playerDmg = (Player)dmg;
				}

				if (dmg instanceof Projectile) {
					final ProjectileSource shooter = ((Projectile)dmg).getShooter();
					if (shooter instanceof Player) {
						playerDmg = (Player)shooter;
					}
				}
			}

			final PlayerDamageByPlayerEvent event = new PlayerDamageByPlayerEvent(player, playerDmg, theDamage, EnumDamageCause.getFromCause(ev.getCause()));
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				ev.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void handleMovements(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();

		if (GameManager.current().isGameInProgress()) {
			final Location from = ev.getFrom();
			final Location to = ev.getTo();

			if (to == null) {
				return;
			}

			// lockdown check
			if (GameEffectManager.playerHasEffect(player, GameEffectType.LOCK_DOWN)) {
				if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
					ev.setCancelled(true);
					player.sendTitle("", Chat.format("&cYou cannot move during Lockdown!"), 0, 5, 0);
				}
				return;
			}

			if (GameEffectManager.playerHasEffect(player, GameEffectType.AMNESIA)) {
				if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
					final double pushSpeed = player.isSneaking() ? 0.05d : 0.1d;
					player.setVelocity(new Vector(new Random().nextBoolean() ? pushSpeed : -pushSpeed, -0.2723, new Random().nextBoolean() ?
							pushSpeed :
							-pushSpeed));
				}
			}

		}
	}

	@EventHandler
	public void handleChangeSlot(PlayerItemHeldEvent ev) {
		final Player player = ev.getPlayer();
		if (GameEffectManager.playerHasEffect(player, GameEffectType.LOCK_DOWN)) {
			ev.setCancelled(true);
			player.sendTitle("", Chat.format("&aYou cannot change slots while affected by Lockdown!"), 5, 10, 5);
		}

	}

	@EventHandler
	public void handleHunger(FoodLevelChangeEvent ev) {
		ev.setCancelled(true);
		ev.setFoodLevel(20);
	}

	@EventHandler()
	public void handleNoOpenUnwantedInventory(InventoryOpenEvent ev) {
		// Auto-Generated
		final InventoryType type = ev.getView().getType();
		if (type != InventoryType.CHEST && GameManager.current().isGameInProgress()) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void handleInventoryClick(InventoryClickEvent ev) {
		if (ev.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void handleDropItem(PlayerDropItemEvent ev) {
		// duh why it dupes
		ev.getItemDrop().remove();
		ev.setCancelled(true);
		if (ClassManager.getClass(ev.getPlayer()) == ClassManager.MAGE) {
			((MageClass)ClassManager.MAGE.getTheClass()).fixSoulEater(ev.getPlayer());
		}
	}

	@EventHandler
	public void handleJoin(PlayerJoinEvent ev) {

		final Player player = ev.getPlayer();
		final GameManager manager = GameManager.current();

		GameUtils.setCollision(false);

		// Display tutorial either way (may add setting)
		manager.displayTutorial(player);

		// load parkour
		for (ParkourCourse value : ParkourCourse.values()) {
			value.showHolograms(player);
		}

		// load parkour leaders
		StatsHologramManager.current().updateViewers();

		if (!ClassesFight.isStableBuild()) {
			Chat.sendMessage(player, "");
			Chat.sendMessage(player, "&4Keep in Mind! &cThis build is NOT stable!");
			Chat.sendMessage(player, "&cThere will be bugs and glitches, make sure to &e/feedback &cthem!");
			Chat.sendMessage(player, "");
			OldPlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.25f);
		}

		// Make sure to build scoreboard first
		ClassesFight.getSingleton().getGameUI().createScoreboard(player);
		Bukkit.getScheduler().runTaskLater(ClassesFight.getPlugin(), () -> NpcManager.getManager().spawnNpc(player), 2);
		ClassEntry.handleClassRejoin(player);

		/**
		 * Load Class Stats
		 */

		// Return where player were before leave if has build mode enabled
		if (PlayerManager.current().hasBuildMode(player)) {
			Chat.sendActionbar(player, "&6Your build mode is still enabled!");
		}
		else {
			player.teleport(GameMap.SPAWN.getLocation());
			ClassesFight.getSingleton().getGameItems().giveAll(player);
		}

		ev.setJoinMessage(Chat.format("%s &e%s wants to fight!", pluginPrefix, player.getName()));
		if (!Database.getDatabase(player).getCoinsEntry().hasCoins(0)) {
			Database.getDatabase(player).getCoinsEntry().setCoins(0);
		}

		if (manager.isGameInProgress()) {
			Chat.sendTitle(player, "", "&aYou're currently spectating.", 10, 120, 10);
			Spectator.add(player);
			player.teleport(manager.getRandomInGamePlayer());
			ClassesFight.getSingleton().getGameUI().createScoreboard(player);
		}

	}

	@EventHandler
	public void handleLeave(PlayerQuitEvent ev) {

		final Player player = ev.getPlayer();
		final GameManager manager = GameManager.current();

		GameUtils.resetPlayer(player);
		ClassesFight.getSingleton().getParkourManager().reset(player);

		if (manager.getPlayers().contains(player)) {
			manager.getPlayers().remove(player);
			GameUtils.resetPlayer(player);
			manager.checkWin();
			Chat.broadcast("%s %s left while fighting!", pluginPrefix, player.getName());
		}

		ev.setQuitMessage(Chat.format("%s &e%s has fallen!", pluginPrefix, player.getName()));
	}

}
