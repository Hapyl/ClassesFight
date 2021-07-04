package ru.hapyl.classesfight.feature;

public class DeathMessage {

	private static final String PLAYER_PLACEHOLDER = "{player}";

	private final String message;
	private final String damagerSuffix;

	public DeathMessage(String message, String damagerSuffix) {
		this.message = message;
		this.damagerSuffix = message.contains(PLAYER_PLACEHOLDER) ? "" : damagerSuffix;
	}

	public String getMessage() {
		return message;
	}

	public String formatMessage(String player) {
		return getMessage().replace(PLAYER_PLACEHOLDER, player);
	}

	public boolean hasSuffix() {
		return !this.damagerSuffix.isBlank();
	}

	public String getDamagerSuffix() {
		return damagerSuffix;
	}

	public static DeathMessage of(String message, String suffix) {
		return new DeathMessage(message, suffix);
	}

}
