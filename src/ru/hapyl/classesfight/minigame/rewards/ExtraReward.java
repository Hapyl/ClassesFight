package ru.hapyl.classesfight.minigame.rewards;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.minigame.AbstractMinigame;

public class ExtraReward implements Reward {

    private final long coins;
    private final long rubies;
    private final long exp;
    private final RareReward rareReward;

    public ExtraReward() {
        this(0, 0, 0, null);
    }

    public ExtraReward(long coins) {
        this(coins, 0, 0, null);
    }

    public ExtraReward(long coins, long rubies) {
        this(coins, rubies, 0, null);
    }

    public ExtraReward(long coins, long rubies, long exp) {
        this(coins, rubies, exp, null);
    }

    public ExtraReward(long coins, long rubies, long exp, RareReward rare) {
        this.coins = coins;
        this.exp = exp;
        this.rubies = rubies;
        this.rareReward = rare;
    }

    public long getCoins() {
        return coins;
    }

    public long getRubies() {
        return rubies;
    }

    @Nullable
    public RareReward getRareReward() {
        return rareReward;
    }

    public long getExp() {
        return this.exp;
    }

    @Override
    public boolean hasAnyRewards() {
        return this.getCoins() > 0 || this.getExp() > 0 || this.getRubies() > 0 || this.getRareReward() != null;
    }

    @Override
    public void grantAll(Player player, int upTo, AbstractMinigame minigame) {

    }
}
