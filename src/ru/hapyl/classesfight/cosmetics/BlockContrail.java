package ru.hapyl.classesfight.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.concurrent.ThreadLocalRandom;

public class BlockContrail extends AbstractEffect {

    private static final String suffixLore = "____&6This &6is &6a &6Block&6 Contrail! &6It &6will &6transform &6blocks &6you &6step &6on.";

    private Material[] blocks;

    public BlockContrail(String name, String lore, long cost, Material icon, ShopItemRarity rarity, Material block) {
        this(name, lore, cost, icon, rarity, new Material[]{block});
    }

    public BlockContrail(String name, String lore, long cost, Material icon, ShopItemRarity rarity, Material... blocks) {
        super(name, lore + suffixLore, cost, icon, rarity);
        for (Material material : blocks) {
            if (!material.isBlock()) {
                throw new IllegalStateException("Material must be block");
            }
        }
        this.blocks = blocks;
    }

    public BlockContrail(String name, String lore, long cost, Material icon, ShopItemRarity rarity) {
        this(name, lore, cost, icon, rarity, (Material)null);
    }

    public void setBlocks(Material... blocks) {
        this.blocks = blocks;
    }

    /**
     * This displays after the block replacements
     *
     * @param player   - viewer
     * @param location - where to display
     */
    public void additionalEffect(Player player, Location location) {
    }

    @Override
    public final void display(Player player) {
        final Location below = player.getLocation().subtract(0, 1, 0);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) {
                showAt(below, online);
            }
            else {
                final boolean enabled = Database.getDatabase(online).getSettingEntry().isEnabled(Setting.SEE_CONTRAILS);
                if (enabled || Spectator.isSpectator(online)) {
                    showAt(below, online);
                }
            }
        }
        scheduleBlockReset(below, 20);
    }

    private void scheduleBlockReset(Location where, int ticks) {
        new GameTask() {
            @Override
            public void run() {
                where.getBlock().getState().update(false, false);
            }
        }.runTaskAtCancel().runTaskLater(ticks);
    }

    private boolean isValidBlock(Block block) {
        if (block == null) {
            return false;
        }
        final Material type = block.getType();
        return !type.isAir() && !block.isLiquid() && type.isOccluding();
    }

    /**
     * @return either a blocks or random one if supplied multiple
     */
    public Material getBlock() {
        return blocks.length == 1 ? blocks[0] : blocks[(ThreadLocalRandom.current().nextInt(blocks.length))];
    }

    private void showAt(Location where, Player viewer) {
        if (isValidBlock(where.getBlock())) {
            viewer.sendBlockChange(where, getBlock().createBlockData());
            this.additionalEffect(viewer, where);
        }
    }
}
