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

package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Material;
import ru.hapyl.classesfight.cosmetics.BlockContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class RainbowContrail extends BlockContrail {

	public RainbowContrail() {
		super("Rainbow", "There are all the colors!__&8Or are there?", -1, Material.RED_STAINED_GLASS, ShopItemRarity.LEGENDARY,
				Material.RED_STAINED_GLASS, Material.ORANGE_STAINED_GLASS,
				Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS,
				Material.LIGHT_BLUE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.BLACK_STAINED_GLASS);
	}
}
