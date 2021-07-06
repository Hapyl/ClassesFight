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

package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public enum ElementType {

	// nothing or idk slow
	STONE(8.0d, 30, player -> PlayerLib.addEffect(player, PotionEffectType.SLOW, 20, 1)),

	// idk yet
	WOOD(4.0d, 15, player -> PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 10, 1)),

	// flights up
	WOOL(1.0d, 10, player -> PlayerLib.addEffect(player, PotionEffectType.LEVITATION, 10, 1)),

	NULL(-1, 0, null);

	private final double damage;
	private final int cd;
	private final Action<Player> effect;

	ElementType(double damage, int cd, Action<Player> effect) {
		this.damage = damage;
		this.cd = cd;
		this.effect = effect;
	}

	public int getCd() {
		return cd;
	}

	public double getDamage() {
		return damage;
	}

	@NULLABLE
	public Action<Player> getEffect() {
		return effect;
	}

	public static ElementType getElementOf(Material material) {
		if (!material.isBlock()) {
			throw new IllegalArgumentException("material is not a block!");
		}

		// don't allow wall blocks except player head
		if (material.name().contains("WALL") && material != Material.PLAYER_HEAD) {
			return NULL;
		}

		final float magicNumber = material.getBlastResistance();
		if (magicNumber >= 5.0d && magicNumber < 10.0d) {
			return STONE;
		}

		if (magicNumber <= 3.0d && magicNumber > 1.0d) {
			return WOOD;
		}

		if (magicNumber > 0.0d && magicNumber <= 1.0d) {
			return WOOL;
		}

		return NULL;

	}

}
