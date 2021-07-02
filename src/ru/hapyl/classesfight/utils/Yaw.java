package ru.hapyl.classesfight.utils;

import org.bukkit.entity.Player;

public enum Yaw {

	NORTH,
	SOUTH,
	WEST,
	EAST;

	public static Yaw getDirection(Player player) {
		float yaw = player.getLocation().getYaw();
		yaw = yaw < 0 ? yaw + 360 : yaw;

		if (yaw >= 315 || yaw < 45) {
			return SOUTH;
		}
		else if (yaw < 135) {
			return WEST;
		}
		else if (yaw < 225) {
			return NORTH;
		}
		else if (yaw < 315) {
			return EAST;
		}
		return NORTH;
	}

}