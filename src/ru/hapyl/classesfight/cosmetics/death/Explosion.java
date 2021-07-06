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

package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.Random;

public class Explosion extends AbstractEffect {

	public Explosion() {
		super("Big Blast", "Creates a firework explosion of random color.", 350, Material.FIREWORK_ROCKET, ShopItemRarity.RARE);
	}

	private final Color[] validColors = {Color.RED, Color.AQUA, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.FUCHSIA, Color.LIME, Color.MAROON, Color.OLIVE, Color.PURPLE};

	@Override
	public void display(Player player) {

		final Location at = player.getLocation();
		final Firework firework = at.getWorld().spawn(at.add(0, 1, 0), Firework.class);

		Color theColor = validColors[new Random().nextInt(validColors.length)];

		final FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(FireworkEffect.builder().flicker(false).trail(false).withColor(theColor).with(FireworkEffect.Type.BALL).build());
		meta.setPower(0);
		firework.setFireworkMeta(meta);
		firework.detonate();

	}

}
