package ru.hapyl.classesfight.minigame.rewards;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.minigame.AbstractMinigame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MutableReward implements Reward {

    private long coins = 0;
    private long rubies = 0;
    private long exp = 0;
    private Set<RareReward> rewards;

    public MutableReward() {
        this.rewards = new HashSet<>();
    }

    public void addCoins(long amount) {
        this.coins = +amount;
    }

    public void addRubies(long amount) {
        this.rubies += amount;
    }

    public void addExp(long amount) {
        this.exp += amount;
    }

    public void addRareReward(RareReward reward) {
        if (reward == null) {
            return;
        }
        this.rewards.add(reward);
    }

    public long getCoins() {
        return coins;
    }

    public long getRubies() {
        return rubies;
    }

    public long getExp() {
        return exp;
    }

    public Set<RareReward> getRewards() {
        return rewards;
    }

    public void setRewards(Set<RareReward> rewards) {
        this.rewards = rewards;
    }

    @Override
    public boolean hasAnyRewards() {
        return coins != 0 || rubies != 0 || exp != 0 || rewards != null;
    }

    @Override
    public void grantAll(Player player, int ignored, AbstractMinigame minigame) {
        if (!hasAnyRewards()) {
            return;
        }
        final Database database = Database.getDatabase(player);
        final StringBuilder builder = new StringBuilder();
        if (coins > 0) {
            builder.append(String.format("&6+%s Coins", coins));
            database.getCoinsEntry().grantCoins(coins, false, null);
        }
        if (rubies > 0) {
            builder.append(String.format("&6, +%s Rub%s", rubies, rubies == 1 ? "y" : "ies"));
            database.getRubyEntry().grantRubies(rubies, null);
        }
        if (exp > 0) {
            builder.append(String.format("&6, +%s Exp", exp));
            Experience.getInstance().grantExp(player, exp);
        }
        if (!this.rewards.isEmpty()) {
            for (RareReward rareReward : this.rewards) {
                builder.append(", &6").append(rareReward.getName());
                rareReward.grantReward(player);
            }
        }
        minigame.sendMessage(builder.toString());
    }

}
