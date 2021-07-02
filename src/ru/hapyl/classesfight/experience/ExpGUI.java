package ru.hapyl.classesfight.experience;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.experience.reward.PerksGUI;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.List;

public class ExpGUI extends PlayerGUI {

    private final Experience exp;

    public ExpGUI(Player player) {
        super(player, "Levelling", 6);
        this.exp = Experience.getInstance();
        this.updateMenu(1);
    }

    private final int[] levelSlots = {9, 18, 27, 28, 29, 20, 11, 2, 3, 4, 13, 22, 31, 32, 33, 24, 15, 6, 7, 8, 17, 26, 35, 44, 53};

    private void updateMenu(int startAt) {

        this.fillItem(0, this.getSize() - 1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").toItemStack());
        this.setItem(0, new ItemBuilder(Material.CHEST).setName("&aGlobal Skill").setSmartLore("Level up this skill by playing and unlock some exclusive rewards!").toItemStack());

        // Next Page
        this.setItem(51, ItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19").setName("&aTo Veteran Rewards").toItemStack());
        this.setClick(51, player -> PlayerLib.villagerNo(player, "&cYou must first unlock level 25!"));

        if (Perk.hasAnyPerks(getPlayer())) {
            this.setItem(47, new ItemBuilder(Material.NETHER_STAR).setName("&aPerks").setSmartLore("Browse and manage your perks!").toItemStack());
            this.setClick(47, PerksGUI::new);
        }

        final int currentLvl = exp.getLvl(getPlayer());

        for (int i = 0; i < 25; i++, startAt++) {
            final Reward reward = exp.getReward(startAt);
            this.setItem(levelSlots[i], this.buildRewardItem(reward, i + 1, currentLvl + 1 == startAt ?
                    IconType.NEXT_LEVEL : currentLvl >= startAt ? IconType.HAS_LEVEL : IconType.LOCKED_LEVEL));
        }

        this.openInventory();
    }

    private ItemStack buildRewardItem(Reward reward, int nextLvl, IconType type) {

        final long totalExpForNextLvlScaled = exp.getExpNeededFor(nextLvl) - exp.getExpNeededFor(nextLvl - 1);
        final long currentExpScaled = exp.getExp(getPlayer()) - exp.getExpNeededFor(nextLvl - 1);

        final double percentDoneBars = (double)(currentExpScaled * 20 / totalExpForNextLvlScaled);
        final double percentDone = (double)(currentExpScaled * 100 / totalExpForNextLvlScaled);

        final boolean specialReward = reward != null && reward.getRareReward() != null;

        final CFItemBuilder builder = new CFItemBuilder(((nextLvl - 1) % 5 == 4) ? Material.GOLD_BLOCK : type.getMaterial());
        builder.setName((specialReward ? ChatColor.GOLD : type == IconType.LOCKED_LEVEL ? ChatColor.RED : type == IconType.NEXT_LEVEL ? ChatColor.YELLOW : ChatColor.GREEN) + "Global " + nextLvl);
        builder.setAmount(nextLvl);
        builder.addLore("");
        builder.addLore("&7Rewards:");

        final boolean hasLevel = type == IconType.HAS_LEVEL;

        if (type == IconType.LOCKED_LEVEL) {
            builder.addLore("  &8???");
        }
        else {
            if (reward != null) {

                final RareReward rareReward = reward.getRareReward();

                builder.addLore(String.format("  %s&6%s Coins", hasLevel ? "&a&l✔ " : "", reward.coinsReward()));
                if (reward.rubiesReward() > 0) {
                    builder.addLore(String.format("  %s&6%s %s", hasLevel ? "&a&l✔ " : "", reward.rubiesReward(), (reward.rubiesReward() == 1) ? "Ruby" : "Rubies"));
                }

                // class unlock reward
                final List<ClassManager> classesAtLvl = ClassManager.getClassesAtLvl(nextLvl);
                if (!classesAtLvl.isEmpty()) {
                    for (ClassManager classManager : classesAtLvl) {
                        builder.addLore(String.format("  %s&6%s Class Access", hasLevel ? "&a&l✔ " : "", classManager.getDisplayName()));
                    }
                }

                if (rareReward != null) {
                    builder.addLore("");
                    builder.addLore("&7And also a Rare Reward!");
                    builder.addLore("  " + (hasLevel ? "&a&l✔ " : "") + rareReward.getName() + (rareReward.isComingSoon() ? " &cComing Soon" : ""));
                    builder.addSmartLore(rareReward.getInfo(), " &7&o");
                }
            }
            else {
                builder.addLore("  &aNo rewards yet!");
            }
        }

        builder.addLore("");

        if (type == IconType.NEXT_LEVEL) {
            if (specialReward) {
                builder.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            builder.addLore(String.format("&7Progress to Global %s - %s", nextLvl, String.format("&e%s&7/&e%s", currentExpScaled, totalExpForNextLvlScaled)));
            builder.addLore(buildBars(percentDoneBars) + String.format(" &e%s%%", String.format("%.1f", percentDone)));
        }

        return builder.hideFlags().build();

    }

    private String buildBars(double percent) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if ((int)percent > i) {
                builder.append(ChatColor.GREEN);
            }
            else {
                builder.append(ChatColor.RED);
            }
            builder.append('■');
        }
        return builder.toString();
    }

    private enum IconType {

        HAS_LEVEL(Material.LIME_STAINED_GLASS_PANE),
        NEXT_LEVEL(Material.YELLOW_STAINED_GLASS_PANE),
        LOCKED_LEVEL(Material.RED_STAINED_GLASS_PANE);

        private final Material material;

        IconType(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

    }

}
