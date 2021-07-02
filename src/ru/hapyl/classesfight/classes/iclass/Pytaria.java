package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class Pytaria extends IClass {

    private final int healthRegenPercent = 20;

    public Pytaria() {
        super("Pytaria", Material.POPPY);
        this.setHealth(EnumInfo.HIGH);
        this.setRole(ClassRole.ASSASSIN);
        this.setInfo("Beautiful, but deadly opponent with addiction to flowers. She suffered all her youth, which at the end, made her only stronger.__" + Abilities.FLOWER_ESCAPE.getAbout() + Abilities.FLOWER_BREEZE.getAbout(),
                "Feel the Breeze",
                "Summons a blooming bee in front of her, that locks to a closest enemy and deals damage (if then don't have any cover) that depends on how low her health is and regenerates &b" + healthRegenPercent + "% &7of missing health.", 7);

        this.setRating(ClassRating.A);
        this.setLvlRequired(5);

        final ClassEquipment equipment = this.getClassEquipment(true);

        equipment.addItem(new ItemBuilder(Material.ALLIUM).setName("&aAnnihilallium &6&lWEAPON").setPureDamage(2.0f).applyDefaultSettings().build());

        equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JiMDc1MmY5ZmE4N2E2OTNjMmQwZDlmMjk1NDkzNzVmZWI2Zjc2OTUyZGE5MGQ2ODgyMGU3OTAwMDgzZjgwMSJ9fX0=");
        equipment.setChestplate(255, 128, 128);
        equipment.setLeggings(51, 102, 255);
        equipment.setBoots(179, 204, 204);

        Abilities.FLOWER_ESCAPE.addItemIfExists(equipment);
        Abilities.FLOWER_BREEZE.addItemIfExists(equipment);

    }

    private final ItemStack[] armorColors = {
            createChestplate(255, 128, 128),// 1
            createChestplate(255, 77, 77),  // 2
            createChestplate(255, 26, 26),  // 3
            createChestplate(179, 0, 0),    // 4
            createChestplate(102, 0, 0)     // 5
    };

    private ItemStack createChestplate(int red, int green, int blue) {
        return ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack();
    }

    private void updateChestplateColor(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final double missingHealth = DamageFeature.getMaxHealth(player) - DamageFeature.getHealth(player);

        if (isBetween(missingHealth, 0, 10)) {
            inventory.setChestplate(armorColors[0]);
        }
        else if (isBetween(missingHealth, 10, 20)) {
            inventory.setChestplate(armorColors[1]);
        }
        else if (isBetween(missingHealth, 20, 30)) {
            inventory.setChestplate(armorColors[2]);
        }
        else if (isBetween(missingHealth, 30, 40)) {
            inventory.setChestplate(armorColors[3]);
        }
        else {
            inventory.setChestplate(armorColors[4]);
        }

    }

    private boolean isBetween(double value, double min, double max) {
        return value >= min && value < max;
    }

    @Override
    public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
        if (!event.isCancelled()) {
            this.updateChestplateColor(event.getWhoGotDamaged());
        }
    }

    @Override
    public void useUltimate(Player player) {
        final double health = DamageFeature.getHealth(player);
        final double maxHealth = DamageFeature.getMaxHealth(player);
        final double missingHp = (maxHealth - health) * healthRegenPercent / maxHealth;

        final double finalDamage = multiplyDamage(health);

        final Location location = player.getLocation();
        final Vector vector = location.getDirection();
        location.add(vector.setY(0).multiply(5));
        location.add(0, 7, 0);

        final Bee pig = GarbageCollector.spawnEntity(location, Bee.class, me -> {
            me.setSilent(true);
            me.setAI(false);
        });

        final Player nearestPlayer = (Player)GameUtils.findNearestEntity(location, 50, player, EntityType.PLAYER);
        PlayerLib.playSound(location, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f);

        new GameTask() {

            private int windupTime = 60;

            @Override
            public void run() {

                final Location lockLocation = nearestPlayer == null ? location.clone().subtract(0, 9, 0) : nearestPlayer.getLocation();
                final Location touchLocation = drawLine(location.clone(), lockLocation.clone());

                // BOOM
                if (windupTime-- <= 0) {
                    PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
                    PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2, 0.2, 0.2, 0.1f);
                    PlayerLib.playSound(location, Sound.ENTITY_BEE_DEATH, 1.5f);
                    pig.remove();
                    this.cancel();

                    GameUtils.getPlayerInRange(touchLocation, 1.5d).forEach(target -> {
                        DamageFeature.damage(target, player, finalDamage, EnumDamageCause.ENTITY_ATTACK, true);
                    });

                    PlayerLib.spawnParticle(touchLocation, Particle.EXPLOSION_LARGE, 3, 0.5, 0, 0.5, 0);
                    PlayerLib.playSound(touchLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.25f);
                    return;
                }

            }
        }.runTaskTimer(0, 1);

        Chat.sendMessage(player, "&a&l][ &a%s healed for &c%s❤ &a!", this.getUltimate().getName(), BukkitUtils.decimalFormat(missingHp));
        DamageFeature.addHealth(player, missingHp);
        this.updateChestplateColor(player);
    }

    private Location drawLine(Location start, Location end) {
        double distance = start.distance(end);
        Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(0.5d);

        for (double i = 0.0D; i < distance; i += 0.5) {
            start.add(vector);
            if (start.getWorld() == null) {
                continue;
            }
            if (!start.getBlock().getType().isAir()) {
                final Location cloned = start.add(0, 0.15, 0);
                start.getWorld().spawnParticle(Particle.FLAME, cloned, 3, 0.1, 0.1, 0.1, 0.02);
                return cloned;
            }
            //start.getWorld().playSound(start, Sound.BLOCK_BAMBOO_HIT, SoundCategory.RECORDS, 1.0f, 2.0f);
            start.getWorld().spawnParticle(Particle.REDSTONE, start, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
        }
        return start;
    }

    private double multiplyDamage(double health) {
        final double initialDamage = 3.0;
        return Math.max(initialDamage * (60 / health), initialDamage);
    }

}
