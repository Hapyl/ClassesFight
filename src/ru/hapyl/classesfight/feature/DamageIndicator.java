package ru.hapyl.classesfight.feature;

import kz.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.runnable.GameTask;

import java.text.DecimalFormat;
import java.util.Random;

public class DamageIndicator {

    private static final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    public static void showDamage(Location at, double damage) {

        // don't show the fictional damage
        if (damage <= DamageFeature.abstractNullDamage) {
            return;
        }

        final Hologram hologram = new Hologram();
        hologram.addLine("&b" + decimalFormat.format(damage));
        hologram.create(randomizeLocation(at.clone().subtract(0, 1, 0)));
        hologram.showAll();

        new GameTask() {
            @Override
            public void run() {
                hologram.destroy();
            }
        }.addCancelEvent(new BukkitRunnable() {
            @Override
            public void run() {
                hologram.destroy();
            }
        }).runTaskLater(30);

    }

    private static Location randomizeLocation(Location in) {
        final double x = new Random().nextDouble() * 0.75;
        final double y = new Random().nextDouble() * 0.75;
        final double z = new Random().nextDouble() * 0.75;
        return in.clone().add(x, y, z);
    }


}
