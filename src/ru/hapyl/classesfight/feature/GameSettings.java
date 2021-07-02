package ru.hapyl.classesfight.feature;

import ru.hapyl.classesfight.gamemode.GameModeEnum;

public class GameSettings {

    private boolean allowFood;
    private boolean allowEvent;
    private GameModeEnum gameMode;

    public GameSettings() {
        this.allowFood = true;
        this.allowEvent = false;
        this.gameMode = GameModeEnum.NORMAL;
    }

    public boolean isAllowFood() {
        return allowFood;
    }

    public void setAllowFood(boolean allowFood) {
        this.allowFood = allowFood;
    }

    public boolean isAllowEvent() {
        return allowEvent;
    }

    public void setAllowEvent(boolean allowEvent) {
        this.allowEvent = allowEvent;
    }

    public GameModeEnum getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameModeEnum gameMode) {
        this.gameMode = gameMode;
    }
}
