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

package ru.hapyl.classesfight.minigame.games;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.IMinigame;

public class TicTacToe extends IMinigame {

	public TicTacToe() {
		super("Tic Tac Toe",
				Integer.MAX_VALUE,
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWFlODVmNzRmOGUyYzA1NGI3ODFhMjlmYTliMjU5MzRiYTYzYmI3OWYxZGU4YTk1YjQzNmQ5YmZkY2FmNGNkIn19fQ==",
				"Win rounds of Tic-Tac-Toe against AI to win rewards!");
	}

	@Override
	public void newInstance(Player player, boolean debug) {
		super.notYetImplemented(player);
	}
}
