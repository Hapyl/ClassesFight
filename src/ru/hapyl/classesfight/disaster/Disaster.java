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

import org.bukkit.Material;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.utils.GameElement;

public abstract class Disaster implements GameElement {

	private final Material material;
	private final String name;
	private final String about;
	private boolean enabled;

	protected final GameManager manager = GameManager.current();
	protected int currentTick;

	public Disaster(Material material, String name, String about) {
		this.material = material;
		this.name = name;
		this.about = about;
		this.enabled = false;
		this.currentTick = 0;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Material getMaterial() {
		return material;
	}

	public String getName() {
		return name;
	}

	public String getAbout() {
		return about;
	}

	public int currentTick() {
		return currentTick;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public abstract void onTick();

}
