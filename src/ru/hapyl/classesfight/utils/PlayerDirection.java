package ru.hapyl.classesfight.utils;

import org.bukkit.entity.Player;

public enum PlayerDirection {

    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static PlayerDirection getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return PlayerDirection.SOUTH;
        }
        else if (yaw < 135) {
            return PlayerDirection.WEST;
        }
        else if (yaw < 225) {
            return PlayerDirection.NORTH;
        }
        else if (yaw < 315) {
            return PlayerDirection.EAST;
        }
        return PlayerDirection.NORTH;
    }

}
