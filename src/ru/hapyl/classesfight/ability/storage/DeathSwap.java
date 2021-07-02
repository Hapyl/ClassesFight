package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TakerClass;
import ru.hapyl.classesfight.classes.iclass.extra.TakerData;
import ru.hapyl.classesfight.utils.GameUtils;

public class DeathSwap extends Ability {
    public DeathSwap() {
        super("Death Swap", "Instantly consume one Spiritual Bone to swap your location with target player.");
        this.setItem(Material.SKELETON_SKULL);
        this.setCooldownSec(20);
    }

    @Override
    public Response useAbility(Player player) {

        final TakerData data = ((TakerClass)ClassManager.TAKER.getTheClass()).getData(player);

        if (data.getBoneCharges() <= 0) {
            return Response.error("Not enough Spiritual Bones!");
        }

        final Player targetPlayer = GameUtils.getTargetPlayer(player, 25, 0.5d);

        if (targetPlayer == null) {
            return Response.error("Not valid target!");
        }

        data.removeBone();

        final Location location = player.getLocation();
        final Location targetLocation = targetPlayer.getLocation();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();
        location.setYaw(targetLocation.getYaw());
        location.setPitch(targetLocation.getPitch());
        targetLocation.setYaw(yaw);
        targetLocation.setPitch(pitch);

        player.teleport(targetLocation);
        targetPlayer.teleport(location);

        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 0.75f);
        PlayerLib.playSound(targetPlayer, Sound.ENTITY_SKELETON_AMBIENT, 0.75f);
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
        PlayerLib.playSound(targetPlayer, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);

        return Response.OK;
    }
}
