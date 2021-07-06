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

package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashMap;
import java.util.Map;

public class RPSPlayerInfo {

    private final GameTask task;
    private final Player owner;
    private final Map<Player, ArmorStand[]> pairs;
    private final Map<Player, RPSInfo> hashMap;

    public RPSPlayerInfo(Player owner) {
        this.owner = owner;
        this.hashMap = new HashMap<>();
        this.pairs = new HashMap<>();
        this.task = new GameTask() {
            @Override
            public void run() {
                // validate that this works
                if (Spectator.isSpectator(owner)) {
                    removePlayer(owner);
                    this.cancel();
                    return;
                }
                if (!pairs.isEmpty()) {
                    pairs.forEach((player, stand) -> {
                        /** unsafe call */
                        stand[0].teleport(player.getLocation().clone().add(0, 1.5, 0));
                        stand[1].teleport(player.getLocation().clone().add(0, 2.5, 0));
                        stand[1].setCustomName(ChatColor.GREEN + "" + getData(player).getCurrentHit());
                    });
                }
            }
        }.addCancelEvent(new BukkitRunnable() {
            @Override
            public void run() {
                clearTrash();
            }
        }).runTaskTimer(0, 5);
    }

    public void addPlayer(Player player) {
        final RPSInfo.RPSMaterial material = RPSInfo.RPSMaterial.random();
        this.hashMap.put(player, new RPSInfo(material));

        // create 'holo'
        // didn't implement hologram equipment yet so have to use vanilla hide impl

        final ArmorStand stand = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0), ArmorStand.class, me -> {
            me.setMarker(true);
            me.setSmall(true);
            me.getEquipment().setHelmet(material.toItemStack());
            me.setInvisible(true);
        });

        final ArmorStand name = player.getWorld().spawn(player.getLocation().clone().add(0, 2, 0), ArmorStand.class, me -> {
            me.setMarker(true);
            me.setSmall(true);
            me.setInvisible(true);
        });

        GameManager.current().forEachBut(t -> Reflect.hideEntity(stand, t), this.owner);
        GameManager.current().forEachBut(t -> Reflect.hideEntity(name, t), this.owner);
        name.setCustomNameVisible(true);

        this.pairs.put(player, new ArmorStand[]{stand, name});

    }

    public void decrementHit(Player player) {
        getData(player).decrementHit();
        if (getData(player).getCurrentHit() <= 0) {
            this.removePair(player);
        }
    }

    public void removePair(Player player) {
        for (ArmorStand stand : this.pairs.get(player)) {
            if (stand != null) {
                stand.remove();
            }
        }
        this.pairs.remove(player);
        this.removePlayer(player);
    }

    public void clearTrash() {
        this.task.cancel();
        this.pairs.forEach((a, b) -> {
            for (ArmorStand armorStand : b) {
                armorStand.remove();
            }
        });
        this.pairs.clear();
    }

    public RPSInfo getData(Player player) {
        return this.hashMap.get(player);
    }

    public void removePlayer(Player player) {
        if (!this.pairs.containsKey(player)) {
            return;
        }

        final ArmorStand[] entity = this.pairs.get(player);
        if (entity != null) {
            for (ArmorStand armorStand : entity) {
                armorStand.remove();
            }
        }
        this.pairs.remove(player);
        this.hashMap.remove(player);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RPSPlayerInfo{");
        sb.append("owner=").append(owner);
        sb.append(", hashMap=").append(hashMap);
        sb.append('}');
        return sb.toString();
    }

    public boolean hasData(Player player) {
        return this.pairs.containsKey(player);
    }
}
