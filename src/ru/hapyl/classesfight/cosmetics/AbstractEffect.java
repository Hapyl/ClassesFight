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

package ru.hapyl.classesfight.cosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.menu.collectibles.ShopItem;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.utils.GameElement;

public abstract class AbstractEffect extends ShopItem implements GameElement {

	public static final long NOT_AVAILABLE_IN_SHOP = -1;

	private final String name;
	private final Material icon;
	private final long cost;
	private String lore;

	public AbstractEffect(final String name, final String lore, final long cost, final Material icon, ShopItemRarity rarity) {
		super(rarity);
		this.name = name;
		this.lore = lore;
		this.cost = cost;
		this.icon = icon;
		if (this instanceof Listener) {
			if (ClassesFight.getPlugin().isEnabled()) {
				ClassesFight.getPlugin().getServer().getPluginManager().registerEvents((Listener)this, ClassesFight.getPlugin());
			}
		}
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	public void setLore(String lore) {
		this.lore = lore;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public AbstractEffect(final String name, final int cost, final Material item, final String lore) {
		this(name, lore, cost, item, ShopItemRarity.COMMON);
	}

	public final String getName() {
		return name;
	}

	public final Material getIcon() {
		return icon;
	}

	public final long getCost() {
		return cost;
	}

	public final String getLore() {
		return lore;
	}

	public abstract void display(Player player);

}
