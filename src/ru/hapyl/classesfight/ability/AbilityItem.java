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

package ru.hapyl.classesfight.ability;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public class AbilityItem {

	private final ItemStack stack;
	private final Ability ability;
	private int startAmount;

	public AbilityItem(Material type, Ability ability) {
		this(type, ability, "CLICK", true);
	}

	public AbilityItem(Material type, Ability ability, boolean flag) {
		this(type, ability, "CLICK", flag);
	}

	public AbilityItem(String str, Ability ability, String suffixNoSpace, boolean cancelEvent) {
		this.ability = ability;
		final CFItemBuilder builder = new CFItemBuilder(Material.PLAYER_HEAD, "AI_" + ability.getId())
				.setHeadTexture(str)
				.setName(String.format("&a%s%s", ability.getName(), suffixNoSpace == null ? "" : " &6&l" + suffixNoSpace))
				.setSmartLore(ability.getAbout())
				.withAbility(this.ability)
				.setCancelEvent(cancelEvent)
				.applyDefaultSettings(false);
		this.stack = builder.build();
		this.startAmount = 1;
	}

	public AbilityItem(String str, Ability ability, String suffixNoSpace) {
		this(str, ability, suffixNoSpace, true);
	}

	public AbilityItem(Material type, Ability ability, String suffixNoSpace) {
		this(type, ability, suffixNoSpace, true);
	}

	public AbilityItem(Material type, Ability ability, String suffixNoSpace, boolean canceledEvent) {
		this.ability = ability;
		final CFItemBuilder builder = new CFItemBuilder(type, "AI_" + ability.getId())
				.setName(String.format("&a%s%s", ability.getName(), suffixNoSpace == null ? "" : " &6&l" + suffixNoSpace))
				.setSmartLore(ability.getAbout())
				.withAbility(this.ability)
				.setCancelEvent(canceledEvent)
				.applyDefaultSettings(false);
		this.stack = builder.build();
		this.startAmount = 1;
	}

	public void setStartAmount(int amount) {
		this.startAmount = Math.min(64, Math.max(1, amount));
	}

	public int getStartAmount() {
		return startAmount;
	}

	public ItemStack getItem() {
		return this.stack;
	}

	public Material getType() {
		return this.stack.getType();
	}

	public Ability getAbility() {
		return ability;
	}

}
