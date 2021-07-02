package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.Collection;

public class Fireball extends Ability {

    public Fireball() {
        super("Fireball", "Casts fireball in straight line. If fireball hits its target or a wall, it explodes and applies burning damage to near players.");
        this.setCooldownSec(5);
        this.setItem(Material.BLAZE_POWDER);
    }

    @Override
    public Response useAbility(Player player) {
        shoot(player, player.getEyeLocation());
        return Response.OK;
    }

    private void shoot(Player shooter, Location location) {
        new GameTask() {

            private int tick = 0;
            private double i = 0;

            @Override
            public void run() {

                if (tick >= 100) {
                    this.cancel();
                    return;
                }

                Vector vector = location.getDirection().normalize();
                double x = vector.getX() * i;
                double y = vector.getY() * i;
                double z = vector.getZ() * i;
                location.add(x, y, z);

                if (location.getBlock().getType().isOccluding()) {
                    explode(location);
                    this.cancel();
                    return;
                }

                final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5);
                if (!entities.isEmpty()) {
                    for (Entity entity : entities) {
                        if (entity == shooter) {
                            continue;
                        }
                        if (entity instanceof Player) {
                            if (Spectator.isSpectator((Player)entity)) {
                                continue;
                            }
                            explode(entity.getLocation());
                            this.cancel();
                            return;
                        }
                    }
                }

                // display particle after 1 block so it doesn't disturb vision
                if (i > 1.0) {
                    location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0., 0, 0, 0);
                }

                if (i % 5 == 0) {
                    location.getWorld().spawnParticle(Particle.LAVA, location, 1, 0, 0, 0, 0);
                }

                location.subtract(x, y, z);
                double step = 0.5;
                tick += step;
                i += step;

            }
        }.runTaskTimer(0, 1);
    }

    private void explode(Location location) {
        float captureRadius = 2.5f;
        Geometry.drawSphere(location, 10.0d, captureRadius, new WorldParticle(Particle.FLAME));
        if (location.getWorld() == null) {
            return;
        }
        location.getWorld()
                .getNearbyEntities(location, captureRadius, captureRadius, captureRadius)
                .stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> {
                    Player player = (Player)entity;
                    GameEffectManager.applyEffect(player, GameEffectType.BURNING, 60);
                });
        location.getWorld().playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 15, 1.25f);
    }

}
