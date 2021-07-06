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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum GameTeam {

	NULL(ChatColor.BLACK, null, 0),

	RED(ChatColor.RED, Material.RED_WOOL, 2),
	GREEN(ChatColor.GREEN, Material.GREEN_WOOL, 2),
	ORANGE(ChatColor.GOLD, Material.ORANGE_WOOL, 2),
	BLUE(ChatColor.BLUE, Material.BLUE_WOOL, 2);

	private final Set<Player> players = new HashSet<>();
	private final ChatColor color;
	private final Material item;
	private final int maxPlayers;

	GameTeam(ChatColor color, Material item, int maxPlayers) {
		this.color = color;
		this.item = item;
		this.maxPlayers = maxPlayers;
	}

	public Material getWool() {
		return item;
	}

	public ChatColor getColor() {
		return color;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public boolean contains(Player player) {
		return !this.getPlayers().isEmpty() && this.getPlayers().contains(player);
	}

	public static Set<Player> getTeammates(Player player) {
		final GameTeam team = getPlayerTeam(player);
		if (team == NULL) {
			return new HashSet<>();
		}
		team.getPlayers().remove(player);
		return team.getPlayers();
	}

	public static String getTeammatesString(Player player) {
		final List<Player> mates = new ArrayList<>(getTeammates(player));
		String str = "";
		for (int i = 0; i < mates.size(); i++) {
			str = str.concat(mates.get(i).getName() + (mates.size() - 1 == i ? "." : ", "));
		}
		return str;
	}

	public static GameTeam getPlayerTeam(Player player) {
		for (GameTeam team : values()) {
			if (!team.players.isEmpty() && team.players.contains(player)) {
				return team;
			}
		}
		return NULL;
	}

	public Set<Player> getPlayers() {
		return this.players;
	}

	public void leaveTeam(Player player) {
		if (!GameManager.current().isTeamMode()) {
			message(player, "&cTeam mode is not enabled.", Sound.ENTITY_VILLAGER_NO);
			return;
		}
		if (!this.players.contains(player)) {
			message(player, "&cYou are not in this team!", Sound.ENTITY_VILLAGER_NO);
			return;
		}
		this.players.remove(player);
		Chat.broadcast("&6&lTEAM! &a%s left %s &ateam.", player.getName(), this.getName());
	}

	public static Set<GameTeam> getFilledTeams() {
		final Set<GameTeam> hash = new HashSet<>();
		for (GameTeam value : values()) {
			if (!value.getPlayers().isEmpty()) hash.add(value);
		}
		return hash;
	}

	public static boolean isTeammate(Player of, Player who) {
		return getTeammates(of).contains(who);
	}

	public void joinTeam(Player player) {
		if (!GameManager.current().isTeamMode()) {
			message(player, "&cTeam mode is not enabled.", Sound.ENTITY_VILLAGER_NO);
			return;
		}
		if (this.players.contains(player)) {
			message(player, "&cYou are already in this team!", Sound.ENTITY_VILLAGER_NO);
			return;
		}
		if (this.players.size() == this.maxPlayers) {
			message(player, "&cThis is is full!", Sound.ENTITY_VILLAGER_NO);
			return;
		}
		final GameTeam t = getPlayerTeam(player);
		if (t != NULL) t.leaveTeam(player);
		this.players.add(player);
		GameUtils.villagerYes(player);
		Chat.broadcast("&6&lTEAM! &a%s joined %s &ateam.", player.getName(), this.getName());
	}

	public static void toggleTeam(Player executor) {
		final GameManager manager = GameManager.current();
		manager.setTeamMode(!manager.isTeamMode());
//        if (GameManager.teamMode) GameItem.TEAM_PICK.allAll();
//        else GameItem.TEAM_PICK.removeAll();
		Chat.broadcast("&6&lTEAM! &a%s toggled &l%s &ateam mode.", executor.getName(), manager.isTeamMode() ? "on" : "off");
	}

	public static void message(Player player, String msg, Sound sound) {
		Chat.sendMessage(player, "&6&lTEAM! " + Chat.format(msg));
		if (sound != null) SoundLib.play(player, sound, 1);
	}

	public static void message(Player player, String msg) {
		message(player, msg, null);
	}

	public String getName() {
		return this.color + ChatColor.BOLD.toString() + Chat.capitalize(this.name());
	}
}
