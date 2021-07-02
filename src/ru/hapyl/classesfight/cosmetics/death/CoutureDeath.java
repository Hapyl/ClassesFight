package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class CoutureDeath extends AbstractEffect {

    public CoutureDeath() {
        super("Couture", "Very Stylish!", -1, Material.GOLD_INGOT, ShopItemRarity.LEGENDARY);
    }

    @Override
    public void display(Player player) {

        final Location at = player.getLocation();

        new GameTask() {
            double phi = 0;

            @Override
            public void run() {
                phi += Math.PI / 6;
                double x, y, z;
                at.getWorld().playSound(at, Sound.ENTITY_SILVERFISH_AMBIENT, SoundCategory.RECORDS, 20, 1.75f);
                for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
                    for (double i = 0; i < 2; i++) {
                        x = 0.3 * (2 * Math.PI - t) * Math.cos(t + phi + i * Math.PI);
                        y = 0.5 * t;
                        z = 0.3 * (2 * Math.PI - t) * Math.sin(t + phi + i * Math.PI);
                        at.add(x, y, z);
                        at.getWorld().spawnParticle(Particle.CRIT, at, 1, 0, 0, 0, 0);
                        at.subtract(x, y, z);
                    }
                }
                if (phi > 7 * Math.PI) {
                    at.getWorld().playSound(at, Sound.ENTITY_SILVERFISH_DEATH, SoundCategory.RECORDS, 20, 1.25f);
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 2);

        // cool effect too!
//        GarbageCollector.add(new BukkitRunnable() {
//            double times = 0;
//
//            @Override
//            public void run() {
//                at.getWorld().playSound(at, Sound.BLOCK_NETHER_SPROUTS_PLACE, SoundCategory.RECORDS, 5, 1.75f);
//                times += 0.1 * Math.PI;
//                for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
//                    double x = times * Math.cos(t);
//                    double y = Math.exp(-0.1 * times) * Math.sin(times) + 1.5;
//                    double z = times * Math.sin(t);
//                    at.add(x, y, z);
//                    at.getWorld().spawnParticle(Particle.CRIT, at, 1, 0, 0, 0, 0);
//                    at.subtract(x, y, z);
//                }
//                if (times >= 7) {
//                    this.cancel();
//                }
//            }
//        }.runTaskTimer(Main.getMain(), 0, 1));

    }
}
