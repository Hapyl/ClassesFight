package ru.hapyl.classesfight.gamemode;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.annotations.Shortcut;
import ru.hapyl.classesfight.gamemode.mode.DeathMatch;
import ru.hapyl.classesfight.gamemode.mode.Normal;

public enum GameModeEnum {

    NORMAL(new Normal()),
    DEATH_MATCH(new DeathMatch());

    private final CFGameMode mode;

    GameModeEnum(CFGameMode instance) {
        this.mode = instance;
    }

    public CFGameMode getMode() {
        return mode;
    }


    @Shortcut
    public int getTimeLimit() {
        return this.getMode().getTimeLimit();
    }

    @Shortcut
    public String getName() {
        return this.getMode().getName();
    }

    @Shortcut
    public void processBeforeGameStart() {
        this.getMode().processBeforeGameStart();
    }

    @Shortcut
    public void processPlayerBeforeGameStart(final Player player) {
        this.validateNull(player, "player");
        this.getMode().processPlayerBeforeGameStart(player);
    }

    @Shortcut
    public void processAfterGameEnd() {
        this.getMode().processAfterGameEnd();
    }

    public void processPlayerAfterGameStart(final Player player) {
        this.validateNull(player, "player");
        this.getMode().processPlayerAfterGameEnd(player);
    }

    @Shortcut
    public void processPlayerDeath(final Player player, @Nullable Player killer) {
        this.validateNull(player, "player who died");
        this.getMode().processPlayerDeath(player, killer);
    }

    @Shortcut
    public boolean checkCondition() {
        return this.getMode().getWinCondition().validate(GameManager.current());
    }

    private void validateNull(Object obj, String excepted) {
        if (obj == null) {
            throw new NullPointerException(excepted + " annotated as @NotNull but was null!");
        }
    }
}
