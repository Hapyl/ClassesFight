package ru.hapyl.classesfight.minigame;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.games.Chronomatron;
import ru.hapyl.classesfight.minigame.games.Superpairs;
import ru.hapyl.classesfight.minigame.games.TicTacToe;
import ru.hapyl.classesfight.minigame.games.Ultrasequencer;

public enum Minigame {

    ULTRA_SEQUENCER(new Ultrasequencer()),
    SUPER_PAIRS(new Superpairs()),
    CHRONOMATRON(new Chronomatron()),
    TIC_TAC_TOE(new TicTacToe());

    private final IMinigame gameClass;

    Minigame(IMinigame gameClass) {
        this.gameClass = gameClass;
    }

    public IMinigame newInstance(Player player, boolean debug) {
        MinigameManager.current().startGame(this.gameClass, player, debug);
        return this.gameClass;
    }

    public IMinigame getGame() {
        return gameClass;
    }

    public String getName() {
        return this.gameClass.getName();
    }

}
