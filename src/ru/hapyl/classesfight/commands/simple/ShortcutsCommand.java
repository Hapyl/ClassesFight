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