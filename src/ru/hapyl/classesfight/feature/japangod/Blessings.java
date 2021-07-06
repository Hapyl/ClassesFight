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
