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

package ru.hapyl.classesfight.stats;

import kz.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.EquipmentSlot;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.annotations.Singleton;
import ru.hapyl.classesfight.annotations.StrictStatic;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.parkour.ParkourData;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsHologramManager implements Listener {

	@StrictStatic private static final Map<ParkourCourse, Hologram> perParkourHologram = new HashMap<>();

	@Singleton private static StatsHologramManager instance;

	public StatsHologramManager() {
		if (instance == null) {
			instance = this;
		}
	}

	public static StatsHologramManager current() {
		return instance;
	}

	public void createHolograms() {

		for (ParkourCourse value : ParkourCourse.values()) {
			updateParkourLeaderboard(value);
		}

	}

	public void removeHolograms() {
		perParkourHologram.forEach((parkour, holo) -> updateParkourLeaderboard(parkour));
		perParkourHologram.clear();
	}

	public void updateParkourLeaderboard(ParkourCourse course) {

		if (course.getParkour().getLeadersLocation() == null) {
			return;
		}

		final Hologram hologram = new Hologram();
		hologram.addLine(ChatColor.AQUA + course.getParkour().getName() + " Leaders");
		hologram.addLine("");

		hologram.addLine("&7...");
		hologram.addLine("&7...");
		hologram.addLine("&7...");

		hologram.addLine("");
		hologram.addLine("&eClick for details");

		final Map<Long, OfflinePlayer> top3 = StatsContainer.current().getTopThreeParkourLeaders(course.getParkour());
		final AtomicInteger integer = new AtomicInteger(1);

		top3.forEach((time, player) -> {
			final String format = new SimpleDateFormat("mm:ss.SS").format(time);
			// null -> invalid player
			if (player.getName() == null) {
				hologram.setLine(integer.get() + 1, "&7...");
			}
			else {
				hologram.setLine(integer.get() + 1, String.format("&e#%s &6%s &7- &b%s%s", integer.get(), player.getName(), format,
						isCheated(course.getParkour(), player
								.getUniqueId()) ? "*" : ""));
			}
			integer.addAndGet(1);
		});


		if (perParkourHologram.containsKey(course)) {
			perParkourHologram.get(course).destroy();
		}

		hologram.create(course.getParkour().getLeadersLocation().toLocation().clone().add(0.0d, 0.5d, 0.0d));
		perParkourHologram.put(course, hologram);
	}

	public boolean isCheated(Parkour parkour, UUID uuid) {
		return StatsContainer.current().isCheated(parkour, uuid);
	}

	public void updateViewers() {
		perParkourHologram.forEach((parkour, holo) -> {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				holo.show(onlinePlayer);
			}
		});
	}

	@EventHandler()
	public void handleJumpParkourThing(PlayerStatisticIncrementEvent ev) {
		// Auto-Generated
		if (GameManager.current().isGameInProgress() || ev.getStatistic() != Statistic.JUMP) {
			return;
		}

		final Player player = ev.getPlayer();
		final ParkourData data = ClassesFight.getSingleton().getParkourManager().getPlayerData(player);

		if (data == null) {
			return;
		}

		final int increment = ev.getNewValue() - ev.getPreviousValue();
		data.incrementJump();

	}

	@EventHandler()
	public void handleClickMenu(PlayerInteractEvent ev) {
		// Auto-Generated

		if (GameManager.current().isGameInProgress()
				|| ev.getHand() == EquipmentSlot.OFF_HAND
				|| ev.getAction() == Action.PHYSICAL
				|| !ev.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
			return;
		}

		final Player player = ev.getPlayer();

		for (ParkourCourse value : ParkourCourse.values()) {
			final BlockLocation leaders = value.getParkour().getLeadersLocation();
			if (leaders == null) {
				continue;
			}
			final Location location = leaders.toLocation();
			if (ev.getClickedBlock() != null && ev.getClickedBlock().getLocation().clone().add(0.5d, 0.5d, 0.5d).distance(location) <= 2.35f) {
				new ParkourStatsGUI(player, value);
				return;
			}
			else {
				if (player.getLocation().distance(location) <= 2.35f) {
					new ParkourStatsGUI(player, value);
					return;
				}
			}
		}

	}

}
