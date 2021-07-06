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

package ru.hapyl.classesfight.ability.storage;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class Grenade extends Ability {
	public Grenade() {
		super("Grenade", "Throw a grenade that bounce off walls. Explodes after &b2s &7in medium AoE.");
		this.setItem(Material.FIREWORK_STAR);
		this.setCooldown(11);
	}

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();
		final World world = location.getWorld();
		if (world == null) {
			return Response.error("world null");
		}

		final Item item = world.dropItem(location, new ItemStack(Material.FIREWORK_STAR));
		item.setVelocity(player.getLocation().getDirection().setY(0.5f));
		item.setPickupDelay(5000);
		new GameTask() {
			@Override
			public void run() {
				item.remove();
				GameUtils.createFakeExplosion(item.getLocation(), 5, 12.0d, null, EnumDamageCause.ENTITY_ATTACK);
			}
		}.runTaskLater(40);

		return Response.OK;
	}
}
