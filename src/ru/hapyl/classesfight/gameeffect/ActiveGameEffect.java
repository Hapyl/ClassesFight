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

public class ActiveGameEffect {

	private final GameEffectType type;
	private long ticksRemaining;

	public ActiveGameEffect(GameEffectType type, long ticksRemaining) {
		this.type = type;
		this.ticksRemaining = ticksRemaining;
	}

	public GameEffectType getType() {
		return type;
	}

	public long getTicksRemaining() {
		return ticksRemaining;
	}

	public GameEffect getEffect() {
		return this.type.getEffect();
	}

	public void setTicksRemaining(long ticksRemaining) {
		this.ticksRemaining = ticksRemaining;
	}

	public void addTicksRemaining(long ticks) {
		this.ticksRemaining += ticks;
	}

	public void tickDown() {
		this.ticksRemaining--;
	}


}
