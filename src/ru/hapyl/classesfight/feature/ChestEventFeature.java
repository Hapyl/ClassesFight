package ru.hapyl.classesfight.feature;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChestEventFeature implements Listener {

    private static Location loc;
    private static Location center;
    private static ArmorStand bar;
    private static int health;
    private static long lastHit = -1;
    private static BukkitTask task;

    private final long invulMillis = 300;
    private final int maxHealth = 40;

    private final String $lore = ChatColor.translateAlternateColorCodes('&', "&e&lCLICK TO CLAIM");

    /**
     * Items List:
     */

    private final ItemStack smallCoins = new ItemBuilder(Material.GOLD_NUGGET).setName("&6Small Bag of Coins").addLore($lore).build();
    private final ItemStack mediumCoins = new ItemBuilder(Material.GOLD_INGOT).setName("&6Medium Bag of Coins").addLore($lore).build();
    private final ItemStack bigCoins = new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).setName("&6Big Bag of Coins").addLore($lore).build();
    private final ItemStack veryBigCoins = new ItemBuilder(Material.GOLD_BLOCK).setName("&6Very Big Bag of Coins").addLore($lore).build();
    private final ItemStack boom = new ItemBuilder(Material.TNT).setName("&cBOOM").addLore("&c&lDO NOT CLICK").build();
    private final ItemStack abilityRefresh = new ItemBuilder(Material.NETHER_STAR).setName("&5Ability Refresh!").addLore($lore).build();


    public ChestEventFeature() {
    }

    public ChestEventFeature(Location loc) {
        ChestEventFeature.loc = loc.getBlock().getLocation().clone();
        center = ChestEventFeature.loc.clone().add(0.5, 0, 0.5);
    }

    public ChestEventFeature create() {

        if (GameManager.current().isEventInProgress()) {
            System.out.println("[DEBUG] [WARNING] Server tried to create event while another is in progress!");
            return null;
        }

        GameManager.current().setEventInProgress(true);

        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest)loc.getBlock().getState();

        chest.setCustomName("Goodies Chest");
        chest.update(true);
        chest.getBlockInventory().setContents(randomizeLoot());

        bar = loc.getWorld().spawn(center, ArmorStand.class);
        bar.setVisible(false);
        bar.setSmall(true);
        bar.setGravity(false);
        bar.setInvulnerable(true);
        health = maxHealth;
        updateName();
        bar.setCustomNameVisible(true);

        return this;
    }

    public static void remove() {

        if (!GameManager.current().isEventInProgress()) {
            ClassesFight.debug("Nothing to remove, event wasn't played or already removed.");
            return;
        }

        loc.getWorld().createExplosion(center, 0, false, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1, .1, 0, .1, 0.2);
        new ParticlesBuilder.RestoneDust(center, Color.GRAY, .2, .2, .2, 30, 0.1).show();
        if (task != null) task.cancel();
        GameManager.current().setEventInProgress(false);

        Chest chest = (Chest)loc.getBlock().getState();
        chest.getBlockInventory().clear();

        loc.getBlock().setType(Material.AIR);
        bar.remove();

        loc = null;
        center = null;

    }

    private ItemStack[] randomizeLoot() {

        ItemStack[] items = new ItemStack[26];

        for (int i = 0; i < 26; i++) {

            int r = new Random().nextInt(100);
            int loot = new Random().nextInt(100);

            if (r > 30) {
                if (loot > 80) {
                    items[i] = smallCoins;
                }
                else if (loot > 40) {
                    items[i] = mediumCoins;
                }
                else if (loot > 30) {
                    items[i] = boom;
                }
                else if (loot > 10) {
                    items[i] = bigCoins;
                }
                else if (loot > 9) {
                    items[i] = abilityRefresh;
                }
                else
                    items[i] = veryBigCoins;
            }
            else items[i] = new ItemStack(Material.AIR);
        }

        return items;
    }

    private void decreaseHealth() {

        if ((System.currentTimeMillis() - lastHit) < invulMillis) return;
        health--;

        if (health < 1) {
            removeTask();
        }
        else {
            updateName();
            loc.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, SoundCategory.MASTER, 10, 1.2f);
            lastHit = System.currentTimeMillis();
        }
    }

    private void updateName() {
        bar.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6>&e>&6> &c&l" + health + " &cHealth &6<&e<&6<"));
    }

    //@EventHandler
    public void handleClaim(InventoryClickEvent ev) {
        if (GameManager.current().isEventInProgress()) {

            if (!(ev.getWhoClicked() instanceof Player)) return;

            final Player player = (Player)ev.getWhoClicked();
            final ItemStack item = ev.getCurrentItem();

            String name;

            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                name = item.getItemMeta().getDisplayName();
            }
            else return;

            switch (name.replace("§", "&")) {
                case "&6Small Bag of Coins":
                    item.setAmount(0);
                    s(player, 100, "Small");
                    break;
                case "&6Medium Bag of Coins":
                    item.setAmount(0);
                    s(player, 500, "Medium");
                    break;
                case "&6Big Bag of Coins":
                    item.setAmount(0);
                    s(player, 1500, "Big");
                    break;
                case "&6Very Big Bag of Coins":
                    item.setAmount(0);
                    s(player, 5000, "Very Big");
                    break;
                case "&5Ability Refresh!":
                    item.setAmount(0);
                    refreshAbility(player);
                    break;
                case "&cBOOM":
                    ev.getInventory().clear();
                    remove();
                    Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " has blown up the Goodies Chest!");
                    break;
            }
        }
    }

    public static boolean calculateChance(double chance) {

        if (GameManager.current().isEventPlayed()) return false;

//        if (GameScoreboard.getTime()[0] <= 0 && GameScoreboard.getTime()[1] < 30) return false;

        double random = new Random().nextDouble() * 101;
        if (chance >= random) {
            if (!GameManager.current().isEventInProgress()) {     // Make sure to create first, \/ only then other stuff!
                new ChestEventFeature(GameManager.current().getCurrentMapSpawnLocation()).create().broadcastStart().soundfx().beamUp();
                GameManager.current().setEventPlayed(true);
                return true;
            }
        }
        return false;
    }

    private void refreshAbility(Player player) {

        ClassManager cls = ClassManager.getClass(player);
        switch (cls) {

        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "⭐ " + ChatColor.DARK_PURPLE + "Your ability has been refreshed!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, SoundCategory.HOSTILE, 10, 1.7f);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.HOSTILE, 10, 2f);
        player.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, null);

    }

    private void s(Player player, int amount, String t) {
//        PlayerDatabase.Coins.add(player, amount);
        player.sendMessage(ChatColor.GOLD + "+" + amount + " Coins! (" + t + " Bag Claimed)");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 2, 1.6f);
    }

    @EventHandler
    private void handleClick(PlayerInteractEvent ev) {

        if (ev.getAction() == Action.LEFT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (ev.getClickedBlock() != null && ev.getClickedBlock().getLocation().equals(loc)) {

                if (health > 0) {
                    if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        ev.setCancelled(true);
                    }
                    else {
                        decreaseHealth();
                    }
                }
            }
        }
    }

    private void removeTask() {

        task = new BukkitRunnable() {

            long time = 10 * 20;

            @Override
            public void run() {

                if (time < 0) {
                    this.cancel();
                    remove();
                }
                if (time % 20 == 0) {
                    loc.getWorld().playSound(loc, Sound.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 10, 1);
                }

                time--;
                m(time);
            }
        }.runTaskTimer(ClassesFight.getPlugin(), 0, 1);

    }

    private void m(long time) {
        String millis = new SimpleDateFormat("S").format(time * 50);
        bar.setCustomName(ChatColor.AQUA + new SimpleDateFormat("s.").format(time * 50) + (millis.length() > 2 ? millis.substring(0, 2) : "00"));
    }

    public ChestEventFeature broadcastStart() {

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Event" + ChatColor.GOLD + "! " + ChatColor.AQUA + "Goodies Chest!");
        Bukkit.broadcastMessage(ChatColor.GRAY + "A chest with good items has spawned on the middle of the map!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEFT CLICK" + ChatColor.GRAY + " it to damage it! Once its health gone it'll open!");
        Bukkit.broadcastMessage("");

        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "EVENT", ChatColor.AQUA + "Goodies Chest!", 10, 30, 10));

        return this;
    }

    public ChestEventFeature beamUp() {
        loc.getWorld().playEffect(loc, Effect.END_GATEWAY_SPAWN, 0);
        return this;
    }

    public ChestEventFeature soundfx() {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.HOSTILE, 1, 2f));
        return this;
    }


}
