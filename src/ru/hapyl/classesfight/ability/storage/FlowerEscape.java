package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class FlowerEscape extends Ability {

    private final int flowerLifeTicks = 120;

    private final double flowerRadius = 2.5;
    private final double flowerDamage = 3.0d;

    public FlowerEscape() {
        super("Flower Escape", "Throws a deadly flower on current location and dashes backwards. Flower deals continuous damage in set intervals and explodes after it's duration dealing double the damage. Damage is scaled with health.");
        this.setCooldown((int)(flowerLifeTicks * 2.0));
        this.setItem(Material.POPPY);
    }

    @Override
    public Response useAbility(Player player) {

        final Location location = player.getLocation();
        final Vector vector = player.getLocation().getDirection().normalize().multiply(-1.5);
        player.setVelocity(vector.setY(0.5d));

        final ArmorStand entity = GarbageCollector.spawnEntity(location, ArmorStand.class, me -> {
            me.setMarker(true);
            me.setInvisible(true);
            me.getEquipment().setHelmet(new ItemStack(Material.POPPY));
        });

        final double finalDamage = multiplyDamage(player);

        new GameTask() {

            private int tick = flowerLifeTicks;

            @Override
            public void run() {

                if (tick-- <= 0) {
                    entity.remove();
                    explode(player, location, finalDamage);
                    this.cancel();
                    return;
                }

                // pulse
                if (tick % 20 == 0) {

                    // fx
                    Geometry.drawCircle(location, flowerRadius, Quality.LOW, new Draw(Particle.TOTEM) {
                        @Override
                        public void draw(Location location) {
                            if (location.getWorld() == null) {
                                return;
                            }
                            location.getWorld().spawnParticle(this.getParticle(), location, 1, 0, 0, 0, 0.2);
                        }
                    });

                    GameUtils.getPlayerInRange(location, flowerRadius).forEach(target -> DamageFeature.damage(target, player, finalDamage, EnumDamageCause.ENTITY_ATTACK));

                    PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.75f);
                    final float pitch = Math.min(0.5f + ((0.1f * (((float)flowerLifeTicks - tick) / 20))), 2.0f);
                    PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_COW_BELL, pitch);

                }


            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    private void explode(Player owner, Location location, double damage) {
        PlayerLib.playSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
        PlayerLib.spawnParticle(location, Particle.SPELL_MOB, 15, 1, 0.5, 1, 0);
        GameUtils.getPlayerInRange(location, flowerRadius).forEach(player -> {
            DamageFeature.damage(player, owner, damage * 2, EnumDamageCause.ENTITY_ATTACK);
        });
    }

    private double multiplyDamage(Player player) {
        return Math.max(flowerDamage * (10 / DamageFeature.getHealth(player)), flowerDamage);
    }

}
