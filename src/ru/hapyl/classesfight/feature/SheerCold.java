package ru.hapyl.classesfight.feature;

import org.bukkit.entity.Player;

public class SheerCold {

	private final Player player;
	private final int tick;

	public SheerCold(Player player, int tick) {
		this.player = player;
		this.tick = tick;
	}

	public int getTick() {
		return tick;
	}

	public Player getPlayer() {
		return player;
	}
}
