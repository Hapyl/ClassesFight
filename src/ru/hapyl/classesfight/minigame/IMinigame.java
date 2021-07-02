package ru.hapyl.classesfight.minigame;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.rewards.ExtraReward;
import ru.hapyl.classesfight.minigame.rewards.MinigameReward;

public abstract class IMinigame extends TextureIcon {

    private final String name;
    private final long startCostCoins;
    private MinigameReward reward;

    private String afterLore;

    public IMinigame(String name, long startCostCoins, String texture, String lore) {
        this(name, startCostCoins, texture, lore, true);
    }

    public IMinigame(String name, long startCostCoins, String texture, String lore, boolean flag) {
        super(texture, name, lore, flag);
        this.name = name;
        this.startCostCoins = startCostCoins;
        this.reward = new MinigameReward();
    }

    public IMinigame(String name, long startCostCoins) {
        this(name, startCostCoins, "null", "&cPlease provide lore in the API!");
    }

    protected void setReward(MinigameReward reward) {
        this.reward = reward;
    }

    protected void setReward(int slot, ExtraReward extra) {
        this.reward.putReward(slot, extra);
    }

    protected void setPrefixLore(String s) {
        this.afterLore = s;
    }

    public String getAfterLore() {
        return afterLore == null ? "" : afterLore;
    }

    protected MinigameReward chainPut(int slot, ExtraReward extra) {
        this.reward.putReward(slot, extra);
        return this.reward;
    }

    public MinigameReward getReward() {
        return reward;

    }

    public abstract void newInstance(Player player, boolean debug);

    public void notYetImplemented(Player player) {
        Chat.sendMessage(player, "&cThis minigame is not yet implemented!");
    }

    public long getStartCostCoins() {
        return startCostCoins;
    }

    public String getName() {
        return name;
    }
}

