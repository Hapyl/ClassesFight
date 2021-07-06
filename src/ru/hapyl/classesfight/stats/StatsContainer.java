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

package ru.hapyl.classesfight.stats;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.parkour.ParkourData;
import ru.hapyl.classesfight.utils.ConfigFile;

import java.util.Locale;
import java.util.TreeMap;
import java.util.UUID;

public class StatsContainer extends ConfigFile {

    private static StatsContainer instance;

    private final String classEntryPlayed = "class.%s.%s";
    private final String parkourPlayerStat = "parkour.%s.%s";

    public StatsContainer() {
        super("stats");
        instance = this;
    }

    public static StatsContainer current() {
        return instance;
    }

    public void addClassEntry(ClassManager clazz, ClassEntryInfo info, long value) {
        this.setClassEntry(clazz, info, this.getClassEntry(clazz, info) + value);
    }

    public void setClassEntry(ClassManager clazz, ClassEntryInfo info, long value) {
        this.getConfig().set(String.format(classEntryPlayed, clazz.name(), info.name()), value);
    }

    public long getClassEntry(ClassManager clazz, ClassEntryInfo info) {
        return this.getConfig().getLong(String.format(classEntryPlayed, clazz.name(), info.name()), 0L);
    }

    public long getParkourStat(Parkour parkour, Player player, ParkourStat stat) {
        return this.getConfig().getLong(String.format(parkourPlayerStat, parkour.getDatabasePath(), player.getUniqueId()) + ("." + stat.path()), Long.MAX_VALUE);
    }

    public long getParkourStat(Parkour parkour, UUID player, ParkourStat stat) {
        return this.getConfig().getLong(String.format(parkourPlayerStat, parkour.getDatabasePath(), player) + ("." + stat.path()), Long.MAX_VALUE);
    }

    public boolean isCheated(Parkour parkour, Player player) {
        return this.getConfig().getBoolean(String.format(parkourPlayerStat, parkour.getDatabasePath(), player.getUniqueId()) + ("." + ParkourStat.CHEAT.path()), false);
    }

    public boolean isCheated(Parkour parkour, UUID player) {
        return this.getConfig().getBoolean(String.format(parkourPlayerStat, parkour.getDatabasePath(), player) + ("." + ParkourStat.CHEAT.path()), false);
    }

    /**
     * old path:
     * <p>
     * (?parkour).
     * parkour.
     * NAME.
     * UUID: time
     * <p>
     * new path:
     * <p>
     * parkour.
     * NAME.
     * UUID{
     * time: _time
     * timestamp: _timestamp
     * chest: _boolean
     * }
     */
    public void setParkourStat(Parkour parkour, Player player, long finished, boolean flag) {

        final YamlConfiguration config = this.getConfig();
        final String core = String.format(parkourPlayerStat, parkour.getDatabasePath(), player.getUniqueId());

        config.set(core + ".time", finished);
        config.set(core + ".timestamp", System.currentTimeMillis());
        config.set(core + ".cheat", flag);
    }

    public void setParkourStat(Parkour parkour, ParkourData data, boolean flag) {
        final YamlConfiguration config = this.getConfig();
        final String core = String.format(parkourPlayerStat, parkour.getDatabasePath(), data.getPlayer().getUniqueId());

        this.setParkourStat(parkour, data.getPlayer(), data.getFinishedAt(), flag);

        // stats
        config.set(core + ParkourStat.STAT_JUMP.dotPath(), data.getJumps());
        config.set(core + ParkourStat.CHECKPOINTS_USED.dotPath(), data.getCheckpointTeleports());

    }

    public void setParkourStat(Parkour parkour, Player player, long finished) {
        setParkourStat(parkour, player, finished, false);
    }

    public boolean setParkourStatIfBetter(Parkour parkour, ParkourData data) {
        final Player player = data.getPlayer();
        final long finished = data.getFinishedAt();
        final long best = getParkourStat(parkour, player, ParkourStat.TIME);

        if (finished <= best) {
            this.setParkourStat(parkour, data, false);
            return true;
        }
        return false;
    }

    // * Class Related Things
    public void setClassStat(ClassManager clazz, ClassStat stat, long value) {
        this.getConfig().set(String.format("class-stat.%s.%s", classToPath(clazz), stat), value);
    }

    public void addClassStat(ClassManager clazz, ClassStat stat, long toAdd) {
        setClassStat(clazz, stat, getClassStat(clazz, stat, 0L) + toAdd);
    }

    public long getClassStat(ClassManager clazz, ClassStat stat, long def) {
        return this.getConfig().getLong(String.format("class-stat.%s.%s", classToPath(clazz), stat), def);
    }

    private String classToPath(ClassManager clazz) {
        return clazz.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public enum ClassStat {

        KILL, DEATH, WIN, LOSE, ULT_USE, PLAYED_TIMES

    }

    public void resetLeaders(ParkourCourse parkour) {
        this.getConfig().set("parkour." + parkour.getParkour().getDatabasePath(), null);
    }

    public TreeMap<Long, OfflinePlayer> getTopParkourLeaders(Parkour parkour, int limit) {
        final TreeMap<Long, OfflinePlayer> list = new TreeMap<>();

        final YamlConfiguration config = this.getConfig();
        final ConfigurationSection sec = config.getConfigurationSection("parkour." + parkour.getDatabasePath());

        if (sec == null) {
            return list;
        }

        for (String uuid : sec.getKeys(false)) {
            if (list.size() == limit) {
                return list;
            }
            final long shouldBeTime = config.getLong(String.format("parkour.%s.%s.time", parkour.getDatabasePath(), uuid), Long.MAX_VALUE);
            list.put(shouldBeTime, Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        }

        return list;
    }

    // player - finished
    public TreeMap<Long, OfflinePlayer> getTopThreeParkourLeaders(Parkour parkour) {
        return getTopParkourLeaders(parkour, 3);
    }

    public boolean isNewTopOne(Parkour parkour, long finishedAt) {
        final TreeMap<Long, OfflinePlayer> list = getTopThreeParkourLeaders(parkour);
        if (list.isEmpty()) {
            return false;
        }
        return finishedAt <= list.keySet().toArray(new Long[]{})[0];
    }

    public enum ParkourStat {

        TIME(Long.MAX_VALUE),
        TIMESTAMP,
        CHEAT,
        STAT_JUMP,
        CHECKPOINTS_USED,

        ;

        private final long def;

        ParkourStat() {
            this(0);
        }

        ParkourStat(long def) {
            this.def = def;
        }

        public long getDef() {
            return def;
        }

        public String path() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String dotPath() {
            return "." + this.path();
        }

    }

}
