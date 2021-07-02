package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.AbilitiesCooldown;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.gameeffect.Stun;
import ru.hapyl.classesfight.utils.*;

import java.util.Collection;

import static ru.hapyl.classesfight.classes.ClassManager.NINJA;

public class NinjaClass extends IClass implements Listener {

    public final double PURE_DAMAGE = 3.0;
    public final double THROWING_STAR_DAMAGE = 7.5d;

    public final int ABILITY_CD_DOUBLE_JUMP = 5;
    public final int ABILITY_CD_STRONG_WEAPON = 10;
    public final int ULTIMATE_ITEM_AMOUNT = 5;

    public final ItemStack STRONG_WEAPON = new CFItemBuilder(Material.STONE_SWORD).setName("&e斬馬刀 &e&lHIT TO STUN").setPureDamage(PURE_DAMAGE).applyDefaultSettings().build();
    public final ItemStack NORMAL_WEAPON = new CFItemBuilder(Material.STONE_SWORD).setName("&e斬馬刀").setPureDamage(PURE_DAMAGE / 2).applyDefaultSettings().build();
    public final ItemStack DASH_ITEM = new CFItemBuilder(Material.FEATHER, "ninja_dash").withCooldown(100, g -> !GameManager.current().arePlayersRevealed()).setName("&aDashing Wind &6&lCLICK").addClickEvent(this::dash, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR).applyDefaultSettings().build();
    public final ItemStack THROWING_STAR = new CFItemBuilder(Material.NETHER_STAR, "throwing_star").setAmount(ULTIMATE_ITEM_AMOUNT).withCooldown(6).setName("&aThrowing Star").addClickEvent(this::throwStar).build();

    public NinjaClass() {
        super("Ninja", Material.IRON_BOOTS);

        this.setDefense(EnumInfo.LOW);
        this.setRole(ClassRole.ASSASSIN);
        this.setLvlRequired(4);
        this.setRating(ClassRating.NOT_YET);

        this.setInfo("Extremely well trained fighter with a gift from the wind, that allows him to Dash, Double Jump and take no fall damage.____&e○ 斬馬刀 &7Light but Sharp sword, that can stun the enemy when charged. Stun deals &c" + this.PURE_DAMAGE + "❤ &7of damage and cools down for &b" + BukkitUtils.roundTick(this.ABILITY_CD_STRONG_WEAPON * 20) + "s&7. While in cooldown, damage is reduced by &b50%&7.____&e○ Dashing Wind &7Instantly dash forward. &b(5s cd)__" + Abilities.SMOKE_BOMB.getAbout(),
                "Throwing Stars",
                "Equip 5 dead-accurate throwing stars that deals " + THROWING_STAR_DAMAGE + " damage upon hitting an enemy. &e&lRIGHT CLICK &7to thrown a star.",
                7);
        this.setUltimateSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f);

        final ClassEquipment eq = this.getClassEquipment(true);

        eq.setLeggings(Material.CHAINMAIL_LEGGINGS);
        eq.setBoots(Material.CHAINMAIL_BOOTS);

        eq.addItem(STRONG_WEAPON);
        eq.addItem(DASH_ITEM);
        Abilities.SMOKE_BOMB.addItemIfExists(eq);

    }

    @Override
    public void onStart(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 1));
    }

    @Override
    public void useUltimate(Player player) {
        player.getInventory().setItem(4, THROWING_STAR);
        player.getInventory().setHeldItemSlot(4);
    }

    @Override
    public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
        if (GameEffectManager.playerHasEffect(player, GameEffectType.INVISIBILITY)) {
            GameEffectManager.removeEffect(player, GameEffectType.INVISIBILITY);
            Chat.sendMessage(player, "&aYou dealt damage and you're Invisibility is gone!");
        }
    }

    @Override
    public void onPlayerRevealed(Player player) {
        player.setAllowFlight(true);
    }

    public void throwStar(Player player) {

        GameUtils.removeHeldItem(player, 1);
        SoundLib.play(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1.5f);

        double MAX_DISTANCE = 40; // doubled since shift is 0.5
        double SHIFT = 0.5;

        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();

        main:
        for (double i = 0; i < MAX_DISTANCE; i += SHIFT) {

            double x = vector.getX() * i;
            double y = vector.getY() * i;
            double z = vector.getZ() * i;
            location.add(x, y, z);

            // check for the hitting a block and an entity
            if (location.getBlock().getType().isOccluding()) {
                SoundLib.play(location.getBlock().getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, 2);
                break;
            }

            // get the entities and apply damage to the player
            final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5);
            if (!entities.isEmpty()) {
                for (Entity entity : entities) {
                    if (entity instanceof Player) {
                        if (entity == player || Spectator.isSpectator((Player)entity)) {
                            continue;
                        }
                        SoundLib.play(entity.getLocation(), Sound.ITEM_TRIDENT_HIT, 2);
                        DamageFeature.damage((Player)entity, player, THROWING_STAR_DAMAGE, EnumDamageCause.PROJECTILE);
                        break main;
                    }
                }
            }

            // display particle after 1 block so it doesn't disturb vision
            if (i > 1.0) {
                location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0., 0, 0, 0);
            }

            location.subtract(x, y, z);

        }
    }

    public void dash(Player player) {

        final Vector vector = player.getLocation().getDirection();
        player.setVelocity(new Vector(vector.getX(), 0, vector.getZ()).normalize().multiply(1.5f));
        SoundLib.play(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.1f);

    }

    public void doubleJump(Player player) {

        if (Spectator.isSpectator(player)) return;
        player.setVelocity(new Vector(0, 1.0, 0));
        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 5, 0.2d, 0, 0.5d, 0.03d);
        SoundLib.play(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.2f);
        player.setFlying(false);
        player.setAllowFlight(false);
        AbilitiesCooldown.add(player, "Double Jump", "n.dj", ABILITY_CD_DOUBLE_JUMP * 20, ninja -> {
            ninja.setFlying(false);
            ninja.setAllowFlight(true);
        });
    }

    @EventHandler
    public void handleDamageToPlayer(EntityDamageByEntityEvent ev) {
        if (ev.getDamager() instanceof Player) {
            if (ev.getEntity() instanceof LivingEntity) {

                final LivingEntity player = (LivingEntity)ev.getEntity();
                final Player damager = (Player)ev.getDamager();

                if (ClassManager.getClass(damager) == NINJA) {
                    final ItemStack theItem = damager.getInventory().getItemInMainHand();
                    if (theItem.isSimilar(STRONG_WEAPON)) {
                        stun(damager, player);
                    }
                }
            }
        }
    }

    private void stun(Player damager, LivingEntity ent) {

        ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
        ent.sendMessage(Chat.format("&aYou are stunned!"));
        SoundLib.play(ent.getLocation(), Sound.BLOCK_ANVIL_PLACE, 2);

        damager.getInventory().setItem(0, NORMAL_WEAPON);
        AbilitiesCooldown.add(damager, "Strong Sword", "n.sword", ABILITY_CD_STRONG_WEAPON * 20, g -> g.getInventory().setItem(0, STRONG_WEAPON));

    }

    @EventHandler
    public void handleFlight(PlayerToggleFlightEvent ev) {
        if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
            return;
        }
        final Player player = ev.getPlayer();
        if (ClassManager.getClass(player) == NINJA) {
            if (!player.isFlying()) {
                doubleJump(player);
            }
        }
    }

}
