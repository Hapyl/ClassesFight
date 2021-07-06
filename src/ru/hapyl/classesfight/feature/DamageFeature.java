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

package ru.hapyl.classesfight.feature;

import kz.hapyl.spigotutils.module.annotate.Super;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.chat.Gradient;
import kz.hapyl.spigotutils.module.chat.gradient.Interpolators;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.RockPaperClass;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.database.entry.StatisticsEntry;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.experience.RewardReason;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.stats.StatsContainer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DamageFeature {

	public static final double abstractNullDamage = 0.03;
	public static final long COMBAT_TAG = 5000L;
	public static final Map<Player, Double> playerHealth = new HashMap<>();
	public static final Map<Player, EnumDamageCause> lastDamageCause = new HashMap<>();
	public static final Map<Player, Player> lastDamager = new HashMap<>();
	public static final Map<Player, Long> combatTag = new HashMap<>();

	public static double getHealth(Player player) {
		return playerHealth.getOrDefault(player, 0.0d);
	}

	public static void preparePlayer(Player player) {
		playerHealth.put(player, getMaxHealth(player));
		updateVisualHealth(player);
	}

	public static void setHealth(Player player, double health) {
		setHealth(player, health, true);
	}

	public static void setHealth(Player player, double health, boolean b) {
		playerHealth.put(player, health);
		if (b) {
			updateVisualHealth(player);
		}
	}

	public static boolean isInCombat(Player player) {
		//return System.currentTimeMillis() - combatTag.getOrDefault(player, 0L) < COMBAT_TAG;
		return false;
	}

	public static void addHealth(Player player, double health) {
		if (isInCombat(player)) {
			return;
		}
		setHealth(player, Math.min(getHealth(player) + health, getMaxHealth(player)));
	}

	public static void removeHealth(Player player, double health) {
		addHealth(player, -health);
	}

	/**
	 * This does NOT actually damage player, only removes health and updates indicators
	 */
	public static void abstractDamage(Player player, double damage) {
		if (Spectator.isSpectator(player)) {
			return;
		}
		setHealth(player, getHealth(player) - damage);
		updateVisualHealth(player);
		if (!GameEffectManager.playerHasEffect(player, GameEffectType.INVISIBILITY)) {
			DamageIndicator.showDamage(player.getLocation().clone().add(0, 1, 0), damage);
		}
	}

	public static double getMaxHealth(Player player) {
		return Math.max(0.5d, ClassManager.getClass(player).getTheClass().getHealth().getValue());
	}

	public static void updateVisualHealth(Player player) {
		player.setMaxHealth(Math.max(0.5d, getMaxHealth(player)));
		player.setHealth(Math.min(Math.max(getHealth(player), 0.5d), getMaxHealth(player)));
	}

	public static void damageEntity(Entity entity, @Nullable Player damager, double damage, EnumDamageCause cause) {
		if (entity instanceof Player) {
			damage((Player)entity, damager, damage, cause, true);
		}
		else if (entity instanceof LivingEntity) {
			((LivingEntity)entity).damage(damage, damager);
		}
	}

	public static void damage(Player player, @Nullable Player damager, double damage, EnumDamageCause cause) {
		damage(player, damager, damage, cause, true);
	}

	public static void damageTick(Player player, @Nullable Player damager, double damage, EnumDamageCause cause, int tick) {
		damageTick(player, damager, damage, cause, tick, true);
	}

	public static void damageTick(Player player, @Nullable Player damager, double damage, EnumDamageCause cause, int tick, boolean flag) {
		player.setMaximumNoDamageTicks(tick);
		damage(player, damager, damage, cause, flag);
		player.setMaximumNoDamageTicks(20);
	}

	public static void updateCombatTime(Player player) {
		combatTag.put(player, System.currentTimeMillis());
	}

	public static long getCombatTime(Player player) {
		return COMBAT_TAG - (System.currentTimeMillis() - combatTag.getOrDefault(player, 0L));
	}

	@Super
	public static void damage(Player player, @Nullable Player damager, double damage, EnumDamageCause cause, boolean calculateKnockback) {
		if (Spectator.isSpectator(player) || player.isInvulnerable()) {
			return;
		}

		final PlayerDamageByPlayerEvent event = new PlayerDamageByPlayerEvent(player, damager, damage, cause);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		lastDamageCause.put(player, cause);
		//updateCombatTime(player);
		if (damager != null) {
			lastDamager.put(player, damager);
		}

		if (calculateKnockback) {
			// should fix death screen bug
			if (player.getHealth() <= abstractNullDamage) {
				player.setHealth(player.getHealth() + abstractNullDamage);
			}
			player.setHealth(player.getHealth() + abstractNullDamage);
			player.damage(abstractNullDamage, damager);
			//DamageFeature.takeDamageFx(player, damager);
		}

	}

	@Nullable
	public static Player getLastDamager(Player player) {
		return lastDamager.getOrDefault(player, null);
	}

	public static EnumDamageCause getLastDamageCause(Player player) {
		return lastDamageCause.getOrDefault(player, EnumDamageCause.NONE);
	}

	public static void damage(Player player, double damage, EnumDamageCause cause) {
		damage(player, player, damage, cause);
	}

	public static double calculateDamage(Player player, Player damager, double initDamage) {

		final ClassManager playerClass = ClassManager.getClass(player);
		final ClassManager damagerClass = ClassManager.getClass(damager);

		final EnumInfo attack = playerClass.getTheClass().getAttack();
		final EnumInfo defense = damagerClass.getTheClass().getDefense();

		return calculateDamage(attack, initDamage, defense);

	}

	public static double calculateDamage(double damage, EnumInfo strength, EnumInfo defense) {
		return (damage / 2) * strength.getValue() / (defense.getValue() / 2);
	}

	public static double calculateDamage(EnumInfo attack, double initDamage, EnumInfo defense) {
		return (initDamage / 2) * attack.getValue() / (defense.getValue());
	}

	public static void fakeDeath(Player player) {
		if (Spectator.isSpectator(player)) {
			return;
		}
		Spectator.add(player);

		final Player lastDamager = getLastDamager(player);
		GameManager.current().getGameMode().getMode().processPlayerDeath(player, lastDamager == null ? player : lastDamager);

		final Database database = Database.getDatabase(player);

		// 2.6 - Death
		ClassManager.getClass(player).getTheClass().onDeath(player);

		// reset this either way
		((RockPaperClass)ClassManager.RPS.getTheClass()).removePlayer(player);
		GameEffectManager.resetEffects(player);
		Cooldown.resetAll();

		final ClassManager currentClass = ClassManager.getClass(player);
		// Global Class Death
		StatsContainer.current().addClassStat(currentClass, StatsContainer.ClassStat.DEATH, 1);

		// not sure about stats for deaths/kills but ok
		database.getStatistics().addStat(StatType.DEATHS, 1);
		database.getStatistics().addClassDeaths(currentClass, 1);
		player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, SoundCategory.MASTER, 1, 1.8f);

		Chat.sendTitle(player, "&c&lYOU DIED", "", 10, 20, 5);
		GameManager.current().checkWin();
	}

	public static void handleKillAndMessage(Player player, Player damager, EnumDamageCause cause) {

		final Experience experience = Experience.getInstance();
		String deathMessage = Chat.format("☠ " + player.getName() + " ");

		if (damager != null) {

			// Don't award coins for suicide
			if (damager != player) {
				Database database = Database.getDatabase(damager);

				experience.rewardPlayer(damager, true, RewardReason.KILL);
				addKillFor(damager);

				final StatisticsEntry entry = database.getStatistics();
				final ClassManager damagerClass = ClassManager.getClass(damager);

				entry.addStat(StatType.KILLS, 1);
				entry.addClassKills(damagerClass, 1);

				// global
				StatsContainer.current().addClassStat(damagerClass, StatsContainer.ClassStat.KILL, 1);
				database.getCoinsEntry().grantCoins(GameManager.current().getCoinsKill(), true, "Kill");

				// fx kill effect
				final EnumEffect effect = database.getCosmetics().getCurrentEffect(EnumEffect.Type.KILL);
				if (effect != null && effect.isEnabled(player)) {
					// use effect AT player who got killed, not who's effect it is.
					effect.display(player);
				}
			}
		}

		deathMessage = new Gradient(concat(deathMessage, cause.getRandomIfMultiple(), damager)).rgb(new Color(160, 0, 0), new Color(255, 51, 51), Interpolators.LINEAR);
		Bukkit.broadcastMessage(deathMessage);

	}

	private static void addKillFor(Player player) {
		final GameManager manager = GameManager.current();
		final Map<Player, Integer> playerKills = manager.getPlayerKills();
		playerKills.put(player, playerKills.getOrDefault(player, 0) + 1);
		if (manager.getMvpPlayer() == null) {
			manager.setMvpPlayer(player);
		}
		else if (playerKills.getOrDefault(manager.getMvpPlayer(), 0) < playerKills.getOrDefault(player, 0)) {
			manager.setMvpPlayer(player);
		}
	}

	private static String getValidPronoun(Entity entity) {
		if (entity instanceof Projectile) {
			final ProjectileSource shooter = ((Projectile)entity).getShooter();
			if (shooter instanceof LivingEntity)
				return ((LivingEntity)shooter).getName() + "'s " + entity.getName();
		}
		return entity.getName();
	}

	private static String concat(String original, DeathMessage message, Entity killer) {
		String suffix = "";
		if (killer != null) {
			final String pronoun = getValidPronoun(killer);
			if (!message.hasSuffix()) {
				return original + message.formatMessage(pronoun);
			}
			else {
				suffix = message.getDamagerSuffix() + " " + pronoun;
			}
		}
		return original + message.getMessage() + " " + suffix;
	}

	@Deprecated
	private static String concat(String original, String message, String killerSuffix, Entity killer) {
		return original.concat(message + (killer == null ? "" : " " + killerSuffix + " " + getValidPronoun(killer)) + ".");
	}

	public static void setLastDamager(Player target, Player player) {
		lastDamager.put(target, player);
	}

}
