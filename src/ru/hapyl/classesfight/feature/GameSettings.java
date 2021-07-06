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

package ru.hapyl.classesfight.feature;

import ru.hapyl.classesfight.gamemode.GameModeEnum;

public class GameSettings {

	private boolean allowFood;
	private boolean allowEvent;
	private GameModeEnum gameMode;

	public GameSettings() {
		this.allowFood = true;
		this.allowEvent = false;
		this.gameMode = GameModeEnum.NORMAL;
	}

	public boolean isAllowFood() {
		return allowFood;
	}

	public void setAllowFood(boolean allowFood) {
		this.allowFood = allowFood;
	}

	public boolean isAllowEvent() {
		return allowEvent;
	}

	public void setAllowEvent(boolean allowEvent) {
		this.allowEvent = allowEvent;
	}

	public GameModeEnum getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameModeEnum gameMode) {
		this.gameMode = gameMode;
	}
}
