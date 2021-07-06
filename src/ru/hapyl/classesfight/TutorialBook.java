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

package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.CenterChat;

public class TutorialBook {

	private final ItemStack item;

	public TutorialBook() {
		final ItemBuilder builder = new ItemBuilder(Material.WRITTEN_BOOK).setBookAuthor("hapyl").setBookTitle("Tutorial Book");
		builder.setBookPages(

				// Front Page
				base("_&lWelcome to",
						"_&6&lClasses Fight!",
						"",
						"This book will teach you everything about the game!",
						"",
						"_&lTable of Contents:",
						" 2 --- Classes",
						" 3 --- Weapons",
						" 4 --- Abilities",
						" 5 --- Ultimates"

				),

				// Classes
				base("_&3&lClasses",
						"&oYou must pick a class before playing. Every class is unique in it's way, someone likes to use a sword, " +
								"someone else a bow, ",
						"or... &owhy not both?", "", "There is short description about a class and their abilities in the class select menu."),

				// Weapons
				base("_&3&lWeapons",
						"&oA weapon is a best friend of a fighter! It can be a sword, a bow or a unknown creation of a some Doctor that can " +
								"absorb block energy, you now, this kinda of stuff.", "",
						"Play with a class to feel their weapon!"),

				// Abilities
				base("_&3&lAbilities",
						"&oAbilities are the key to victory! Look at the Alchemist as example, week guy by himself, but don't let him drink that " +
								"potion!",
						"", "Abilities have their pros and cons so use them wisely!"),

				// Ultimate
				base("_&3&lUltimates",
						"&oEvery class has an Ultimate ability that needs to be charged before use. You can see required points in the Scoreboard " +
								"(or Tab!) while in game. ", "", "You will gain &lone&r ultimate point every &l10s&r in game.")

		);
		this.item = builder.toItemStack();
	}

	public ItemStack getItem() {
		return item;
	}

	private BaseComponent[] base(String... text) {
		final ComponentBuilder builder = new ComponentBuilder();
		for (int i = 0; i < text.length; i++) {
			if (i > 0) {
				builder.append("\n");
			}
			String str = text[i];
			if (str.startsWith("_")) {
				str = str.substring(1);
				builder.append(CenterChat.makeString(str, 54));
			}
			else {
				builder.append(Chat.format(str));
			}
		}
		return builder.create();
	}

	private BaseComponent[] base(String text) {
		return new ComponentBuilder(Chat.format(text)).create();
	}

	private BaseComponent[] base(String text, ClickEvent event) {
		return new ComponentBuilder(Chat.format(text)).event(event).create();
	}

	private BaseComponent[] base(String text, HoverEvent event) {
		return new ComponentBuilder(Chat.format(text)).event(event).create();
	}

	private BaseComponent[] base(String text, ClickEvent event1, HoverEvent event) {
		return new ComponentBuilder(Chat.format(text)).event(event1).event(event).create();
	}


}
