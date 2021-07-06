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

package ru.hapyl.classesfight.minigame.games;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class ChronoButton {

	private final String name;
	private final Material display;
	private final Material pressed;
	private final float pitch;
	private final int[] slots;

	public ChronoButton(String name, Material display, Material pressed, int... slots) {
		this(name, display, pressed, 1.0f, slots);
	}

	public ChronoButton(String name, Material display, Material pressed, float pitch, int... slots) {
		this.name = name;
		this.display = display;
		this.pressed = pressed;
		this.pitch = pitch;
		this.slots = slots;
	}

	public String getName() {
		return name;
	}

	public Material getDisplay() {
		return display;
	}

	public Material getPressed() {
		return pressed;
	}

	public float getPitch() {
		return pitch;
	}

	public int[] getSlots() {
		return slots;
	}

	public boolean hasSlot(int slot) {
		for (int i : this.slots) {
			if (i == slot) {
				return true;
			}
		}
		return false;
	}

	public void playSound(Player player) {
		OldPlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, this.pitch);
	}

	public ItemStack toItem() {
		return new ItemBuilder(this.getDisplay()).setName(this.getName()).toItemStack();
	}

	public ItemStack toItemPressed() {
		return new ItemBuilder(this.getPressed()).setName(this.getName()).glow().toItemStack();
	}

}
