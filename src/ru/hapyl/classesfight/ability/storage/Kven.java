package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashMap;
import java.util.Map;

public class Kven extends Ability {

    private final Map<Player, Integer> charges;

    public Kven() {
        super("Quen", "Applies two charges of Quen shield that blocks damage.");
        this.charges = new HashMap<>();
        this.setItem(Material.FLOWER_POT);
        this.setCooldownSec(30);
    }

    @Override
    public void onStop() {
        this.charges.clear();
    }

    public int getShieldCharges(Player player) {
        return this.charges.getOrDefault(player, 0);
    }

    public void decreaseShieldCharges(Player player) {
        this.charges.put(player, getShieldCharges(player) - 1);
        if (getShieldCharges(player) <= 0) {
            this.charges.remove(player);
        }
    }

    @Override
    public Response useAbility(Player player) {

        if (getShieldCharges(player) != 0) {
            return Response.error("Already have shield applied!");
        }

        Chat.sendMessage(player, "&a%s Shields have been activated!", this.getName());
        PlayerLib.playSound(player, Sound.BLOCK_BELL_RESONATE, 2.0f);
        this.charges.put(player, 2);

        new GameTask() {

            private double t1 = 0;

            @Override
            public void run() {

                final double radius = 0.75d;
                final Location location = player.getLocation().add(0, 1, 0);
                final int shieldCharges = getShieldCharges(player);

                if (shieldCharges <= 0) {
                    this.cancel();
                    return;
                }

                // First Charge
                double x = radius * Math.sin(t1);
                double z = radius * Math.cos(t1);

                location.add(x, 0, z);
                PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 1, 0, 0, 0, 0);
                location.subtract(x, 0, z);

                // a
                if (shieldCharges >= 2) {
                    location.add(z, 0.2, x);
                    PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 1, 0, 0, 0, 0);
                    location.subtract(z, 0.2, x);
                }

                if (t1 >= 36) {
                    t1 = 0;
                }
                else {
                    t1 += 0.1;
                }


            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
