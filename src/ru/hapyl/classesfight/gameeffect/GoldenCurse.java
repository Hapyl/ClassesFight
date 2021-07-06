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

package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.Random;

public class GoldenCurse implements GameEffect {

	private final ItemStack[] AVAILABLE_ITEMS = new ItemStack[]{
			new ItemStack(Material.GOLDEN_SWORD),
			new ItemStack(Material.GOLDEN_HELMET),
			new ItemStack(Material.GOLDEN_CHESTPLATE),
			new ItemStack(Material.GOLDEN_LEGGINGS),
			new ItemStack(Material.GOLDEN_BOOTS),
			new ItemStack(Material.GOLDEN_APPLE),
			new ItemStack(Material.GOLDEN_AXE),
			new ItemStack(Material.GOLDEN_CARROT),
			new ItemStack(Material.GOLDEN_HOE),
			new ItemStack(Material.GOLDEN_SHOVEL),
			new ItemStack(Material.GOLDEN_PICKAXE),
			new ItemStack(Material.GOLDEN_HORSE_ARMOR),
			new ItemStack(Material.GOLD_BLOCK),
			new ItemStack(Material.GOLD_INGOT),
			new ItemStack(Material.GOLD_NUGGET)
	};

	@Override
	public void onEffectStart(Player player, Object... extra) {
		OldPlayerLib.addPotionEffect(player, PotionEffectType.SLOW, Integer.MAX_VALUE, 1);
	}

	@Override
	public void onEffectStop(Player player) {
		OldPlayerLib.removePotionEffect(player, PotionEffectType.SLOW);
	}

	@Override
	public long getTaskDelay() {
		return 2;
	}

	@Override
	public void affectTask(Player player) {
		final Item item = player.getWorld()
				.dropItemNaturally(player.getLocation().clone().subtract(0.5, new Random().nextInt(1), 0.5), AVAILABLE_ITEMS[new Random().nextInt(AVAILABLE_ITEMS.length)]);
		item.setPickupDelay(100);
		item.setTicksLived(5990);
	}

	@Override
	public String getEffectName() {
		return "Golden Curse";
	}
}
