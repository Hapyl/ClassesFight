package ru.hapyl.classesfight.consumable;

import org.bukkit.Location;
import ru.hapyl.classesfight.GameMap;

public class ConsumableLocation {

    private final GameMap map;
    private final double x, y, z;

    public ConsumableLocation(GameMap map, double x, double y, double z) {
        this.map = map;
        this.x = x + 0.5;
        this.y = y - 1.416;
        this.z = z + 0.5;
    }

    public boolean compareMap() {
        return GameMap.getCurrentMap() == this.map;
    }

    public Location toLocation() {
        return new Location(this.map.getLocation().getWorld(), this.x, this.y, this.z);
    }

    public GameMap getMap() {
        return map;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }


}
