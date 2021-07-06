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

package ru.hapyl.classesfight.gameeffect;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Immovable implements GameEffect {

	private final Map<Player, Double> oldValue = new HashMap<>();

	@Override
	public void onEffectStart(Player player, Object... extra) {
		final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		if (attribute == null) {
			return;
		}
		oldValue.put(player, attribute.getBaseValue());
		attribute.setBaseValue(1.0d);
	}

	@Override
	public void onEffectStop(Player player) {
		final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		if (attribute == null) {
			return;
		}
		attribute.setBaseValue(oldValue.getOrDefault(player, 0.0d));
		oldValue.remove(player);
	}

	@Override
	public String getEffectName() {
		return "Immovable";
	}
}
