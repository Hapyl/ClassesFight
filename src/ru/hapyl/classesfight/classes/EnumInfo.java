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

public enum EnumInfo {

	// none is 20 to prevent
	NONE(-2, 20),
	VERY_LOW(-1, 20),
	LOW(0, 30),
	MEDIUM(1, 40),
	HIGH(2, 50),
	VERY_HIGH(3, 60);

	private final int index;
	private final double value;

	public int index() {
		return this.index;
	}

	EnumInfo(int index) {
		this(index, 0.0d);
	}

	EnumInfo(int index, double value) {
		this.index = index;
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
