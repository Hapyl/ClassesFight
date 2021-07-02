package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.Collection;

public class Akciy extends Ability {

    public Akciy() {
        super("Axii", "Stuns a target you looking at for 5 seconds or until they get hit. Stunned enemies cannot move nor use their abilities.");
        this.setCooldownSec(60);
        this.setItem(Material.SLIME_BALL);
    }

    @Override
    public Response useAbility(Player player) {
        final Player target = GameUtils.getTargetPlayer(player, 50.0d, 0.5d);

        if (target == null) {
            return Response.error("No valid target!");
        }

        GameEffectManager.applyEffect(target, GameEffectType.STUN, 100);
        Chat.sendMessage(player, "&aStunned %s!", target.getName());

        return Response.OK;
    }



}
