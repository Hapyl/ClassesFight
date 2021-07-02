package ru.hapyl.classesfight.feature;

import org.bukkit.event.entity.EntityDamageEvent;
import ru.hapyl.classesfight.utils.Validator;

/**
 * This is the class that makes all this damage system work, also custom damage!
 */
public enum EnumDamageCause {

    // *==* those are custom damages *==*
    CREEPER_EXPLOSION(true),
    NOVA_EXPLOSION(true),
    SHOCK_DART(true),
    BOOM_BOW_ULTIMATE(true),
    FIRE_MOLOTOV(true),
    FIRE_SPRAY(true),
    FROZEN_WEAPON(true),
    LEASHED(true),
    SOUL_WHISPER(true),
    TOXIN(true),

    // *==* there are vanilla ones, have to use them *==*

    // Have to consider entity_attack as custom damage for display porpoises
    ENTITY_ATTACK(true),
    PROJECTILE,
    FALL,
    FIRE,
    FIRE_TICK,
    LAVA,
    DROWNING,
    BLOCK_EXPLOSION,
    ENTITY_EXPLOSION,
    VOID,
    POISON,
    MAGIC,
    WITHER,
    FALLING_BLOCK,
    DRAGON_BREATH,
    CRAMMING,

    NONE, // this used as default return
    OTHER; // this used if there no other damage

    private final boolean customDamage;

    EnumDamageCause(boolean customDamage) {
        this.customDamage = customDamage;
    }

    EnumDamageCause() {
        this(false);
    }

    public boolean isCustomDamage() {
        return customDamage;
    }

    public static EnumDamageCause getFromCause(EntityDamageEvent.DamageCause cause) {
        final EnumDamageCause enumValue = Validator.getEnumValue(EnumDamageCause.class, cause.name());
        return enumValue == null ? EnumDamageCause.OTHER : enumValue;
    }

}
