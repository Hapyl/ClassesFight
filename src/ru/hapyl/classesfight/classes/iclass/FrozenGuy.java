/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.*;

public class FrozenGuy extends IClass implements Listener {

    public final String GUN_CD_KEY = "frozen_gun";
    public final double GUN_PURE_DAMAGE = 1.7;
    public final int GUN_RELOAD_TIME = 25;

    public final double HEALING_AMOUNT = 0.25;

    public final int WALL_BUILD_DELAY = 4;
    public final int WALL_DECAY_TIME = 15;

    public final int LAVINA_COOLDOWN = 400;

    public final Map<Player, Location> wallLocation = new HashMap<>();
    public final Set<Wall> wallStorage = new HashSet<>();
    public final Set<Location> iceCollection = new HashSet<>();

    private final ItemStack WEAPON = new CFItemBuilder(Material.IRON_SHOVEL, GUN_CD_KEY).setName("&aSnowball Cannon &e&lRIGHT CLICK").setPureDamage(2.0).withCooldown(GUN_RELOAD_TIME).addClickEvent(this::shoot).applyDefaultSettings(false).build();
    private final ItemStack WALL_ITEM = new CFItemBuilder(Material.ICE, "ice_wall").setName("&aIce Barrier &e&lPLACE TO USE").addClickEvent(this::placeWall, Action.RIGHT_CLICK_BLOCK).build();
    private final ItemStack LAVINA_ITEM = new CFItemBuilder(Material.SNOWBALL).setName("&aIce Cone &e&lTHROW").build();

    public FrozenGuy() {
        super("Freazly", Material.ICE);

        this.setAttack(EnumInfo.LOW);
        this.setRating(ClassRating.A);
        this.setRole(ClassRole.RANGE);

        this.setInfo("A great warrior from the Frozen Castle is here with deadly freezing attacks! ____&e○ Snowball Cannot &e&lRIGHT CLICK &7to shoot a beam of freezing energy, damage and slowing enemies upon hit. Does not pierce blocks.____&e○ Ice Cone &7Throw a snowball that cages enemy and slows them upon hit. The ice is visible only to the enemy, everyone else can see through it!", "Ice Barrier", "Get a barrier builder block. Place to build a 5x3 Ice Wall that decay after &b" + WALL_DECAY_TIME + "s&7. The wall blocks vision and grants positive effect if near it. Also, &eSnowball Cannon &7can shoot thought this wall!", 7);
        this.setUltimateSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);

        final ClassEquipment eq = this.getClassEquipment(true);

        eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FkNzQ4NmI1ZDIwODIzZDVjMjRjYmExODUwYTYwMGE3NzQ0MjA5ODk5ODI4YjE5Y2NmOTNmNjlmMjE4NzA1OCJ9fX0=");
        eq.setChestplate(Color.AQUA);
        eq.setLeggings(Color.OLIVE);
        eq.setBoots(Color.AQUA);

