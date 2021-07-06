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

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class VarArgsGameEffect<T extends Number> implements GameEffect {

	protected final Map<Player, T> values;

	protected VarArgsGameEffect() {
		this.values = Maps.newHashMap();
	}

	public abstract void onEffectStarts(Player player, T t);

	public void onEffectUpdates(Player player, T t) {

	}

	@Override
	@SuppressWarnings("all")
	public final void onEffectUpdate(Player player, Object... extra) {
		this.onEffectUpdates(player, (T)extra[0]);
	}

	@Override
	@SuppressWarnings("all")
	public final void onEffectStart(Player player, Object... extra) {
		this.onEffectStarts(player, (T)extra[0]);
	}

	public void setValue(Player player, T t) {
		values.put(player, t);
	}

	public T getValue(Player player) {
		return values.getOrDefault(player, defaultValue());
	}

	public void removeValue(Player player) {
		values.remove(player);
	}

	public final void clearValues() {
		values.clear();
	}

	protected abstract T defaultValue();
}
