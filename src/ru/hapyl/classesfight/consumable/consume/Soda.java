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

package ru.hapyl.classesfight.consumable.consume;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.consumable.Consumable;

public class Soda extends Consumable {

	public Soda() {
		super("Soda", "Instantly drink a can of Soda and gain &bSpeed &7for 2 seconds.", 200, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNiMDFmYjJmNmJhNDdjOWQ3NjM4NDkxZjM3Y2Q4NTgyYTkzNzczMTE4NmRmNGQxZWNjZDU5YjY1YmYzNyJ9fX0=");
		this.addSpawnLocation(GameMap.ARENA, -102, 5, -104);
		this.addSpawnLocation(GameMap.GREENHOUSE, 7, 8, 66);
		this.addSpawnLocation(GameMap.NETHER, -155, 4, -58);
		this.addSpawnLocation(GameMap.JAPAN, 112, 17, -134);
		this.addSpawnLocation(GameMap.RAILWAY, 112, 9, 13);
		this.addSpawnLocation(GameMap.SKY, 1040, 26, 1006);
	}

	@Override
	public void consume(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
	}
}
