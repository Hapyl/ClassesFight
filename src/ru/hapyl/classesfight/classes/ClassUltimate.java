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

package ru.hapyl.classesfight.classes;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.HashMap;
import java.util.Map;

public class ClassUltimate implements Listener {

	public static final Map<Player, Integer> ultimatePoints = new HashMap<>();
	// This used for the perk thing, just easier to manipulate
	public static final Map<Player, Integer> ultimateNeeded = new HashMap<>();
	public static final int POINTS_PER_TIME = 10; // seconds
	public static final int POINTS_PER = 1;

	private static final String ULTIMATE_PREFIX = Chat.format("&c&lULTIMATE! ");

	private String name;
	private int points;
	private Sound sound;
	private float pitch;

	public ClassUltimate() {
	}

	/**
	 * Этот класс отвечает за визуал и количество поинтов ульты для класса.
	 *
	 * @param name   - Название ульты. Будет выведено для всех при использовании ульты.
	 * @param points - Количество поинтов, нужное для активации ульты.
	 * @param sound  - Звук, который будет проигран при испольховании ульты.
	 * @param pitch  - Питч звука
	 */
	public ClassUltimate(String name, int points, Sound sound, float pitch) {
		this.name = name;
		this.points = points;
		this.sound = sound;
		this.pitch = pitch;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Используя этот конструктор, звук ильты будет ENDER_DRAGON_GROWL;
	 *
	 * @param name   - Название ульты. Будет выведено для всех при использовании ульты.
	 * @param points - Количество поинтов, нужное для активации ульты.
	 */
	public ClassUltimate(String name, int points) {
		this(name, points, Sound.ENTITY_ENDER_DRAGON_GROWL, 1);
	}

	public int getPoints() {
		return points;
	}

	public String getName() {
		return name;
	}

	public static void onStop() {
		ultimatePoints.clear();
	}

	////////////////////

	public static int getPoints(Player player) {
		return ultimatePoints.getOrDefault(player, 0);
	}

	public static int getPointsNeeded(Player player) {
		return ultimateNeeded.getOrDefault(player, Integer.MAX_VALUE);
	}

	public static boolean perkAffected(Player player) {
		return getPointsNeeded(player) != ClassManager.getClass(player).getUltimate().getPoints();
	}

	public static void setPointsNeeded(Player player, int points) {
		if (Perk.ULTIMATE_MADNESS.hasPerkAndEnabled(player)) {
			points--;
		}
		if (Perk.ULTIMATE_MADNESS_2.hasPerkAndEnabled(player)) {
			points--;
		}
		// Min is always 1 point
		points = Math.max(1, points);
		ultimateNeeded.put(player, points);
	}

	public static void setPoints(Player player, int i) {
		ultimatePoints.put(player, i);
	}

	public static void preparePlayer(Player player) {
		final ClassManager clazz = ClassManager.getClass(player);
		setPointsNeeded(player, clazz.getUltimate().getPoints());
	}

	public static void chargeUltimate(Player player) {
		setPoints(player, getPointsNeeded(player));
	}

	public static boolean isUltimateCharged(Player player) {
		return getPoints(player) == getPointsNeeded(player);
	}

	public static void onPlayersRevealed() {

		final int pointsDelay = POINTS_PER_TIME * 20;
		new GameTask() {
			@Override
			public void run() {
				GameManager.current().forEachInGamePlayer(player -> {

					final ClassManager playerClass = ClassManager.getClass(player);
					final ClassUltimate ultimate = playerClass.getUltimate();

					if (ultimate == null) {
						return;
					}

					final int pointsNeeded = getPointsNeeded(player);

					if (isUltimateCharged(player) || playerClass.getTheClass().isUsingUltimate(player)) {
						return;
					}

					setPoints(player, Math.min(getPoints(player) + POINTS_PER, pointsNeeded));
					if (Setting.ULTIMATE_POINT_SOUND.isEnabled(player)) {
						SoundLib.play(player, Sound.ENTITY_CHICKEN_EGG, 0f);
					}

					if (getPoints(player) == pointsNeeded) {
						broadcastUltimateReady(player);
					}

				});
			}
		}.runTaskTimer(pointsDelay, pointsDelay);
	}

	private static void broadcastUltimateReady(Player player) {
		Chat.sendMessage(player, ULTIMATE_PREFIX + "&bYour ultimate has charged, press &e&lF &bto use it!");
		if (Setting.ULTIMATE_READY_SOUND.isEnabled(player)) {
			SoundLib.play(player, Sound.BLOCK_CONDUIT_DEACTIVATE, 2);
		}
		Chat.sendTitle(player, "", "&aYou ultimate is ready!", 5, 15, 5);
	}

	@EventHandler
	public void handlePlayerSwapHandsEvent(PlayerSwapHandItemsEvent ev) {

		ev.setCancelled(true);

		if (!GameManager.current().isGameInProgress()) {
			return;
		}

		// handle ultimate use
		final Player player = ev.getPlayer();
		final ClassManager clazz = ClassManager.getClass(player);
		final ClassUltimate ultimate = clazz.getUltimate();

		if (isUltimateCharged(player)) {

			// don't allow to ult after the game (fix meteor and other ultimates)
			if (GameManager.current().isState(GameManager.GameState.PAST_GAME)) {
				Chat.sendMessage(player, ULTIMATE_PREFIX + "&cUnable to use ultimate in after the game ends!");
				return;
			}

			// predicate check
			if (!clazz.predicateUltimate(player)) {
				Chat.sendMessage(player, ULTIMATE_PREFIX + "&cUnable to use ultimate! " + clazz.getTheClass().predicateMessage());
				return;
			}

			// already in use check
			if (clazz.getTheClass().isUsingUltimate(player)) {
				Chat.sendMessage(player, ULTIMATE_PREFIX + "&cUnable to use ultimate! Ultimate still in use!");
				return;
			}

			// Global Stat Info
			StatsContainer.current().addClassStat(ClassManager.getClass(player), StatsContainer.ClassStat.ULT_USE, 1);

			setPoints(player, 0);
			Bukkit.broadcastMessage(Chat.format(ULTIMATE_PREFIX + "&b%s used %s!", player.getName(), ultimate.getName()));
			SoundLib.play(player.getLocation(), ultimate.sound, ultimate.pitch);
			Database.getDatabase(player).getStatistics().addStat(StatType.ULTIMATE_USED, 1);
			clazz.useUltimate(player);
		}
		else {
			Chat.sendTitle(player, "", "&cYour ultimate isn't ready!", 5, 10, 5);
		}

	}


}
