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

package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class ParanoiaBlessing implements Blessing {

	@Override
	public void bless(Player player) {
		GameEffectManager.applyEffect(player, GameEffectType.PARANOIA, 10 * 20);
	}

	@Override
	public String message() {
		return "You will have problems seeing things...";
	}

	@Override
	public void playSound(Player player) {
		OldPlayerLib.playSound(player, Sound.AMBIENT_CAVE, 2);
	}
}
