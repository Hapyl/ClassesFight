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

package ru.hapyl.classesfight.classes.iclass.extra;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.concurrent.ThreadLocalRandom;

public class RPSInfo {

	private final RPSMaterial material;
	private int currentHit;

	public RPSInfo(RPSMaterial material) {
		this.material = material;
		this.currentHit = 3;
	}

	public void decrementHit() {
		--currentHit;
	}

	public int getCurrentHit() {
		return currentHit;
	}

	public RPSMaterial getMaterial() {
		return material;
	}

	public enum RPSMaterial {

		ROCK(Sound.BLOCK_ANVIL_LAND, 1.75f),
		PAPER(Sound.ENTITY_HORSE_SADDLE, 1.25f),
		SCISSORS(Sound.ITEM_SHIELD_BREAK, 1.25f);

		private Sound soundGroup;
		private float pitch;

		RPSMaterial() {

		}

		RPSMaterial(Sound sound, float pitch) {
			this.soundGroup = sound;
			this.pitch = pitch;
		}

		public static RPSMaterial random() {
			return RPSMaterial.values()[ThreadLocalRandom.current().nextInt(values().length)];
		}

		public RPSMaterial getWeak() {
			return this == ROCK ? SCISSORS : this == PAPER ? ROCK : PAPER;
		}

		public RPSMaterial getOpposite() {
			return this == ROCK ? PAPER : this == PAPER ? SCISSORS : ROCK;
		}

		public void playSound(Player player) {
			if (this.soundGroup != null) {
				OldPlayerLib.playSound(player, this.soundGroup, this.pitch);
			}
		}

		public ItemStack toItemStack() {
			return new ItemStack(this == ROCK ? Material.COBBLESTONE : this == PAPER ? Material.PAPER : Material.SHEARS);
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("RPSInfo{");
		sb.append("material=").append(material);
		sb.append(", currentHit=").append(currentHit);
		sb.append('}');
		return sb.toString();
	}
}
