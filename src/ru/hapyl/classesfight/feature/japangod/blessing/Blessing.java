package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Blessing {

    void bless(Player player);

    String message();

    default void spawnFx(Location location) {

    }

    void playSound(Player player);

}
