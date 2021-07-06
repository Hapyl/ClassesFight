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