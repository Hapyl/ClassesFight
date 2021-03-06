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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.GrimoireCooldownApplier;
import ru.hapyl.classesfight.classes.iclass.Librarian;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.classes.iclass.inst.Instance;
import ru.hapyl.classesfight.runnable.GameTask;

public class WeaponDarkness extends Ability implements GrimoireCooldownApplier {
	public WeaponDarkness() {
		super("Infusion of Darkness", "Infuses your weapon for &b10&7/&b13&7/&b16&7/&b19s &7with higher damage.");
		this.setItem(Material.INK_SAC);
	}

	private final int[] tickPerLevel = {200, 260, 320, 380};

	@Override
	public Response useAbility(Player player) {
		final Librarian librarian = Instance.LIBRARIAN;
		librarian.infuseWeapon(player, true);
		new GameTask() {
			@Override
			public void run() {
				librarian.infuseWeapon(player, false);
			}
		}.runTaskLater(tickPerLevel[librarian.getBookLevel(player) - 1]);
		librarian.removeSpellItems(player, Abilities.WEAPON_DARKNESS);
		GrimmoreBook.applyCooldown(player, getGrimmoreCooldown());
		return Response.OK;
	}

	@Override
	public int getGrimmoreCooldown() {
		return 30 * 20;
	}
}
