package ru.hapyl.classesfight.minigame.rewards;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.AbstractMinigame;

public interface Reward {

    boolean hasAnyRewards();

    void grantAll(Player player, int upTo, AbstractMinigame minigame);

}
