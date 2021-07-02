package ru.hapyl.classesfight.feature.japangod.blessing;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class GenocideBlessing implements Blessing {

    @Override
    public void bless(Player player) {
        GameManager.current().forEachInGamePlayer(it -> {
            if (it != player) {
                DamageFeature.fakeDeath(it);
                Chat.sendMessage(it, "&6&lGOD! &dI have decided to purge you by someones wishes...");
            }
        });
    }

    @Override
    public String message() {
        return "You will have mercy on you.";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_PILLAGER_CELEBRATE, 0.75f);
    }

}
