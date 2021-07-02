package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.locaiton.LocationHelper;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashMap;
import java.util.Map;

public abstract class DummyFriend extends Ability {

    private static final Map<Player, HumanNPC> friends = new HashMap<>();

    private final int lifeTime;
    private final boolean left;

    public DummyFriend(String name, String info, boolean left) {
        super("Summon " + name, info);
        this.setItem(Material.LEATHER);
        this.lifeTime = 200;
        this.left = left;
        this.setCooldown(this.lifeTime * 2);
        this.setCastMessage("Come to life!", "Appear!");
    }

    public abstract void affectTick(Player player, HumanNPC friend);

    private void disappear(Player player, HumanNPC friend) {
        friends.remove(player);
        friend.remove();
    }

    @Override
    public void onStop() {
        friends.clear();
    }

    @Override
    public Response useAbility(Player player) {

        if (hasFriend(player)) {
            return Response.error("Cannot use it at the moment!");
        }

        final HumanNPC npc = new HumanNPC(getLocation(player), "", player.getName());
        npc.showAll();
        npc.setFarAwayDist(100);
        friends.put(player, npc);

        GameTask.runTaskTimerTimes((task, time) -> {
            npc.setLocation(getLocation(player));
            npc.setPose(player.isSneaking() ? NPCPose.CROUCHING : NPCPose.STANDING);

            if (time % 20 == 0) {
                affectTick(player, npc);
            }

            if (time == 0) {
                disappear(player, npc);
            }

        }, 1, lifeTime).addCancelEvent(() -> this.disappear(player, npc));

        return Response.OK;
    }

    private Location getLocation(Player player) {
        final Vector location = this.left ? LocationHelper.getToTheLeft(player.getLocation(), 1.5d) : LocationHelper.getToTheRight(player.getLocation(), 1.5d);
        return player.getLocation().add(location);
    }

    public boolean hasFriend(Player player) {
        return friends.containsKey(player);
    }

}
