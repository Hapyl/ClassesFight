package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

import java.util.HashMap;
import java.util.Map;

public class MoonPillar extends Ability {

    private final Map<Player, Location> pillars;

    public MoonPillar() {
        super("Moonsplite Pillar", "Raises a pillar at target location for &b10s &7that pulses in set intervals, damaging enemies and healing yourself. You can only have 1 pillar at the time.");
        this.setCooldownSec(30);
        this.setItem(Material.BONE);
        this.pillars = new HashMap<>();
    }

    @Override
    public void onStop() {
        this.pillars.clear();
    }

    @Nullable
    public Location getPillarLocation(Player player) {
        return this.pillars.getOrDefault(player, null);
    }

    @Override
    public Response useAbility(Player player) {

        if (getPillarLocation(player) != null) {
            return Response.error("You already have a " + this.getName() + "!");
        }

        final Location location = getTargetLocation(player);

        if (location == null) {
            return Response.error("No valid block in sight!");
        }

        final double distance = location.distance(player.getLocation());
        if (distance >= 7.5d) {
            return Response.error("Too far away! Max 7.5 blocks. (" + BukkitUtils.decimalFormat(distance) + ")");
        }

        if (!canFit(location)) {
            return Response.error("Cannot fit pillar!");
        }

        this.pillars.put(player, location);
        raisePillar(location);

        final int period = 5;
        new GameTask() {

            private int tick = 0;

            @Override
            public void run() {
                if ((tick += period) >= (10 * 20)) {
                    destroyPillar(location);
                    pillars.remove(player);  // npe/concurrent check?
                    this.cancel();
                    return;
                }

                // pulse
                if (tick % 15 == 0) {
                    pulsePillar(location, player);
                }

            }
        }.addCancelEvent(new BukkitRunnable() {
            @Override
            public void run() {
                destroyPillar(location);
            }
        }).runTaskTimer(0, period);

        return Response.OK;
    }

    private void pulsePillar(Location location, Player owner) {
        final double effectRange = 2.5d;
        final BlockData blockData = Material.END_STONE.createBlockData();
        Geometry.drawCircle(location, effectRange, Quality.NORMAL, new Draw(Particle.BLOCK_DUST) {
            @Override
            public void draw(Location location) {
                final World world = location.getWorld();
                if (world != null) {
                    world.spawnParticle(this.getParticle(), location, 0, 0, 0, 0, blockData);
                }
            }
        });
        PlayerLib.playSound(location, Sound.BLOCK_STONE_BREAK, 0.0f);
        final World world = location.getWorld();
        if (world != null) {
            world.getNearbyEntities(location, effectRange, effectRange, effectRange).stream().filter(e -> e instanceof Player).forEach(player -> {
                if (player == owner) {
                    DamageFeature.addHealth(owner, 1.0d);
                    owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 2, true, false));
                    world.spawnParticle(Particle.HEART, ((Player)player).getEyeLocation().clone().add(0, 0.5d, 0), 1, 0, 0, 0, 0);
                }
                else {
                    DamageFeature.damage((Player)player, owner, 2.0d, EnumDamageCause.ENTITY_ATTACK);
                    player.setVelocity(player.getLocation().getDirection().multiply(-0.5).setY(0.25d));
                }
            });
        }
    }

    private void raisePillar(Location location) {
        location.getBlock().setType(Material.END_STONE_BRICKS, false);
        location.getBlock().getRelative(BlockFace.UP).setType(Material.END_STONE_BRICKS, false);
        location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.END_STONE, false);

        // fx
        PlayerLib.playSound(location, Sound.BLOCK_PISTON_EXTEND, 0.25f);
    }

    private void destroyPillar(Location location) {
        // already destroyed
        if (location.getBlock().getType().isAir()) {
            return;
        }
        location.getBlock().setType(Material.AIR, false);
        location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
        location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR, false);

        //fx
        PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);

        if (location.getWorld() == null) {
            throw new NullPointerException("world null");
        }
        location.getWorld().spawnParticle(Particle.SPIT, location.clone().add(0, 2, 0), 15, 0, 1, 0, 0.05);
    }

    private boolean canFit(Location location) {
        final Block block = location.getBlock();
        return block.getType().isAir() && block.getRelative(BlockFace.UP).getType().isAir() && block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isAir();
    }

    @Nullable
    private Location getTargetLocation(Player player) {
        final Block block = player.getTargetBlockExact(25);
        if (block == null) {
            return null;
        }
        return block.getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
    }

}