        eq.addItem(WEAPON);
        eq.addItem(LAVINA_ITEM);

    }

    private void shoot(Player player) {

        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.5f);

        double MAX_DISTANCE = 30;
        double SHIFT = 0.5;

        Location location = player.getLocation().add(0, 1.5, 0);
        shoot(player, location, MAX_DISTANCE, SHIFT);

        //        AbilitiesCooldown.add(player, "Reloading", GUN_CD_KEY, GUN_RELOAD_TIME);

    }

    @Override
    public void onStart(Player player) {
    }

    @Override
    public void onStartOnce() {
        new GameTask() {
            final double radius = 3.5;

            @Override
            public void run() {
                wallLocation.forEach((player, location) -> {
                    if (player.getLocation().distance(location) <= radius) {
                        DamageFeature.addHealth(player, HEALING_AMOUNT);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1));
                    }
                    GeometryLib.drawCircle(location, 60, radius, new GeometryLib.TinyParticle(Particle.FALLING_WATER));
                    GeometryLib.drawCircle(location, 60, radius, new GeometryLib.TinyParticle(Particle.BUBBLE_POP));
                });
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public void onStopOnce() {
        this.wallStorage.forEach(wall -> {
            for (Block block : wall.getBlocks()) {
                block.setType(Material.AIR, false);
            }
        });
        this.wallStorage.clear();
        this.wallLocation.clear();
        this.iceCollection.forEach(location -> {
            location.getBlock().getState().update(false, false);
        });
        this.iceCollection.clear();
    }

    @Override
    public void useUltimate(Player player) {
        player.getInventory().addItem(WALL_ITEM);
    }

    @Override
    public String predicateMessage() {
        return "You can only have 1 wall!";
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return !player.getInventory().contains(WALL_ITEM);
    }

    @EventHandler
    public void handleShooting(ProjectileLaunchEvent ev) {

        if (!GameManager.current().isGameInProgress()) {
            return;
        }

        if (ev.getEntity() instanceof Snowball) {

            final ProjectileSource shooter = ev.getEntity().getShooter();
            final Snowball snowball = (Snowball)ev.getEntity();

            if (shooter instanceof Player) {
                Player player = (Player)shooter;

                player.setCooldown(Material.SNOWBALL, LAVINA_COOLDOWN);
                player.getInventory().addItem(LAVINA_ITEM);

                //                AbilitiesCooldown.add(player, "Ice Cone", "ice_cone", 400, LAVINA_ITEM);
                snowball.addScoreboardTag("cone_snowball");
                snowball.setCustomName(player.getUniqueId().toString());

            }

        }
    }

    @EventHandler
    public void handleSnowball(ProjectileHitEvent ev) {

        if (ev.getEntity() instanceof Snowball) {
            if (ev.getHitEntity() == null) {
                return;
            }

            final Snowball snowball = (Snowball)ev.getEntity();
            final Entity entity = ev.getHitEntity();

            if (!snowball.getScoreboardTags().contains("cone_snowball") || snowball.getCustomName() == null) {
                return;
            }
            final Player owner = Bukkit.getPlayer(UUID.fromString(snowball.getCustomName()));
            snowball.remove();

            if (owner != null && entity instanceof Player) {
                if (Spectator.isSpectator((Player)entity)) {
                    return;
                }
                cone((Player)entity, owner);
            }

        }

    }

    private void cone(Player player, Player owner) {

        final Location location = player.getLocation();
        Chat.sendMessage(owner, "&aYour snowball hit %s!", player.getName());
        Chat.sendMessage(player, "&aYou got hit by %s's snowball! &e&lCLICK &aIce to remove it!", owner.getName());

        final BlockData data = Material.ICE.createBlockData();

        player.sendBlockChange(saveLocation(location.clone().add(1, 0, 0)), data);
        player.sendBlockChange(saveLocation(location.clone().add(-1, 0, 0)), data);
        player.sendBlockChange(saveLocation(location.clone().add(1, 1, 0)), data);
        player.sendBlockChange(saveLocation(location.clone().add(-1, 1, 0)), data);

        player.sendBlockChange(saveLocation(location.clone().add(0, 0, 1)), data);
        player.sendBlockChange(saveLocation(location.clone().add(0, 0, -1)), data);
        player.sendBlockChange(saveLocation(location.clone().add(0, 1, 1)), data);
        player.sendBlockChange(saveLocation(location.clone().add(0, 1, -1)), data);

        player.sendBlockChange(saveLocation(location.clone().add(0, 2, 0)), data);

        player.teleport(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch()));
        PlayerLib.playSound(player, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));

    }

    private Location saveLocation(Location loc) {
        iceCollection.add(loc);
        return loc;
    }

    private void placeWall(Player player) {

        final Block block = player.getTargetBlockExact(5);
        if (block == null) {
            return;
        }

        final Location location = block.getRelative(BlockFace.UP).getLocation();
        if (!location.getBlock().getType().isAir()) return;

        // check the direction player is looking
        final Yaw direction = Yaw.getDirection(player);

        GameUtils.removeHeldItem(player, 1);
        final Wall wall = new Wall(player);

        wallLocation.remove(player);
        wallLocation.put(player, location.clone().add(0.5, 0, 0.5));

        final boolean bool = direction == Yaw.EAST || direction == Yaw.WEST;
        Location startLocation = location.subtract((bool ? 0 : 2), 0, (bool ? 2 : 0));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                final Block blockToChange = startLocation.add(bool ? 0 : j, i, bool ? j : 0).getBlock();
                if (blockToChange.getType().isAir()) wall.add(blockToChange);
                startLocation.subtract(bool ? 0 : j, i, bool ? j : 0);
            }
        }

        wallStorage.add(wall);
        wall.buildSmooth(WALL_BUILD_DELAY, WALL_DECAY_TIME);

    }

    public static class Wall {

        private final List<Block> blocks;
        private final Player owner;
        private boolean built;

        Wall(Player owner) {
            this.blocks = new ArrayList<>();
            this.owner = owner;
            this.built = false;
        }

        public Wall add(Block block) {
            blocks.add(block);
            return this;
        }

        public void build() {
            for (Block block : this.blocks) {
                block.setType(Material.ICE, false);
            }
            built = true;
        }

        public void buildSmooth(int delayEachTime, int destroyAfter) {

            final List<Block> blocks = getBlocks();
            if (blocks.isEmpty()) {
                return;
            }

            // Iteration 1
            new GameTask() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        if (blocks.size() <= i) break;
                        final Block theBlock = blocks.get(i);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.6f);
                        theBlock.setType(Material.ICE, false);
                    }
                }
            }.runTaskLater(delayEachTime);

            // Iteration 1
            new GameTask() {
                @Override
                public void run() {
                    for (int i = 5; i < 10; i++) {
                        if (blocks.size() <= i) break;
                        final Block theBlock = blocks.get(i);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.6f);
                        theBlock.setType(Material.ICE, false);
                    }
                }
            }.runTaskLater(delayEachTime * 2L);

            // Iteration 2
            new GameTask() {
                @Override
                public void run() {
                    for (int i = 10; i < 15; i++) {
                        if (blocks.size() <= i) break;
                        final Block theBlock = blocks.get(i);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
                        SoundLib.play(theBlock.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.6f);
                        theBlock.setType(Material.ICE, false);
                    }

                    built = true;
                    if (destroyAfter > 0) {
                        decay(destroyAfter);
                    }
                }
            }.runTaskLater(delayEachTime * 3L);

        }

        public void destroy() {
            for (Block block : this.blocks) {
                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 10, 2f);
                block.setType(Material.AIR, false);
            }
            this.blocks.clear();
            final FrozenGuy instance = (FrozenGuy)ClassManager.FROZEN_GUY.getTheClass();
            instance.wallLocation.remove(this.owner);
            instance.wallStorage.remove(this);
        }

        private void decay(int overSeconds) {

            if (!built) {
                return;
            }

            final int delay = overSeconds / 4 * 20;

            new GameTask() {

                private int currentTick = 0;

                @Override
                public void run() {

                    for (Block block : blocks) {
                        block.setType(Material.FROSTED_ICE, false);
                        final Ageable blockData = (Ageable)block.getBlockData();
                        blockData.setAge(Math.min(currentTick, 3));
                        block.setBlockData(blockData, false);
                    }

                    if (currentTick++ > 3) {
                        destroy();
                        this.cancel();
                    }
                }
            }.runTaskTimer(delay, delay);

        }

        public List<Block> getBlocks() {
            return this.blocks;
        }

    }

    private enum Yaw {

        NORTH, SOUTH, WEST, EAST;

        public static Yaw getDirection(Player player) {
            float yaw = player.getLocation().getYaw();
            yaw = yaw < 0 ? yaw + 360 : yaw;

            if (yaw >= 315 || yaw < 45) {
                return SOUTH;
            }
            else if (yaw < 135) {
                return WEST;
            }
            else if (yaw < 225) {
                return NORTH;
            }
            else if (yaw < 315) {
                return EAST;
            }
            return NORTH;
        }

    }

    private void shoot(Player shooter, Location location, double times, double shift) {

        main:
        for (double i = 0; i < times; i += shift) {

            Vector vector = location.getDirection().normalize();
            double x = vector.getX() * i;
            double y = vector.getY() * i;
            double z = vector.getZ() * i;
            location.add(x, y, z);

            // check for the hitting a block and an entity
            final Material type = location.getBlock().getType();
            if (type.isOccluding()) {
                if (type == Material.ICE || type == Material.FROSTED_ICE) continue;
                else break;
            }

            final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5);
            if (!entities.isEmpty()) {
                for (Entity entity : entities) {
                    if (entity == shooter) continue;
                    if (entity instanceof Player) {
                        if (Spectator.isSpectator((Player)entity)) continue;
                        DamageFeature.damage((Player)entity, shooter, GUN_PURE_DAMAGE, EnumDamageCause.FROZEN_WEAPON);
                        ((Player)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1));
                        break main;
                    }
                }
            }

            // display particle after 1 block so it doesn't disturb vision
            if (i > 1.0) {
                location.getWorld().spawnParticle(Particle.SNOWBALL, location, 1, 0., 0, 0, 0);
            }
            location.subtract(x, y, z);
        }
    }

}
