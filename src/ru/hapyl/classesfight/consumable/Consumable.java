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

package ru.hapyl.classesfight.consumable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class Consumable {

	private final String lore;
	private final String name;
	private final long price;
	private final ItemStack itemStack;
	private final Set<ConsumableLocation> spawnLocations;

	public Consumable(String name, String lore, long price, String headTexture) {
		this.name = name;
		this.lore = lore;
		this.price = price;
		this.headTexture = headTexture;
		this.spawnLocations = new HashSet<>();
		this.itemStack = createItemStack();
	}

	public void addSpawnLocation(ConsumableLocation location) {
		this.spawnLocations.add(location);
	}

	public void addSpawnLocation(GameMap map, int x, int y, int z) {
		this.addSpawnLocation(new ConsumableLocation(map, x, y, z));
	}

	public Set<ConsumableLocation> getSpawnLocations() {
		return spawnLocations;
	}

	private ItemStack createItemStack() {
		return new CFItemBuilder(Material.PLAYER_HEAD, "consumable_" + this.name.toLowerCase())
				.setHeadTexture(this.headTexture)
				.setName(String.format("&a%s &e&lCLICK", this.name))
				.setSmartLore(this.lore)
				.withCooldown(20, null, "&cCannot consume that yet!")
				.addClickEvent(player -> {
					GameUtils.removeHeldItem(player, 1);
					this.consume(player);
				}).build();
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public abstract void consume(Player player);

	public String getName() {
		return name;
	}

	public String getLore() {
		return lore;
	}

	public long getPrice() {
		return price;
	}

	public String getHeadTexture() {
		return headTexture;
	}

	private final String headTexture;
}
