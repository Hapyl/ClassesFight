package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.Random;

public class Pyrotand extends IClass {

    public Pyrotand() {
        super("Arch-vile", Material.FIRE_CHARGE);
        this.setInfo("A pyrotechnic who had an accident... that he tried not to talk about. But with power of both &cPyre &7and &9Hydro&7, makes himself a tough opponent.__" + Abilities.FIRE_BALL.getAbout() + Abilities.HOMING_WATER_BALL.getAbout(),
                "Coin Flip",
                "Flips a coin to decide which element to use to annihilate his enemies. &eHeads &7- applies burning to all players in radius. &eTails &7- applies drowning effect, slowing enemies.", 7);
        this.setRole(ClassRole.STRATEGIST);
        this.setRating(ClassRating.S);
        this.setLvlRequired(2);

        final ClassEquipment equipment = this.getClassEquipment(true);

        equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBlOTllZDY1MWU3YTUxZGQ3MmE0ZDRiYjUwNGEzZmQ3MzVkYTYxZjUxNjQzMjJkNmY0OTJkNzE2MjIxZDQyYyJ9fX0=");
        equipment.setChestplate(133, 0, 0);
        equipment.setLeggings(153, 0, 0);
        equipment.setBoots(179, 0, 0);

        equipment.addItem(new CFItemBuilder(Material.IRON_SWORD).setName("&aKaa'h").setPureDamage(4.0d).applyDefaultSettings(false).build());

        Abilities.FIRE_BALL.addItemIfExists(equipment);
        Abilities.HOMING_WATER_BALL.addItemIfExists(equipment);

    }

    @Override
    public void onStart(Player player) {
    }

    @Override
    public void useUltimate(Player player) {

        final boolean bool = new Random().nextBoolean();
        final int windUpTime = 30;

        GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, windUpTime);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 5));

        new GameTask() {

            private final Location location = player.getLocation();
            private int tick = 0;

            @Override
            public void run() {

                if ((tick % 5) == 0) {
                    Geometry.drawCircle(location, 7.5d, Quality.NORMAL, new Draw(Particle.LAVA) {
                        @Override
                        public void draw(Location location) {
                            if (location.getWorld() == null) {
                                return;
                            }
                            location.getWorld().spawnParticle((bool ? Particle.FLAME : Particle.WATER_SPLASH), location, 1, 0.1, 0.05, 0.1, 0.01);
                        }
                    });
                }

                // affect
                if (tick >= windUpTime) {
                    player.removePotionEffect(PotionEffectType.SLOW);

                    final World world = location.getWorld();
                    if (world == null) {
                        return;
                    }

                    if (bool) {
                        world.spawnParticle(Particle.LAVA, location, 30, 3.5, 0.1d, 3.5, 0.1);
                        world.spawnParticle(Particle.FLAME, location, 30, 3.5, 0.1d, 3.5, 0.1);
                        world.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 10, 0.85f);
                    }
                    else {
                        world.spawnParticle(Particle.WATER_BUBBLE, location, 30, 3.5, 0.1d, 3.5, 0.1);
                        world.spawnParticle(Particle.WATER_SPLASH, location, 30, 3.5, 0.1d, 3.5, 0.1);
                        world.playSound(location, Sound.AMBIENT_UNDERWATER_ENTER, 10, 0.5f);
                    }

                    player.getNearbyEntities(7.5d, 7.5d, 7.5d)
                            .stream()
                            .filter(entity -> entity instanceof Player).forEach(ent -> {
                        Player tar = (Player)ent;
                        if (Spectator.isSpectator(tar) || tar == player) {
                            return;
                        }
                        DamageFeature.damage(tar, player, 8.5d, EnumDamageCause.ENTITY_ATTACK);
                        GameEffectManager.applyEffect(tar, bool ? GameEffectType.BURNING : GameEffectType.DROWNING, 120);
                    });

                    this.cancel();
                    return;
                }

                ++tick;

            }
        }.runTaskTimer(0, 1);

    }

}
