package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class AngelFriend extends DummyFriend {

    public AngelFriend() {
        super("Angel", "", false);
    }

    @Override
    public void affectTick(Player player, HumanNPC friend) {
        PlayerLib.spawnParticle(friend.getLocation(), Particle.VILLAGER_HAPPY, 10, 0.113, 0.412, 0.113, 0.01f);
    }
}
