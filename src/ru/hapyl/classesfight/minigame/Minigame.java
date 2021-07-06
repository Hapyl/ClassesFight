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

package ru.hapyl.classesfight.minigame;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.games.Chronomatron;
import ru.hapyl.classesfight.minigame.games.Superpairs;
import ru.hapyl.classesfight.minigame.games.TicTacToe;
import ru.hapyl.classesfight.minigame.games.Ultrasequencer;

public enum Minigame {

	ULTRA_SEQUENCER(new Ultrasequencer()),
	SUPER_PAIRS(new Superpairs()),
	CHRONOMATRON(new Chronomatron()),
	TIC_TAC_TOE(new TicTacToe());

	private final IMinigame gameClass;

	Minigame(IMinigame gameClass) {
		this.gameClass = gameClass;
	}

	public IMinigame newInstance(Player player, boolean debug) {
		MinigameManager.current().startGame(this.gameClass, player, debug);
		return this.gameClass;
	}

	public IMinigame getGame() {
		return gameClass;
	}

	public String getName() {
		return this.gameClass.getName();
	}

}
