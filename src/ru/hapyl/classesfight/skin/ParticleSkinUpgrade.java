package ru.hapyl.classesfight.skin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSkinUpgrade extends SkinUpgrade {

    private final Particle particle;
    private final int amount;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final float speed;

    public ParticleSkinUpgrade(String name, Particle particle, int amount, float speed) {
        super(name, Type.PARTICLE);
        this.particle = particle;
        this.amount = amount;
        this.offsetX = 0.0d;
        this.offsetY = 0.0d;
        this.offsetZ = 0.0d;
        this.speed = speed;
    }

    @Override
    public void affect(Player player) {
        final Location location = player.getLocation();
        location.getWorld().spawnParticle(this.particle, location, this.amount, this.offsetX, this.offsetY, this.offsetZ, this.speed);
    }

}
