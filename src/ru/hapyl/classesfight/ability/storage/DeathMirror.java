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
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TakerClass;
import ru.hapyl.classesfight.classes.iclass.extra.TakerData;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;

public class DeathMirror extends Ability {
	public DeathMirror() {
		super("Mirror of Death", "Instantly consume one Spiritual Bone and create shield that absorbs any damage for &b5s&7.");
		this.setItem(Material.DRIED_KELP);
		this.setCooldown(200);
	}

	@Override
	public Response useAbility(Player player) {

		final TakerData data = ((TakerClass)ClassManager.TAKER.getTheClass()).getData(player);

		if (data.getBoneCharges() <= 0) {
			return Response.error("Not enough bones!");
		}

		data.removeBone();
		GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, 5 * 20);

		return Response.OK;
	}
}
