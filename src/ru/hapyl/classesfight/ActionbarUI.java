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

package ru.hapyl.classesfight;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.reflect.Ticking;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.utils.GameElement;

import java.util.List;
import java.util.Map;

public class ActionbarUI implements Ticking, GameElement {

	private final Player player;
	private final Map<Integer, String> strings;

	public ActionbarUI(Player player) {
		this.player = player;
		this.strings = Maps.newLinkedHashMap();
	}

	@Override
	public void tick() {
		if (Spectator.isSpectator(player)) {
			return;
		}
		final String string = buildUIElement();
		if (string.isEmpty()) {
			return;
		}
		Chat.sendActionbar(player, string);
	}

	public void setString(int pos, String str) {
		this.strings.put(pos, str);
	}

	public void setString(int pos, String str, Object... dot) {
		this.setString(pos, String.format(str, dot));
	}

	public boolean removeString(int pos) {
		return !this.strings.remove(pos).isEmpty();
	}

	public boolean hasString(int pos) {
		return this.strings.containsKey(pos);
	}

	// Effects | Custom | Cooldowns

	private String buildUIElement() {

		final StringBuilder builder = new StringBuilder();
		final long combatTime = DamageFeature.getCombatTime(player);

		if (DamageFeature.isInCombat(player)) {
			builder.append("&4☠");
		}

		if (!this.strings.isEmpty()) {
			if (DamageFeature.isInCombat(player)) {
				builder.append(" &8|&7 ");
			}
			for (final String value : this.strings.values()) {
				builder.append(value).append(" ");
			}
		}

		final List<Cooldown> cooldowns = Cooldown.getCooldowns(player);
		if (!cooldowns.isEmpty()) {
			if (!this.strings.isEmpty()) {
				builder.append(" &8|&7 ");
			}
			else if (DamageFeature.isInCombat(player)) {
				builder.append(" &8|&7 ");
			}
			for (int i = 0; i < cooldowns.size(); i++) {
				final Cooldown cd = cooldowns.get(i);
				final int timeLeft = cd.getTimeLeft();
				builder.append(ChatColor.GREEN)
						.append(cd.getName())
						.append(" ")
						.append(ChatColor.AQUA)
						.append(ChatColor.BOLD)
						.append(timeLeft <= 0 ? "READY" : cd.getTimeLeftSec());
				if (timeLeft <= 0) {
					if (!cd.isDone()) {
						cd.setDone(true);
						cd.onStop();
					}
					if (timeLeft <= -1000) { // this used to display READY for one more second before cancelling the cooldown
						cd.stopCooldown();
					}
				}
				if (i != cooldowns.size() - 1) {
					builder.append("&7, ");
				}
			}
		}

		return builder.toString();

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {
		this.strings.clear();
	}

	public static ActionbarUI getUI(Player player) {
		return ClassesFight.getSingleton().getGameUI().getActionbarUI(player);
	}

}
