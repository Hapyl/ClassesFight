package ru.hapyl.classesfight.gamemode;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.gamemode.wc.WinConditionEnum;

public abstract class CFGameMode {

    private final String name;
    private final int timeLimit;
    private final WinConditionEnum winCondition;

    public CFGameMode(String name, int timeLimit, WinConditionEnum winCondition) {
        this.name = name;
        this.timeLimit = timeLimit;
        this.winCondition = winCondition;
    }

    protected CFGameMode(String name, int timeLimit) {
        this(name, timeLimit, WinConditionEnum.LAST_MAN_STANDING);
    }

    public String getName() {
        return this.name;
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public WinConditionEnum getWinCondition() {
        return this.winCondition;
    }

    public abstract void processBeforeGameStart();

    public abstract void processPlayerBeforeGameStart(final Player player);

    public abstract void processAfterGameEnd();

    public abstract void processPlayerAfterGameEnd(final Player player);

    public abstract void processPlayerDeath(final Player player, @Nullable final Player killer);

}
