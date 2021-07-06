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

package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.CommandProcessor;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.experience.ExpGUI;
import ru.hapyl.classesfight.menu.GameMenuGUI;
import ru.hapyl.classesfight.menu.InfoMenu;
import ru.hapyl.classesfight.menu.MapSelectMenu;
import ru.hapyl.classesfight.menu.SettingsMenu;
import ru.hapyl.classesfight.menu.collectibles.gui.CollectionGUI;
import ru.hapyl.classesfight.menu.collectibles.gui.ShopGUI;
import ru.hapyl.classesfight.menu.minigame.MinigameMenu;

import java.util.function.Consumer;

public class ShortcutsCommand {

	private final CommandProcessor commandProcessor;

	public ShortcutsCommand() {

		this.commandProcessor = new CommandProcessor();
		register("collection", CollectionGUI::new);
		register("shop", ShopGUI::new);
		register("lvl", ExpGUI::new);
		register("minigames", MinigameMenu::new);
		register("settings", SettingsMenu::new);
		register("map", MapSelectMenu::new);
		register("menu", GameMenuGUI::new);
		register("stats", InfoMenu::new);
		register("archive", this::notYet);
	}

	private void notYet(Player player) {
		Chat.sendMessage(player, "&cThis feature is not yet available!");
	}

	private void register(String cmd, Consumer<Player> consumer) {

		final SimpleCommand command = new SimpleCommand(cmd) {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				if (GameManager.current().isGameInProgress()) {
					Chat.sendMessage(commandSender, "&cYou can only use this command in lobby!");
					return;
				}
				consumer.accept((Player)commandSender);
			}
		};

		command.setAllowOnlyPlayer(true);
		this.commandProcessor.registerCommands(command);

	}

}