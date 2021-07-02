package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;

public class Test implements GameEffect {

    @Override
    public void onEffectStart(Player player, Object... extra) {
        player.sendMessage("This is a test effect yay!");
    }

    @Override
    public void onEffectStop(Player player) {
        player.sendMessage("The test effect has gone!");
    }

    @Override
    public long getTaskDelay() {
        return 5;
    }

    @Override
    public void affectTask(Player player) {
        player.sendMessage("Hello this runs every 5 times until the effect is over..");
    }

    @Override
    public String getEffectName() {
        return "Test effect";
    }
}
