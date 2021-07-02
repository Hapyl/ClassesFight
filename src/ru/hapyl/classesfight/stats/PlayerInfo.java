package ru.hapyl.classesfight.stats;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;

public class PlayerInfo {

    private final Player player;
    private ClassManager bestClass;
    private long kills;
    private long deaths;
    private long wins;

    public PlayerInfo(Player player) {
        this.player = player;
        this.getBestClass();
    }

    public Player getPlayer() {
        return player;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getKills() {
        return kills;
    }

    public long getWins() {
        return wins;
    }

    public ClassManager getBest() {
        return this.bestClass;
    }

    private void getBestClass() {
        ClassManager bestClass = null;

        for (ClassManager validClass : ClassManager.getValidClasses()) {
            if (bestClass == null) {
                bestClass = validClass;
                continue;
            }
            if (isOtherBetter(bestClass, validClass)) {
                bestClass = validClass;
            }
        }

        this.bestClass = bestClass;
        this.kills = getKills(bestClass);
        this.deaths = getDeaths(bestClass);
        this.wins = getWins(bestClass);

    }

    private boolean isOtherBetter(ClassManager what, ClassManager other) {
        final long thisKills = getKills(what);
        final long thisWins = getWins(what);
        final long otherKills = getKills(other);
        final long otherWins = getWins(other);
        return otherKills > thisKills && otherWins > thisWins;
    }

    private long getKills(ClassManager clazz) {
        final StatsContainer stats = StatsContainer.current();
        return stats.getClassEntry(clazz, ClassEntryInfo.KILLS);
    }

    private long getWins(ClassManager clazz) {
        final StatsContainer stats = StatsContainer.current();
        return stats.getClassEntry(clazz, ClassEntryInfo.WINS);
    }

    private long getDeaths(ClassManager clazz) {
        final StatsContainer stats = StatsContainer.current();
        return stats.getClassEntry(clazz, ClassEntryInfo.DEATHS);
    }


}
