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

package ru.hapyl.classesfight.gamemode.mode;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.gamemode.CFGameMode;
import ru.hapyl.classesfight.gamemode.wc.WinConditionEnum;

public class Normal extends CFGameMode {

	public Normal() {
		super("Normal", 600, WinConditionEnum.LAST_MAN_STANDING);
	}

	@Override
	public void processBeforeGameStart() {

	}

	@Override
	public void processPlayerBeforeGameStart(Player player) {

	}

	@Override
	public void processAfterGameEnd() {

	}

	@Override
	public void processPlayerAfterGameEnd(Player player) {

	}

	@Override
	public void processPlayerDeath(Player player, Player killer) {
		final Database database = Database.getDatabase(player);
		final EnumEffect effect = database.getCosmetics().getCurrentEffect(EnumEffect.Type.DEATH);

		if (effect != null && effect.isEnabled(player)) {
			effect.display(player);
		}

		GameManager.current().getPlayers().remove(player);

	}
}
