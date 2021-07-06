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

package ru.hapyl.classesfight.menu.collectibles;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.EnumEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemShop {

	private static final Map<UUID, ItemShop> itemShops = new HashMap<>();

	private int reRollCount;
	private EnumEffect[] items;

	private ItemShop(Player player) {
		this.generateItems();
		itemShops.put(player.getUniqueId(), this);
	}

	public int getReRollCount() {
		return reRollCount;
	}

	public void incrementReRollCount() {
		++reRollCount;
	}

	public void generateItems() {
		this.items = new EnumEffect[5];
		for (int i = 0; i < this.items.length; i++) {
			this.items[i] = randomEffect();
		}
	}

	public EnumEffect[] getItems() {
		return items;
	}

	private EnumEffect randomEffect() {
		EnumEffect effect = EnumEffect.values()[ThreadLocalRandom.current().nextInt(EnumEffect.values().length)];
		return (hasEffect(effect) || !effect.availableInShop()) ? randomEffect() : effect;
	}

	public boolean hasEffect(EnumEffect effect) {
		for (EnumEffect item : this.items) {
			if (effect == item) {
				return true;
			}
		}
		return false;
	}

	public static ItemShop getShop(Player player) {
		final ItemShop shop = itemShops.getOrDefault(player.getUniqueId(), null);
		return shop == null ? new ItemShop(player) : shop;
	}

}
