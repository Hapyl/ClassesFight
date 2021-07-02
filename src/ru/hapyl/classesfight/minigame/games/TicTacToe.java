package ru.hapyl.classesfight.minigame.games;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.IMinigame;

public class TicTacToe extends IMinigame {

    public TicTacToe() {
        super("Tic Tac Toe",
                Integer.MAX_VALUE,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWFlODVmNzRmOGUyYzA1NGI3ODFhMjlmYTliMjU5MzRiYTYzYmI3OWYxZGU4YTk1YjQzNmQ5YmZkY2FmNGNkIn19fQ==",
                "Win rounds of Tic-Tac-Toe against AI to win rewards!");
    }

    @Override
    public void newInstance(Player player, boolean debug) {
        super.notYetImplemented(player);
    }
}
