package ru.hapyl.classesfight.experience.reward;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RareReward {

    private final String name;
    private final String info;
    private final Consumer<Player> consumer;
    private Consumer<Player> revokeConsumer;
    private Predicate<Player> predicate;
    private RewardType type;
    private boolean comingSoon;

    public RareReward(String name, String info) {
        this(name, info, null, null, RewardType.UNKNOWN);
    }

    public RareReward(String name, String info, Consumer<Player> consumer, RewardType type) {
        this(name, info, consumer, null, type);
    }

    public RareReward(String name, String info, Consumer<Player> consumer, Consumer<Player> revokeConsumer, RewardType type) {
        this.name = name;
        this.info = info;
        this.consumer = consumer;
        this.revokeConsumer = revokeConsumer;
        this.type = type;
    }

    public RareReward setRevokeAction(Consumer<Player> action) {
        this.revokeConsumer = action;
        return this;
    }

    public RareReward setPredicate(Predicate<Player> predicate) {
        this.predicate = predicate;
        return this;
    }

    // boolean as if player has it
    public boolean predicate(Player player) {
        if (this.predicate == null) {
            return false;
        }
        else {
            return this.predicate.test(player);
        }
    }

    public void setType(RewardType type) {
        this.type = type;
    }

    public RewardType getType() {
        return this.type;
    }

    public boolean isComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public RareReward(String name) {
        this(name, "");
    }

    public String getName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + this.type.name().toUpperCase() + ChatColor.GOLD + " " + this.name;
    }

    public String getInfo() {
        return this.info;
    }

    public void grantReward(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

    public void revokeReward(Player player) {
        if (revokeConsumer != null) {
            revokeConsumer.accept(player);
        }
    }

}
