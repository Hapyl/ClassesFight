/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

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
