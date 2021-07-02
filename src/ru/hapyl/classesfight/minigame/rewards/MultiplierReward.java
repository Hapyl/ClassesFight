package ru.hapyl.classesfight.minigame.rewards;

import ru.hapyl.classesfight.experience.reward.Reward;

public class MultiplierReward implements Reward {

    private final float multiplierFactor;

    public MultiplierReward(float factor) {
        this.multiplierFactor = factor;
    }

    public long multiply(long in) {
        return Math.max(in, (long)(in * this.multiplierFactor));
    }

    @Override
    public long coinsReward() {
        return 0;
    }
}
