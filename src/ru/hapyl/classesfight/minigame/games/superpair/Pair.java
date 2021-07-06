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

package ru.hapyl.classesfight.minigame.games.superpair;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.experience.reward.RarePackageReward;
import ru.hapyl.classesfight.experience.reward.RareReward;

public class Pair {

    // int represent slots
    private final int firstPair;
    private final int secondPair;

    // have to keep track of what pair added
    private long coins, exp, rubies;
    private RareReward rare;

    private ItemStack stack;
    private PowerUpType powerUp;

    public Pair(int firstPair, int secondPair) {
        this.firstPair = firstPair;
        this.secondPair = secondPair;
    }

    public Pair(int[] slots) {
        Validate.isTrue(slots.length == 2, "slots length must be 2, not " + slots.length);
        this.firstPair = slots[0];
        this.secondPair = slots[1];
    }

    public Pair(int onlyPair) {
        this(onlyPair, -1);
    }

    public Pair addReward(long coins, long rubies, long exp) {
        this.coins = coins;
        this.rubies = rubies;
        this.exp = exp;
        return this;
    }

    public Pair setPowerUp(PowerUpType type) {
        this.powerUp = type;
        return this;
    }

    public boolean isPowerUp() {
        return powerUp != null;
    }

    public PowerUpType getPowerUp() {
        return powerUp;
    }

    public enum PowerUpType {
        ADDITIONAL_CLICKS,
        ADDITIONAL_TIME,
        REVEAL_NEXT_CLICK
    }

    public ItemStack getItemOrCreate() {
        if (this.stack == null) {
            if (this.getRare() != null && this.getRare() instanceof RarePackageReward) {
                final RarePackageReward pack = (RarePackageReward)this.getRare();
                return new ItemBuilder(pack.getMaterial())
                        .setAmount(pack.getAmount())
                        .setName("&a&l" + pack.getName() + " Package Reward")
                        .addLore("&7This package contains:")
                        .addLore(" &a" + pack.getCoins() + " Coins")
                        .addLore(" &a" + pack.getExp() + " Experience")
                        .addLore(" &a" + pack.getRuby() + " Rubies")
                        .toItemStack();
            }
            PairType type = this.coins > this.exp ? PairType.COINS : this.exp >= this.rubies ? PairType.EXP : this.rubies > 0 ? PairType.RUBIES : PairType.OTHER;
            this.stack = new ItemBuilder(type.getMaterial())
                    .setName(ChatColor.GREEN + (Chat.capitalize(type) + " Pair"))
                    .setAmount(getAmount(type))
                    .addLoreIf("&7+%s Coins", coins > 0, coins)
                    .addLoreIf("&7+%s Exp", exp > 0, exp)
                    .addLoreIf("&7+%s %s", rubies > 0, rubies, rubies == 1 ? "Ruby" : "Rubies").toItemStack();
        }
        return this.stack;
    }

    private int getAmount(PairType type) {
        long l = type == PairType.COINS ? this.coins / 100 : type == PairType.RUBIES ? this.rubies : type == PairType.EXP ? this.exp / 100 : 1;
        return (int)Math.min(64, Math.max(1, l));
    }

    public boolean contains(int slot) {
        return this.firstPair == slot || this.secondPair == slot;
    }

    public long getCoins() {
        return coins;
    }

    public long getExp() {
        return exp;
    }

    public long getRubies() {
        return rubies;
    }

    public RareReward getRare() {
        return rare;
    }

    public final Pair addRareReward(RareReward rare) {
        this.rare = rare;
        return this;
    }

    public int getFirstPair() {
        return firstPair;
    }

    public int getSecondPair() {
        return secondPair;
    }

    @Override
    public String toString() {
        return "Pair{" + "firstPair=" + firstPair +
                ", secondPair=" + secondPair +
                ", coins=" + coins +
                ", exp=" + exp +
                ", rubies=" + rubies +
                ", rare=" + rare +
                ", stack=" + stack +
                '}';
    }
}
