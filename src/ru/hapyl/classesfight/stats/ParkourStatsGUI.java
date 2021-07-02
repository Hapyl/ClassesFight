package ru.hapyl.classesfight.stats;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.math.IntInt;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.utils.GameUtils;

import java.text.SimpleDateFormat;
import java.util.TreeMap;

public class ParkourStatsGUI extends PlayerGUI {

    private final ParkourCourse course;

    public ParkourStatsGUI(Player player, ParkourCourse course) {
        super(player, course.getParkour().getName() + " Leaders", 5);
        this.setCloseMenuItem(this.getSize() - 5);
        this.course = course;
        this.updateMenu();
    }

    private void updateMenu() {

        final Parkour parkour = this.course.getParkour();
        final StatsContainer stats = StatsContainer.current();
        final TreeMap<Long, OfflinePlayer> top = stats.getTopParkourLeaders(parkour, 21);

        if (top.isEmpty()) {
            this.setItem(22, new ItemBuilder(Material.CAULDRON).setName("&cNo Leaders Yet!").setSmartLore("No one yet complete this parkour, be the first one to do so!").build());
        }
        else {
            final IntInt pos = new IntInt(1);
            final IntInt slot = new IntInt(10);
            top.forEach((time, player) -> {
                final String name = player.getName();
                final ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(name).setName("&a#%s - %s", pos.getAndAdd(1), name);

                builder.addLore();
                builder.addLore("&7Completion Time: &b" + new SimpleDateFormat("mm:ss.SS").format(time));
                builder.addLore("&7Completion Date: &b" + GameUtils.formatDate(stats.getParkourStat(parkour, player.getUniqueId(), StatsContainer.ParkourStat.TIMESTAMP)));
                builder.addLore();

                if (stats.isCheated(parkour, player.getUniqueId())) {
                    builder.addSmartLore("Important! This record is &c&nnot&c legit, an admin used a command to set the time!", "&c");
                    builder.addLore();
                }

                builder.addLore("&8Extra Stats:");
                builder.addLore(" &7Times Jumped: &b" + stats.getParkourStat(parkour, player.getUniqueId(), StatsContainer.ParkourStat.STAT_JUMP));
                builder.addLore(" &7Checkpoints Used: &b" + stats.getParkourStat(parkour, player.getUniqueId(), StatsContainer.ParkourStat.CHECKPOINTS_USED));
                builder.addLore();
                builder.addLore("&aMore info coming soon!");

                this.setItem(slot.get(), builder.build());
                slot.addAndGet(slot.get() % 9 == 7 ? 3 : 1);
            });
        }

        this.openInventory();
    }
}
