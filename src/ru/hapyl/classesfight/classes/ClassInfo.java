package ru.hapyl.classesfight.classes;

/**
 * Этот класс отвечает за информацию в меню выбора класса.
 */

public final class ClassInfo {

	private final String lore;
	private final String aboutKit;
	private final String ultimate;
	private final String ultimateLore;

	public ClassInfo(String lore, String aboutKit, String ultimate, String ultimateLore) {
		this.lore = lore;
		this.aboutKit = aboutKit.isEmpty() ? "" : aboutKit.startsWith("__") ? aboutKit : "__" + aboutKit;
		this.ultimate = ultimate;
		this.ultimateLore = ultimateLore;
	}

	public ClassInfo(String lore, String ultimateName, String ultimateLore) {
		this(lore, "", ultimateName, ultimateLore);
	}

	public String getLore() {
		return lore;
	}

	public String getAboutKit() {
		return aboutKit;
	}

	public String getUltimate() {
		return ultimate;
	}

	public String getUltimateLore() {
		return ultimateLore;
	}

}
