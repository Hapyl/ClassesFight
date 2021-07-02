package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class SmokeBomb extends Ability {
    public SmokeBomb() {
        super("Smoke Bomb", "Instantly throw a smoke bomb at your current location and become invisible. Players inside smoke range will have their vision disturbed.");
        this.setCooldownSec(20);
        this.setItem(Material.INK_SAC);
    }

    @Override
    public Response useAbility(Player player) {

        final Location location = player.getLocation();
        GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, 120);
        PlayerLib.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);
        GameTask.runTaskTimerTimes((task, time) -> {

            if (time % 20 == 0) {
                PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 20, 1, 0, 1, 0.0f);
                GameUtils.getPlayerInRange(location, 2).forEach(range -> {
                    PlayerLib.addEffect(range, PotionEffectType.BLINDNESS, 20, 1);
                });
            }

        }, 1, 120);

        return Response.OK;
    }

}
