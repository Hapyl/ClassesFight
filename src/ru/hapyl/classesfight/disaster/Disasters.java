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

package ru.hapyl.classesfight.disaster;

import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import ru.hapyl.classesfight.ability.storage.LowGravity;
import ru.hapyl.classesfight.ability.storage.Thunder;
import ru.hapyl.classesfight.disaster.storage.TheDarkNight;

public enum Disasters {

	THE_DARK_NIGHT(new TheDarkNight(Material.SQUID_SPAWN_EGG, "The Dark Night", "This is a very dark night, and only lightning can light the way.")),
	LOW_GRAVITY(new LowGravity(Material.END_STONE, "Low Gravity", "Are we going to moon?")),
	GOD_OF_THUNDER(new Thunder(Material.PUFFERFISH_BUCKET, "God of Thunder", "A lightning will charge periodically upon all players, dodge or suffer damage.")),
	;

	private final Disaster disaster;

	Disasters(Disaster disaster) {
		this.disaster = disaster;
	}

	public boolean isEnabled() {
		return disaster.isEnabled();
	}

	public void setEnabled(boolean flag) {
		disaster.setEnabled(flag);
	}

	public Disaster getDisaster() {
		return disaster;
	}

	public static void forEach(Action<Disaster> action) {
		for (final Disasters value : values()) {
			action.use(value.disaster);
		}
	}

}
