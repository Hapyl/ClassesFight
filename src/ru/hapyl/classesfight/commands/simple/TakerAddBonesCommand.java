package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TakerClass;

import java.util.List;

public class TakerAddBonesCommand extends SimpleAdminCommand {

    public TakerAddBonesCommand(String str) {
        super(str);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;

        if (GameManager.current().isGameInProgress() && GameManager.current().isDebugMode() && ClassManager.getClass(player) == ClassManager.TAKER) {
            ((TakerClass)ClassManager.TAKER.getTheClass()).addBoneCharge(player);
            Chat.sendMessage(player, "&aAdded bone charged!");
            return;
        }
        Chat.sendMessage(player, "&cGame must in progress, debug mode must be enabled and your class must be Taker to use this!");

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}