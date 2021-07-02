package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.record.Record;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.parkour.ParkourManager;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.stats.StatsHologramManager;
import ru.hapyl.classesfight.utils.Validator;

import java.util.Arrays;
import java.util.List;

public class ParkourCommand extends SimpleCommand {

    public ParkourCommand(String str) {
        super(str);
        this.setAllowOnlyPlayer(true);
        this.setAllowOnlyOp(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player)sender;
        if (args.length >= 1) {

            // display all parkours
            if ("list".equalsIgnoreCase(args[0])) {
                int pos = 1;
                for (ParkourCourse value : ParkourCourse.values()) {
                    final Parkour parkour = value.getParkour();
                    Chat.sendMessage(player, "&3" + pos + "->" + parkour.getName());
                    Chat.sendMessage(player, "  &bStart = " + BukkitUtils.locationToString(parkour.getStart().toLocation()));
                    Chat.sendMessage(player, "  &bFinish = " + BukkitUtils.locationToString(parkour.getFinish().toLocation()));
                    ++pos;
                }
                return;
            }

            if ("replay".equalsIgnoreCase(args[0])) {

                Player target = player;

                if (args.length >= 2) {
                    target = Bukkit.getPlayer(args[1]);
                }

                if (target == null) {
                    assert player != null;
                    Chat.sendMessage(player, "&cCould not find player named %s!", args[1]);
                    return;
                }

                final Record record = Record.getRecord(target);
                if (record == null) {
                    ParkourManager.parkourMessage(player, "&cCould not find a replay for you! Keep in mind that replays don't stay after server restarts. (yet)");
                    return;
                }

                if (record.isPlaying()) {
                    record.stopPlayingRecord();
                    record.sendRecordMessage("&aStopped parkour replay.");
                    return;
                }

                record.playReplay();
                return;
            }

            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("resetleaders")) {
                    final ParkourCourse parkour = Validator.getEnumValue(ParkourCourse.class, args[1]);

                    if (parkour == null) {
                        Chat.sendMessage(player, "&cParkour not found!");
                        return;
                    }

                    Chat.broadcast("&a%s just reset %s Leaderboard!", player.getName(), parkour.getParkour().getName());
                    StatsContainer.current().resetLeaders(parkour);
                    StatsHologramManager.current().updateParkourLeaderboard(parkour);
                    StatsHologramManager.current().updateViewers();

                }
            }

            if (args.length >= 3) {
                // parkour settime PLAYER PARKOUR LONG
                // parkour resetleaders PARKOUR
                // parkour revoke PLAYER PARKOUR
                final Player target = Bukkit.getPlayer(args[1]);
                final ParkourCourse course = Validator.getEnumValue(ParkourCourse.class, args[2]);

                if (target == null) {
                    Chat.sendMessage(player, "&cThis player is not online!");
                    return;
                }

                if (course == null) {
                    Chat.sendMessage(player, "&cParkour not found!");
                    return;
                }

                final Parkour parkour = course.getParkour();

                // settime check
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("settime")) {
                        long newTime = Validate.getLong(args[3]);
                        if (newTime == 0) {
                            Chat.sendMessage(player, "&cTime cannot be that short!");
                            return;
                        }

                        StatsContainer.current().setParkourStat(parkour, target, newTime, true);
                        Chat.sendMessage(player, "&aSet new time for %s at %s to %s.", target.getName(), parkour.getName(), newTime);
                        StatsHologramManager.current().updateParkourLeaderboard(course);
                        StatsHologramManager.current().updateViewers();

                    }
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "revoke": {
                        if (parkour.hasCompleteBefore(target)) {
                            parkour.setCompletion(target, false);
                            Chat.sendMessage(player, "&aRevoke %s's %s completion.", target.getName(), parkour.getName());
                        }
                        else {
                            Chat.sendMessage(player, "&c%s haven't completed this parkour yet!", target.getName());
                        }
                        break;
                    }
                    case "grant": {
                        if (!parkour.hasCompleteBefore(target)) {
                            parkour.setCompletion(target, true);
                            Chat.sendMessage(player, "&aGrant %s completion to %s.", parkour.getName(), target.getName());
                        }
                        else {
                            Chat.sendMessage(player, "&c%s already complete this parkour!", target.getName());
                        }
                        break;
                    }
                }

            }

        }
        else {
            player.sendMessage(this.getUsage());
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return super.completerSort(Arrays.asList("list", "revoke", "grant", "resetleaders", "settime", "replay"), args);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("resetleaders")) {
            return super.completerSort(super.arrayToList(ParkourCourse.values()), args);
        }
        if (args.length == 3 && !args[0].equalsIgnoreCase("list")) {
            return super.completerSort(super.arrayToList(ParkourCourse.values()), args);
        }
        return null;
    }

}