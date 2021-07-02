package ru.hapyl.classesfight.gameeffect;

public enum GameEffectType {

	TEST(new Test()),

	// positives
	VULNERABLE(new Vulnerability(), "While affected by Vulnerability, player will take double damage from any sources.", true),
	FALL_DAMAGE_RESISTANCE(new FallDamageResistance(), "While affected by Fall Damage Resistance, player will not take fall damage.", true),
	RESISTANCE(new Resistance(), "While affected by Resistance, player will have a shield that absorbs all damage taken.", true),
	INVISIBILITY(new Invisibility(), "While affected by Invisibility, player will not be visible, nor can deal and take damage.", true),
	STRENGTH(new Strength(), "While affected by Strength, player will deal double the damage.", true),

	// negatives
	LOCK_DOWN(new Lockdown(), "While affected by Lockdown, player will be detained from moving, using abilities and switching slots."),
	PARANOIA(new Paranoia(), "While affected by Paranoia, player will have a limit vision and will hear decoy sounds."),
	GOLDEN_CURSE(new GoldenCurse(), "While affected by Golden Curse, player will drop coins (not their coins) upon getting hit."),
	WEAKNESS(new Weakness(), "While affected by Weakness, player will do twice less damage."),
	BURNING(new Burning(), "While affected by Burning, player will periodically take fire damage."),
	DROWNING(new Drowning(), "While affected by Drowning, player will periodically take water damage and will be slowed."),
	CORROSION(new Corrosion(), "While affected by Corrosion, player will be slowed and their vision is disturbed."),
	STUN(new Stun(), "While stunned, player cannot move nor attack other players."),
	AMNESIA(new Amnesia(), "While affected by Amnesia, player's controls are randomized and nausea is applied."),

	//
	IMMOVABLE(new Immovable(), "While affected by Immovable, player will not take knockback."),
	SHIELDED(new Shield(), "While shielded, your shield will absorb set amount of hits before breaking."),
	BREAKABLE_SHIELD(new HealthShield(), "While shielded, your shield will absorb set amount of damage before breaking.");

	private final boolean positive;
	private final GameEffect effect;
	private final String aboutEffect;

	GameEffectType(GameEffect effect) {
		this(effect, effect.getEffectName(), false);
	}

	GameEffectType(GameEffect effect, String aboutEffect, boolean isPositive) {
		this.effect = effect;
		this.aboutEffect = aboutEffect;
		this.positive = isPositive;
	}

	GameEffectType(GameEffect effect, String aboutEffect) {
		this(effect, aboutEffect, false);
	}

	public boolean isPositive() {
		return positive;
	}

	public GameEffect getEffect() {
		return this.effect;
	}

	public String getAboutEffect() {
		return aboutEffect;
	}
}
