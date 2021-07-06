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

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.GrimoireCooldownApplier;
import ru.hapyl.classesfight.classes.iclass.Librarian;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.classes.iclass.inst.Instance;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;

public class LibrarianShield extends Ability implements GrimoireCooldownApplier {
	public LibrarianShield() {
		super("Voidless Shield", "Creates a shield with voidless capacity of absorbing damage for &b5&7/&b6&7/&b7&7/&b8s&7.");
		this.setItem(Material.SHIELD);
	}

	private final int[] tickPerLevel = {100, 120, 140, 160};

	@Override
	public Response useAbility(Player player) {
		final Librarian librarian = Instance.LIBRARIAN;
		final int tick = tickPerLevel[librarian.getBookLevel(player) - 1];
		GameEffectManager.applyShield(player, tick, -1);
		Chat.sendMessage(player, "&aApplied shield for %ss!", BukkitUtils.roundTick(tick));
		librarian.removeSpellItems(player, Abilities.LIBRARIAN_SHIELD);
		GrimmoreBook.applyCooldown(player, getGrimmoreCooldown());
		return Response.OK;
	}

	@Override
	public int getGrimmoreCooldown() {
		return 30 * 20;
	}
}
