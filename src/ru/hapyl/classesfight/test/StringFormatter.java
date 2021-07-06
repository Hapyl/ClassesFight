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

package ru.hapyl.classesfight.test;

public class StringFormatter {

	private final String string;

	public StringFormatter(String string) {
		this.string = string;
	}

	public String format() {
		return this.format0();
	}

	private strictfp String format0() {
		final StringBuilder builder = new StringBuilder();
		final String[] splits = this.string.split(" ");

		for (String value : splits) {
			String str = value;
			for (EnumStringFormatter esp : EnumStringFormatter.values()) {
				str = esp.checkAndFormat(value);
			}
			builder.append(str).append(" ");
		}
		return builder.toString().trim();
	}

}
