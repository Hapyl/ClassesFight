package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.MeleeStance;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class StartedClass extends IClass {
    public StartedClass() {
        super("h-12", Material.IRON_PICKAXE);
        this.setInfo("Well versed fighter, who is weakest with a bow, and that's why he must master it.", Abilities.STARTED_WEAPON.getAbout() + Abilities.MELEE_STANCE.getAbout(), "Crowned Mastery", "While in &a&lRange Stance&7, summons a Rain of Fire in front of you. Anyone who crosses through it will be damaged and set on fire.__While in &a&lMelee Stance&7, summons a Sweepnado that circles around you damaging anyone who got close enough.", 6);
        this.setRole(ClassRole.RANGE);
        this.setUltimateSound(Sound.ENTITY_PLAYER_BREATH, 0.75f);

        final ClassEquipment eq = this.getClassEquipment(true);

        eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzZmE1YTE2M2Q0Mjk1ZDNkYjE0YWM0MjlmNzg4NDJjZTFiMDE3OGY3ZDI3YTc3ZTNmZGFkNjVjOGQ3NGVkNiJ9fX0=");
        eq.setChestplate(0, 172, 230);
        eq.setLeggings(71, 71, 209);
        eq.setBoots(111, 111, 109);

        Abilities.STARTED_WEAPON.addItemIfExists(eq);
        Abilities.MELEE_STANCE.addItemIfExists(eq);

    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Override
    public void useUltimate(Player player) {

        final MeleeStance meleeStance = (MeleeStance)Abilities.MELEE_STANCE.getAbility();

        // Melee Ultimate
        if (meleeStance.isInMeleeStance(player)) {
            PlayerLib.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.0f);

            new GameTask() {

                private final double distance = 2.5d;
                private double theta = 0.0d;

                @Override
                public void run() {

                    final Location location = player.getLocation();

                    // 3 is being times circling
                    if (theta < (Math.PI * 2) * 6) {

                        final double x = distance * (Math.sin(theta));
                        final double z = distance * (Math.cos(theta));

                        location.add(x, 1, z);
                        PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0.0f);
                        PlayerLib.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                        GameUtils.getPlayerInRange(location, 1.0d).forEach(range -> {
                            if (range == player) {
                                return;
                            }
                            DamageFeature.damageTick(range, player, 3.0d, EnumDamageCause.ENTITY_ATTACK, 10);
                        });
                        location.subtract(x, 1, z);

                        theta += Math.PI / 8;
                        return;
                    }

                    this.cancel();

                }
            }.runTaskTimer(0, 1);

            return;
        }

        // Range Ultimate
        PlayerLib.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 0.0f);
        final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(5.0d).setY(0.0d));
        PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 1, 0, 0, 0, 0);

        final double distance = 3.5d;

        GameTask.runTaskTimerTimes((task, tick) -> {

            GameUtils.getPlayerInRange(location, distance).forEach(target -> DamageFeature.damage(target, player, 1.0d, EnumDamageCause.ENTITY_ATTACK));

            for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 16) {
                final double x = distance * (Math.sin(d));
                final double z = distance * (Math.cos(d));
                location.add(x, 0, z);
                spawnArrow(location);
                GameUtils.getEntitiesInRange(location, distance / 2.5d).forEach(entity -> entity.setFireTicks(entity.getFireTicks() + 5));
                PlayerLib.spawnParticle(location, Particle.FLAME, 1, 0, 0, 0, 0.0f);
                location.subtract(x, 0, z);
            }

        }, 5, 25);

    }

    private void spawnArrow(Location location) {
        if (location.getWorld() == null) {
            return;
        }
        location.getWorld().spawn(location.add(0, 5, 0), Arrow.class, me -> {
            me.setVelocity(new Vector(0.0d, -0.5d, 0.0d));
            me.setDamage(5.0d);
            me.setFireTicks(500);
        });
        location.subtract(0, 5, 0);
    }

}
