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

package ru.hapyl.classesfight.utils.pn;

import ru.hapyl.classesfight.classes.ClassManager;

public class ClassUpdatePatch extends Patch {

	private final ClassManager clazz;

	public ClassUpdatePatch(ClassManager clazz) {
		super(Type.CLASS_UPDATE);
		this.clazz = clazz;
	}

	public ClassManager getClazz() {
		return clazz;
	}

}
