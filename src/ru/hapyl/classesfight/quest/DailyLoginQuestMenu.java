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

package ru.hapyl.classesfight.quest;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.PackagedReward;

public class DailyLoginQuestMenu extends PlayerGUI {

    private final DailyLoginQuestReward daily;

    public DailyLoginQuestMenu(Player player, GUI from) {
        super(player, "Daily Rewards", 5);
        this.daily = ClassesFight.getSingleton().getDailyRewards();
        this.setArrowBack(from);
        this.updateMenu();
    }

    private void updateMenu() {

        final long nextDailyIn = daily.nextDailyIn(getPlayer());
        final long currentDay = daily.getCurrentDay(getPlayer());
        final long currentDayModulo = currentDay % 21;

        for (int i = 0, slot = 10; i < 21; i++, slot += (slot % 9) == 7 ? 3 : 1) {
            final PackagedReward currentReward = daily.getCurrentReward(i);
            final boolean hasClaimed = (i < currentDayModulo || (i == currentDayModulo && nextDailyIn < 0));

            final ItemBuilder builder = new ItemBuilder(hasClaimed ? Material.MINECART : ((i % 7 == 6) ? Material.CHEST : Material.CHEST_MINECART))
                    .setName("&aDaily Reward &l%s", i + 1)
                    .addLore(currentReward.getRewardInfoLore())
                    .addLore()
                    .setAmount(i + 1)
                    .predicate(i == currentDayModulo, ItemBuilder::glow);

            if (hasClaimed) {
                builder.addLore("&aRewards claimed!");
            }

            if (i == currentDayModulo) {
                if (nextDailyIn <= 0) {
                    builder.addLore("&eClick to claim!");
                }
                else {
                    builder.addLore("&eCan claim in:");
                    builder.addLore(" &7" + daily.nextDailyInString(getPlayer()));
                }
            }

            if (i > currentDayModulo) {
                builder.addLore("&cNot available to claim!");
            }

            this.setItem(slot, builder.toItemStack());
            if (i == currentDayModulo && nextDailyIn <= 0) {
                this.setClick(slot, (player) -> {
                    daily.claimRewards(player);
                    this.updateMenu();
                });
            }
            else {
                this.setClick(slot, player -> PlayerLib.playSound(player, Sound.BLOCK_LAVA_POP, 0.0f));
            }
        }

        this.openInventory();
    }

}
