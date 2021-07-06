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

package ru.hapyl.classesfight.feature.npc;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.reflect.npc.ClickType;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.quest.eye.TheEyeMenu;
import ru.hapyl.classesfight.stats.PlayerInfo;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashMap;
import java.util.Map;

public class NpcManager {

	private NpcManager() {
	}

	private static final NpcManager manager = new NpcManager();

	public static NpcManager getManager() {
		return manager;
	}

	private final World defaultWorld = Bukkit.getWorlds().get(0);

	private final Location spawnLocation = new Location(defaultWorld, -14.0, 14.0, 0.5, -90, 0);
	private final Location eyeLocation = new Location(defaultWorld, 28.5, 16.1, 0.5, 90, 0);
	private final Location hypixelLocation = new Location(defaultWorld, 10, 16, 24, 102.5f, 0.0f);

	//    private final Location hapylLocation = new Location(defaultWorld, -14.0, 13.5, 5.0, -90, 0);
	//    private final Location didenLocation = new Location(defaultWorld, -14.0, 13.5, -4.0, -90, 0);
	//    private final Location dirtyLocation = new Location(defaultWorld, -13.0, 13.5, 8.0, -90, 0);
	//    private final Location sdimasLocation = new Location(defaultWorld, -13.0, 13.5, -7.0, -90, 0);

	private final Map<Player, HumanNPC> playerNpc = new HashMap<>();
	private final Map<Player, PlayerInfo> playerInfo = new HashMap<>();

	private final HumanNPC[] globalNPCs = new HumanNPC[2];

	public void spawnNpc(Player player) {
		//-14.0 14 0.5

		this.updatePlayerInfo(player);

		playerNpc.put(player,
				new HumanNPC(spawnLocation, "&e&lCLICK", player.getName())
						//.setPrefix("The Omniscient")
						//.addDialogSoundEntry("I'm seems to be broken right now... but you can still see your stats!", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0)
						//                        .addDialogSoundEntry(String.format("Your best class is %s&a! You killed %s enemies, won %s times and died only %s times!",
						//                                info.getBest().getDisplayName(),
						//                                info.getKills(),
						//                                info.getWins(),
						//                                info.getDeaths()),
						//                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0)
						//                        .addDialogSoundEntry("Check it for yourself!", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 20)
						//                        .addDialogSoundEntry("Yeah, here they are...", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 20)
						//                        .addClickAction(InfoMenu::new, 0)
						.setLookAtCloseDist(5)
						.addTextAboveHead("&aThe Omniscient"));

		playerNpc.get(player).show(player);
		showGlobalNPCs(player);

	}

	public void updatePlayerInfo(Player player) {
		this.playerInfo.put(player, new PlayerInfo(player));
	}

	public PlayerInfo getPlayerInfo(Player player) {
		return playerInfo.getOrDefault(player, new PlayerInfo(player));
	}

	public void createGlobalStuff() {

		globalNPCs[0] = new HumanNPC(eyeLocation, "&e&lCLICK") {
			@Override
			public void onClick(Player player, HumanNPC npc, ClickType clickType) {
				new TheEyeMenu(player).openInventory();
			}
		}.addTextAboveHead("&aThe Eye")
				.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYxMzMwMjM5NTg0NSwKICAicHJvZmlsZUlkIiA6ICI4NDMwMDNlM2JlNTY0M2Q5OTQxMTBkMzJhMzU2MTk2MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHYWJvTWNHYW1lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODhiMWNkOTU3NDY3MmU4ZTMyNjJmMjEwYzBkZGRiYzA4MmVhNzU2OWU4ZTcwZjBjMDdiNGJlZTc1ZTMyZjYyIgogICAgfQogIH0KfQ==", "IfwCAwsmxYOgutDZaDi6mjsp5Hzl0oV6zXyYWl6iNDFyEXZisKbYIDHbw/Vua4T8FD2gwWHtM4nTduvRk2DRcmLubkpUmmJ+t/9/6oEa9N5VfG6veAA436fSnfhEl+F/MR2gTQFz7nJb/S0E/WmZGcMr7deGL61tdsFVVKwJOHboM/fokGWpfhMG7LRY5uI9S4CIC0f0sKiFRtDC0fXGNKpkumMYn8t6oCjpQnvESYRVV8AD6Ap8s2ajTRYM/OhFJEulTIXP8N70bD8qClpEpbL4RuC0fuEEuoGSIWvQ4PwFc5uSnT1WnVPMreSD8P5XEaxiNRlqHReTQ6Bz9XuYc7uMfYn+DneWZSBf4Y3SmfnSNtax7W+e1CXxMCDMQHFtAxoaNmmQ6lWKSEFSiGEAPILdM7Nq0HaM6ITgUW2TlSt+k82hh4jwp6C+G2y5+h5S55eLzQ5ERbjvvDZuHRLr0izlz/kszVdtcAGKxTGrHbWs+pYboTWrLEXiTJLYS5dUar3/TeCf6uh2WyeOUqpgbCwrYLKzbxXxYNKTGcbv7Sva72YodKH7uvl6uCp17/n0+PGV2ymzxzVb9w/oMlrUzz3QdfA9capV7jIDSrItnu0+AFL7O80LDTGTKUdFiIQDuaOKFh11DW8bGzJtRPPeD4RDvE3kiy4cvgZCh/0xQUg=");
		//.addClickEvent((player) -> new TheEyeMenu(player).openInventory());
                /*.addDialogLine("Hello, {PLAYER}!", 0)
                .addDialogLine("Unfortunately I can't help you right now, I'm still trying to see.", 20)
                .addDialogLine("See the truth, sometimes it can be hard...", 30);*/

		globalNPCs[1] = new HumanNPC(hypixelLocation, "", "hypixel").setPose(NPCPose.CROUCHING);
		//.setPrefix("???")
		//.addDialogLine("You can see this sparky thing, can't you?", 0)
		//.addDialogLine("I've been staring at it for at least 3-5 business days.", 30);

	}

	public void showGlobalNPCs(Player player) {
		for (HumanNPC globalNPC : globalNPCs) {
			if (globalNPC != null) {
				globalNPC.show(player);
			}
		}
		if (playerNpc.get(player) != null) {
			playerNpc.get(player).show(player);
		}
	}

	public void hideGlobalNPCs(Player player) {
		for (HumanNPC globalNPC : globalNPCs) {
			// Use #remove, not #hide
			if (globalNPC != null) {
				globalNPC.hide(player);
			}
		}
		if (playerNpc.get(player) != null) {
			playerNpc.get(player).hide(player);
		}
	}

	private void sendMessageAndSound(Player player, String message, Sound sound, float pitch) {
		Chat.sendMessage(player, "&a&l[NPC] &7" + message);
		OldPlayerLib.playSound(player, sound, pitch);
	}

	public void updateGlobalNPCs(Player player) {
		this.hideGlobalNPCs(player);
		this.showGlobalNPCs(player);
	}
}
