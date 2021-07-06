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

package ru.hapyl.classesfight.utils.candle;

import org.bukkit.entity.Player;

public class CandleData {

	private final Player player;
	private Candles candle;
	private boolean randomOffset;

	public CandleData(Player player, Candles candle, boolean randomOffset) {
		this.player = player;
		this.candle = candle;
		this.randomOffset = randomOffset;
	}

	public void setCandle(Candles candle) {
		this.candle = candle;
	}

	public void setRandomOffset(boolean randomOffset) {
		this.randomOffset = randomOffset;
	}

	public Player getPlayer() {
		return player;
	}

	public Candles getCandle() {
		return candle;
	}

	public boolean isRandomOffset() {
		return randomOffset;
	}
}
