package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.minigame.MinigameMenu;
import ru.hapyl.classesfight.minigame.Minigame;
import ru.hapyl.classesfight.minigame.MinigameManager;

import java.util.List;

public class MinigameCommand extends SimpleCommand {

    public MinigameCommand(String str) {
        super(str);
        this.setAllowOnlyPlayer(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;

        // minigame MINIGAME
        if (args.length >= 1) {

            boolean debug = false;

            if (args.length == 2) {
                debug = args[1].equalsIgnoreCase("-d");
            }

            final Minigame minigame = Validate.getEnumValue(Minigame.class, args[0]);
            if (minigame == null) {
                Chat.sendMessage(player, "&cCould not find minigame %s.", args[0]);
                return;
            }

            Chat.sendMessage(player, "&aStarting %s minigame%s...", minigame.getName(), debug ? " in debug mode" : "");
            minigame.newInstance(player, debug);

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return super.completerSort(arrayToList(Minigame.values()), args);
        }
        return null;
    }

}