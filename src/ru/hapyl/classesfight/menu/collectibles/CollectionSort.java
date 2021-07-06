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

package ru.hapyl.classesfight.menu.collectibles;

import ru.hapyl.classesfight.cosmetics.EnumEffect;

import java.util.Comparator;
import java.util.List;

public enum CollectionSort {

	A_TO_Z("Alphabetical order."),
	Z_TO_A("Reversed alphabetical order."),
	RARITY_BEST_WORST("Best rarity items first."),
	RARITY_WORST_BEST("Worst rarity items first.");

	private final String about;

	CollectionSort(String about) {
		this.about = about;
	}

	public String getAbout() {
		return about;
	}

	public CollectionSort next() {
		final int nextIndex = this.ordinal() + 1;
		return values()[(nextIndex >= values().length) ? 0 : nextIndex];
	}

	public CollectionSort prev() {
		final int prevIndex = this.ordinal() - 1;
		return values()[prevIndex < 0 ? (values().length - 1) : prevIndex];
	}

	public void sort(List<EnumEffect> input) {
		switch (this) {

			case A_TO_Z: {
				input.sort(Comparator.comparing(a -> (a.getClazz().getName().charAt(0) + "")));
				break;
			}

			case Z_TO_A: {
				input.sort((a, b) -> (b.getClazz().getName().charAt(0) + "").compareTo(a.getClazz().getName().charAt(0) + ""));
				break;
			}

			case RARITY_BEST_WORST: {
				input.sort((a, b) -> b.getClazz().getRarity().ordinal() - a.getClazz().getRarity().ordinal());
				break;
			}

			case RARITY_WORST_BEST: {
				input.sort(Comparator.comparingInt(a -> a.getClazz().getRarity().ordinal()));
				break;
			}

		}
	}

}
