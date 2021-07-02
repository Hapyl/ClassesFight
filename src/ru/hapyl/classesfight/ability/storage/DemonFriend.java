package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.entity.Player;

public class DemonFriend extends DummyFriend {
	public DemonFriend() {
		super("Demon", "Spawns a demon.", true);
	}

	@Override
	public void affectTick(Player player, HumanNPC friend) {

	}
}
