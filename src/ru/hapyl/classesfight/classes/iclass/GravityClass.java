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

package ru.hapyl.classesfight.classes.iclass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.Icons;

public class GravityClass extends IClass {

	private final int ultimateTime = 200;

	public GravityClass() {
		super("Dr. Ed", Material.GLASS_BOTTLE);
		this.setRole(ClassRole.RANGE);
		this.setInfo("Simple named scientist with not simple inventions.__" + Icons.ABILITY_DOT + Abilities.GRAVITY_GUN.getName() + " &7A tool that is capable of absorbing blocks elements.__" + Abilities.POTION_BLINDNESS.getAbout(),
				"Upgrades People, Upgrades!",
				"Grants Dr. Ed an upgraded version of his \"" + Abilities.GRAVITY_GUN.getName() + "\" &7for &b" + (this.ultimateTime / 20) + "s&7. This device is capable of capturing entities' flesh and energy, allowing to manipulate them.",
				7);

		final ClassEquipment equipment = this.getClassEquipment(true);

		this.setRating(ClassRating.A);

		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I1MWU5NmJkZGQxNzc5OTJkNjgyNzhjOWQ1ZjFlNjg1YjYwZmJiOTRhYWE3MDkyNTllOWYyNzgxYzc2ZjgifX19");
		equipment.setChestplate(179, 204, 204);
		equipment.setLeggings(148, 184, 184);
		equipment.setBoots(71, 107, 107);

		Abilities.GRAVITY_GUN.addItemIfExists(equipment);
		Abilities.POTION_BLINDNESS.addItemIfExists(equipment);
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true);
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(4, Abilities.UPGRADED_GRAVITY_GUN.getAbility().getItem().getItem());
		inventory.setHeldItemSlot(4);
		new GameTask() {
			@Override
			public void run() {
				setUsingUltimate(player, false);
				inventory.setItem(4, new ItemStack(Material.AIR));
			}
		}.runTaskLater(this.ultimateTime);
	}

}
