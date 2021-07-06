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

package ru.hapyl.classesfight.tablist;

import io.netty.util.internal.ConcurrentSet;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.cosmetics.Prefix;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.ActiveGameEffect;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Tablist implements Runnable {

    private final JavaPlugin plugin;

    public Tablist(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0, 20);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void run() {

        this.updatePlayers();
        final String staticHeader = Chat.format("\n&aYou're playing ClassesFight!\n&ediscord.me/honorelondo\n");

        Bukkit.getOnlinePlayers().forEach(player -> {
            final StringBuilder builder = new StringBuilder();
            final ConcurrentSet<ActiveGameEffect> activeEffects = GameEffectManager.getActiveEffects(player);

            // Show effect in game and activities in lobby
            if (!GameManager.current().isGameInProgress()) {
                builder.append("\n&a&lActivities:\n");

                for (int i = 0; i < Activity.values().length; i++) {
                    final Activity value = Activity.values()[i];
                    if (!value.isEnabled(player)) {
                        continue;
                    }
                    if (i != 0) {
                        builder.append("\n");
                    }
                    builder.append(String.format("&7%s\n", value.getName()));
                    builder.append("&8").append(value.getAbout(player));
                    builder.append("\n");

                }

            }
            else {
                builder.append("\n&a&lActive Effects:\n");
                if (activeEffects == null || activeEffects.isEmpty()) {
                    builder.append("&8None!\n");
                    player.setPlayerListHeaderFooter(staticHeader, Chat.format(builder.toString()));
                    return;
                }
                for (ActiveGameEffect effect : activeEffects) {
                    final String extra = effect.getEffect().getExtraInfo(player);
                    builder.append(Chat.format("%s%s &f- %s%s",
                            effect.getType().isPositive() ? "&a" : "&c",
                            Chat.capitalize(effect.getType()),
                            new SimpleDateFormat("mm:ss").format(effect.getTicksRemaining() * 50),
                            extra.isEmpty() ? "" : " (" + extra + ")"));
                    builder.append("\n");
                }
            }

            player.setPlayerListHeaderFooter(staticHeader, Chat.format(builder.toString()));
        });
    }

    private final List<Object> entries = new ArrayList<>();

    private void updatePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> player.setPlayerListName(formatPlayerName(player)));
    }

    private String formatPlayerName(Player player) {

        String name;
        final boolean isSpectator = Spectator.isSpectator(player);
        final ClassManager playerClass = ClassManager.getClass(player);

        if (isSpectator) {
            name = "&7[&f&lSpectator&7]";
        }
        else {
            name = "&6[" + playerClass.getDisplayName(ChatColor.WHITE, ChatColor.BOLD) + "&6]";
        }

        if (!GameManager.current().isGameInProgress()) {
            final EnumEffect prefix = Database.getDatabase(player).getCosmetics().getCurrentEffect(EnumEffect.Type.PREFIX);
            if (prefix != null && prefix.isEnabled(player)) {
                name = name + " " + (((Prefix)prefix.getClazz()).getPrefix());
            }
        }

        name = name + (player.isOp() ? " &c" : " &e") + player.getName();

        if (GameManager.current().isGameInProgress() && !isSpectator) {
            final int points = ClassUltimate.getPoints(player);
            final int pointsNeeded = ClassUltimate.getPointsNeeded(player);
            final boolean charged = ClassUltimate.isUltimateCharged(player);
            final boolean isUsingUltimate = playerClass.getTheClass().isUsingUltimate(player);
            name = name + " &8| " + (isUsingUltimate ? "&a&lIN USE" : (charged ? "&b&lREADY" : String.format("&b%s&7/&b%s", points, pointsNeeded)));
        }

        return Chat.format(name.substring(0, Math.min(name.length(), 64)));

    }

}
