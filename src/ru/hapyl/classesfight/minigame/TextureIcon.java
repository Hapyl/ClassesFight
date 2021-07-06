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

package ru.hapyl.classesfight.minigame;

import kz.hapyl.spigotutils.module.inventory.Icon;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TextureIcon extends Icon {

	private final String texture;
	private final boolean smart;

	public TextureIcon(String texture, String name, String lore, boolean smart) {
		super(Material.PLAYER_HEAD, name, lore);
		this.texture = texture;
		this.smart = smart;
	}

	public TextureIcon(String texture, String name, String lore) {
		this(texture, name, lore, true);
	}

	public String getTexture() {
		return this.texture;
	}

	@Override
	public ItemStack buildIcon() {
		return this.getBuilder().toItemStack();
	}

	@Override
	public ItemBuilder getBuilder() {
		final ItemBuilder builder = super.getBuilder().setHeadTexture(this.getTexture()).removeLore().addLore();
		if (this.smart) {
			return builder.addSmartLore(this.getLore());
		}
		else {
			return builder.addLore(this.getLore());
		}
	}
}
