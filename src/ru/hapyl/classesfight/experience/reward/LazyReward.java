package ru.hapyl.classesfight.experience.reward;

import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class LazyReward implements Reward {

    private final int coins;
    private final int rubies;
    private RareReward rareReward;

    public LazyReward(int coins, int rubies) {
        this.coins = coins;
        this.rubies = rubies;
    }

    public LazyReward setRareReward(RareReward rareReward) {
        this.rareReward = rareReward;
        return this;
    }

    public LazyReward setRareReward(String name, String info, Consumer<Player> action, RewardType type) {
        return this.setRareReward(new RareReward(name, info, action, type));
    }

    public LazyReward setRareReward(String name, String info, Consumer<Player> grantAction, Consumer<Player> revokeAction, RewardType type) {
        return this.setRareReward(new RareReward(name, info, grantAction, revokeAction, type));
    }

    public LazyReward setRevokeAction(Consumer<Player> action) {
        this.rareReward.setRevokeAction(action);
        return this;
    }

    public LazyReward setPredicate(Predicate<Player> predicate) {
        this.rareReward.setPredicate(predicate);
        return this;
    }

    public boolean predicate(Player player) {
        return this.rareReward.predicate(player);
    }

    public LazyReward setComingSoon() {
        this.rareReward.setComingSoon(true);
        return this;
    }

    @Override
    public RareReward getRareReward() {
        return this.rareReward;
    }

    public boolean isComingSoon() {
        return this.rareReward.isComingSoon();
    }

    @Override
    public long coinsReward() {
        return this.coins;
    }

    @Override
    public long rubiesReward() {
        return this.rubies;
    }

}
