package ru.hapyl.classesfight.utils;

import org.bukkit.entity.Player;

public class PlayerData {

	private final Player player;

	public PlayerData(Player player) {
		this.player = player;
	}

	public final Player getPlayer() {
		return player;
	}
}
