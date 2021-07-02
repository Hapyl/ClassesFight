package ru.hapyl.classesfight.experience.reward;

import ru.hapyl.classesfight.cosmetics.EnumEffect;

public class RewardStorage {

    public static final RareReward COMPLETE_ULTRASEQUENCER = new RareReward("Rainbow", "Rainbow for everyone!", EnumEffect.RAINBOW::grantItem, RewardType.COSMETIC);

}
