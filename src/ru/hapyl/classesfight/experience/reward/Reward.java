package ru.hapyl.classesfight.experience.reward;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public interface Reward {

    default long rubiesReward() {
        return 0;
    }

    long coinsReward();

    @Nullable
    default RareReward getRareReward() {
        return null;
    }

}
