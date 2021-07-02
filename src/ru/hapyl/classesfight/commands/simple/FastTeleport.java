package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FastTeleport extends SimpleCommand {
    public FastTeleport(String name) {
        super(name);
        this.setAllowOnlyPlayer(true);
        this.setPermission("mmu.op");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] args) {
        Player player = (Player)commandSender;
        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Chat.sendMessage(player, "&cPlayer is not online!");
                return;
            }

            player.teleport(target);
            Chat.sendMessage(player, "&aTelepotred to %s", target.getName());

        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        final List<String> names = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            names.add(onlinePlayer.getName());
        }
        return names;
    }
}
