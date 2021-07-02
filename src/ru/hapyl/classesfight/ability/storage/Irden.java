package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class Irden extends Ability {

    private final int LIFE_TIME = 200;
    private final double RADIUS = 3.5d;

    public Irden() {
        // Enemies inside the aura will be slowed, invulnerable, deal reduced damage and aren't affected by knockback.
        super("Yrden", "Creates Yrden aura at your current location. Enemies inside the aura are slowed, invulnerable, weakened and aren't affected by knockback.");
        this.setCooldownSec(25);
        this.setItem(Material.POPPED_CHORUS_FRUIT);
    }

    @Override
    public Response useAbility(Player player) {

        final Location location = player.getLocation();

        new GameTask() {

            private int tick = LIFE_TIME;

            @Override
            public void run() {

                if (tick-- <= 0) {
                    this.cancel();
                    return;
                }

                // first and every 20 ticks
                if (tick % 20 == 0 || tick == (LIFE_TIME - 1)) {
                    Geometry.drawCircle(location, RADIUS, Quality.LOW, new WorldParticle(Particle.SPELL_WITCH));
                }

                GameUtils.getPlayerInRange(location, RADIUS).forEach(target -> {

                    if (target == player) {
                         return;
                    }

                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 3));
                    GameEffectManager.applyEffect(target, GameEffectType.VULNERABLE, 5, true);
                    GameEffectManager.applyEffect(target, GameEffectType.IMMOVABLE, 5, true);
                    GameEffectManager.applyEffect(target, GameEffectType.WEAKNESS, 5, true);

                });

            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
