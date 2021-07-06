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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class Prefix extends AbstractEffect {

	private final String prefix;

	public Prefix(String name, String lore, String prefix, long cost, Material icon, ShopItemRarity rarity) {
		super(name, lore, cost, icon, rarity);
		this.prefix = prefix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public final void display(Player player) {
		Chat.sendMessage(player, "&a%s Prefix Preview: &r%s", this.getName(), this.prefix);
	}
}
