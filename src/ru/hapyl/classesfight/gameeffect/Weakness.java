package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;

public class Weakness implements GameEffect {
    @Override
    public void onEffectStart(Player player, Object... extra) {

    }

    @Override
    public void onEffectStop(Player player) {

    }

    @Override
    public String getEffectName() {
        return "Weakness";
    }
}
