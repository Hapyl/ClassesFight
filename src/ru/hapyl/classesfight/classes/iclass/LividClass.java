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

package ru.hapyl.classesfight.classes.iclass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassInfo;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public class LividClass extends IClass {

	/**
	 * --[ 6 Faces ]--
	 *
	 * 1. Sneaky (Invisibility, Low Damage)
	 * 2. Enraged (Strong, High Damage)
	 * 3. Healthy (A lot of health, Low Damage)
	 * 4.
	 * 5.
	 * 6.
	 *
	 */

	public LividClass() {
		super("Masked", Material.ROTTEN_FLESH, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.VERY_HIGH, ClassRole.STRATEGIST,
				new ClassInfo("", "", ""),
				new ClassUltimate("", Integer.MAX_VALUE));
	}

	private static final ItemStack WEAPON = new CFItemBuilder(Material.IRON_SWORD)
			.setName("&aWeapon")
			.build();


	@Override
	public void useUltimate(Player player) {

	}
}
