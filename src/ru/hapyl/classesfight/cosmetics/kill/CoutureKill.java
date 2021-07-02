package ru.hapyl.classesfight.cosmetics.kill;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class CoutureKill extends AbstractEffect {

    public CoutureKill() {
        super("Couture", "Very Stylish!", -1, Material.GOLD_INGOT, ShopItemRarity.LEGENDARY);
    }

    @Override
    public void display(Player player) {

        final Particle particle = Particle.CRIT;
        final Location at = player.getLocation();

        GarbageCollector.add(new BukkitRunnable() {
            double t = 0;
            float pitch = 1;

            @Override
            public void run() {
                pitch += Math.min(0.015, 2.0f);
                t += Math.PI / 16;
                at.getWorld().playSound(at, Sound.ENTITY_CHICKEN_EGG, SoundCategory.RECORDS, 20, pitch);
                at.getWorld().playSound(at, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, 20, pitch);
                at.getWorld().playSound(at, Sound.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.RECORDS, 20, pitch - 0.5f);
                double x = 0, y = 0, z = 0;
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 4) {
                    x = 0.3 * (4 * Math.PI - t) * Math.cos(t + phi);
                    y = 0.2 * t;
                    z = 0.3 * (4 * Math.PI - t) * Math.sin(t + phi);
                    at.add(x, y, z);
                    at.getWorld().spawnParticle(particle, at, 1, 0, 0, 0, 0);
                    at.subtract(x, y, z);
                }

                if (t >= 4 * Math.PI) {
                    at.add(x, y, z);
                    at.getWorld().playSound(at, Sound.ENTITY_PLAYER_BURP, SoundCategory.RECORDS, 20, 1.25f);
                    at.getWorld().spawnParticle(particle, at, 40, 0, 0, 0, 1);
                    this.cancel();
                }

            }
        }.runTaskTimer(Main.getMain(), 0, 1));
    }

}
