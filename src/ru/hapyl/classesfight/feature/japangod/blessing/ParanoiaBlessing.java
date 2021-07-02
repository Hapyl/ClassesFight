package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class ParanoiaBlessing implements Blessing {

    @Override
    public void bless(Player player) {
        GameEffectManager.applyEffect(player, GameEffectType.PARANOIA, 10 * 20);
    }

    @Override
    public String message() {
        return "You will have problems seeing things...";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.AMBIENT_CAVE, 2);
    }
}
