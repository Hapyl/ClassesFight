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

package ru.hapyl.classesfight.menu.minigame;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.menu.GameMenuGUI;
import ru.hapyl.classesfight.minigame.IMinigame;
import ru.hapyl.classesfight.minigame.Minigame;
import ru.hapyl.classesfight.minigame.games.superpair.PairType;
import ru.hapyl.classesfight.minigame.rewards.ExtraReward;
import ru.hapyl.classesfight.minigame.rewards.MinigameReward;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinigameMenu extends PlayerGUI {

    public MinigameMenu(Player player) {
        super(player, "Minigames", 5);
        this.updateMenu();
        this.openInventory();
    }

    private void updateMenu() {
        final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").toItemStack();
        this.fillItem(0, 8, blackBar);
        this.fillItem(this.getSize() - 9, this.getSize() - 1, blackBar);

        for (int i = 0, slot = 19; i < Minigame.values().length; ++i, slot += 2) {
            createIcon(slot, Minigame.values()[i]);
        }

        this.setArrowBack("Main Menu", GameMenuGUI::new);
        this.openInventory();
    }

    private void createIcon(int slot, Minigame mini) {
        Validate.notNull(mini);
        final IMinigame game = mini.getGame();
        final ItemBuilder item = game.getBuilder();

        item.setName(item.getName() + " &c&lALPHA");
        final MinigameReward reward = game.getReward();
        boolean couldNotFitRewards = false;

        if (reward != null && reward.hasAnyRewards()) {
            item.addLore().addLore("Rewards");
            final Map<Integer, ExtraReward> mapped = reward.getRewards();
            if (mapped.size() > 5) {
                couldNotFitRewards = true;
                item.addLore(" &aRight-Click to view rewards!");
            }
            else {
                item.addLore(" &eRound Reached  &8-  &aReward");
                // #1 -> Reward ➣➢
                mapped.forEach((lvl, extra) -> item.addLore(Chat.bformat(" #&e{}  &8{}  &a{}", lvl, (lvl % 2 == 1) ? "➣" : "➢", wrap(extra))));
            }
        }

        // after lore that used for one single minigame only :(
        if (!game.getAfterLore().isEmpty()) {
            item.addLore().addLore(game.getAfterLore());
            // I know I know
            if (mini == Minigame.SUPER_PAIRS) {
                item.addLore();
                item.addLore("&7Possible Rewards Per Pair:");
                item.addLore(" &e%s-%s Coins", PairType.COINS.getMinValue(), PairType.COINS.getMaxValue());
                item.addLore(" &e%s-%s Experience", PairType.EXP.getMinValue(), PairType.EXP.getMaxValue());
                item.addLore(" &e%s-%s Rubies", PairType.RUBIES.getMinValue(), PairType.RUBIES.getMaxValue());
            }
        }

        item.addLore().addLore("Starting Cost").addLore(" &e" + String.format("%,d", game.getStartCostCoins()) + " Coins");
        final boolean hasCoins = Database.getCoins(this.getPlayer()).hasCoins(game.getStartCostCoins());
        item.addLore().addLore(hasCoins ? "&eClick to play!" : "&cNot enough coins :(");

        final ItemStack itemStack = item.toItemStack();

        this.setItem(slot, itemStack);
        this.setClick(slot, player -> {
            if (hasCoins) {
                mini.newInstance(this.getPlayer(), false);
                Database.getCoins(this.getPlayer()).removeCoins(game.getStartCostCoins());
            }
            else {
                Chat.sendMessage(this.getPlayer(), "&cNot enough coins :(");
                GameUtils.villagerNo(this.getPlayer());
            }
        }, ClickType.LEFT, ClickType.SHIFT_LEFT);

        if (couldNotFitRewards) {
            this.setClick(slot, player -> buildRewardMenu(mini), ClickType.RIGHT, ClickType.SHIFT_RIGHT);
        }

    }

    private void buildRewardMenu(Minigame minigame) {

        final PlayerGUI menu = new PlayerGUI(getPlayer(), minigame.getName() + " Rewards", 5);

        final MinigameReward reward = minigame.getGame().getReward();

        long coins = 0;
        long rubies = 0;
        long exp = 0;

        int slot = 9;
        for (Integer integer : reward.getRewards().keySet()) {
            final ExtraReward extra = reward.getAtOr(integer, null);
            if (extra == null) {
                menu.setItem(slot, new ItemBuilder(Material.BEDROCK).setAmount(integer).setName("&cNo Reward!").toItemStack());
                continue;
            }

            coins += extra.getCoins();
            rubies += extra.getRubies();
            exp += extra.getExp();

            menu.setItem(slot, buildRewardItem(coins, rubies, exp, extra.getRareReward(), integer));
            ++slot;
        }

        menu.setItem(40, new ItemBuilder(Material.ARROW).setName("&aGo Back").setLore("To Minigames").toItemStack());
        menu.setClick(40, this::openInventory);

        menu.openInventory();

    }

    private ItemStack buildRewardItem(long coins, long rubies, long exp, RareReward rare, int round) {
        final List<String> lore = new ArrayList<>();
        Material material = null;
        if (coins > 0) {
            lore.add(String.format("&7+%s Coins", coins));
            if (rubies < 5) {
                material = coins > 600 ? Material.YELLOW_GLAZED_TERRACOTTA : Material.SUNFLOWER;
            }
        }
        if (rubies > 0) {
            lore.add(String.format("&7+%s %s", rubies, rubies == 1 ? "Ruby" : "Rubies"));
        }
        if (exp > 0) {
            lore.add(String.format("&7+%s Experience", exp));
            if (exp >= 500) {
                material = Material.EMERALD_BLOCK;
            }
            else if (coins > 600) {
                material = Material.EMERALD;
            }
        }

        if (rare != null) {
            lore.add("");
            lore.add("&7And also a &b&lrare &7reward!");
            lore.add(String.format(" &a%s", rare.getName()));
        }

        final String roundAndPronoun = round + GameUtils.getNumberPronoun(round);
        final ItemBuilder builder = new ItemBuilder(material == null ? Material.CHEST : material)
                .setName("&aReward for completing " + roundAndPronoun + " round")
                .addSmartLore(String.format("You must complete %s round to get this reward, not reach it!", roundAndPronoun), "&8")
                .addLore();

        for (String s : lore) {
            builder.addLore(s);
        }

        builder.addLore().addSmartLore("&cNOTE: Using hints reduces rewards.");

        if (round > 1) {
            builder.addLore().addLore("&aIncluding rewards from previous rounds.");
        }

        return builder.setAmount(round).toItemStack();
    }

    private String wrap(ExtraReward reward) {
        final long coins = reward.getCoins();
        final long rubies = reward.getRubies();
        final long exp = reward.getExp();
        final RareReward rare = reward.getRareReward();
        final StringBuilder builder = new StringBuilder();
        if (coins > 0) {
            builder.append(coins).append(" Coins");
        }
        if (rubies > 0) {
            builder.append(", ").append(rubies).append(" ").append(rubies == 1 ? "Ruby" : "Rubies");
        }
        if (exp > 0) {
            builder.append(", ").append(exp).append(" Exp");
        }
        if (rare != null) {
            builder.append(", ").append(rare.getName());
        }
        return builder.toString();
    }

}
