package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RedeemCommand extends SimpleCommand {

    public RedeemCommand(String str) {
        super(str);
        this.setCooldownTick(100);
        this.setAllowOnlyPlayer(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        if (args.length == 1) {
            final String code = args[0];
            if (code.length() != 12) {
                Chat.sendMessage(player, "&cInvalid code type!");
                return;
            }
            Chat.sendMessage(player, "&7Looking for code \"%s\"...", code.toUpperCase(Locale.ROOT));
            new GameTask() {
                @Override
                public void run() {
                    Chat.sendMessage(player, "&cCould not find code \"%s\"!", code.toUpperCase(Locale.ROOT));
                    PlayerLib.playSound(player, Sound.BLOCK_ANVIL_PLACE, 0.75f);
                }
            }.runTaskLater(20 * (new Random().nextInt(4)));
            return;
        }
        Chat.sendMessage(player, "&cInvalid amount of arguments!");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}