package ru.hapyl.classesfight.feature.japangod;

import kz.hapyl.spigotutils.module.util.CollectionUtils;
import ru.hapyl.classesfight.feature.japangod.blessing.*;

public enum Blessings {

    HEALING(new HealingBlessing()),
    DAMAGE(new DamageBlessing()),
    SPEED(new SpeedBlessing()),
    DEATH(new DeathBlessing()),
    GENOCIDE(new GenocideBlessing()),
    STRENGTH(new StrengthBlessing()),
    PARANOIA(new ParanoiaBlessing()),
    JUMP(new JumpBoostBlessing()),
    SLOW(new SlownessBlessing()),
    ULT(new UltimateBlessing());

    private final Blessing blessing;

    Blessings(Blessing blessing) {
        this.blessing = blessing;
    }

    public static Blessings random() {
        return CollectionUtils.randomElement(values(), HEALING);
    }

    public Blessing getBlessing() {
        return blessing;
    }
}
