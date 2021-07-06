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

package ru.hapyl.classesfight.translate;

import ru.hapyl.classesfight.utils.ConfigFile;

public class Locale extends ConfigFile {

	private final String displayName;
	private final String localeName;

	public Locale(String localeName, String display) {
		super(localeName.toLowerCase() + "");
		this.displayName = display;
		this.localeName = localeName.toLowerCase();
	}

	public final String getByKeyOr(String key, String or) {
		return this.getConfig().getString(key, or);
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getLocaleName() {
		return localeName;
	}
}
