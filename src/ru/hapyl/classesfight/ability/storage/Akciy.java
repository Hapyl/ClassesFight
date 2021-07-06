/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.GameUtils;

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
