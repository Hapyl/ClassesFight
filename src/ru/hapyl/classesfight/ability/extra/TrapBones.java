package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TakerClass;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

import java.util.HashSet;
import java.util.Set;

public class TrapBones {

    private final Player player;
    private final Location location;
    private final Set<GarbageEntity<ArmorStand>> stands;

    public TrapBones(Player player, Location location) {
        this.player = player;
        this.location = location;
        this.location.setYaw((float)(ThreadRandom.nextFloat() * (Math.PI * 2f)));
        this.stands = new HashSet<>();
        this.spawnStands();
    }

    private final float[][] offsets = {
            {-0.057819f, -0.77205f, -0.024552f},
            {0.048929f, -0.68791f, -0.080905f},
            {-0.01652f, -0.76746f, 0.03125f},
            {0.0514712f, -0.79218f, -0.4375f}
    };

    private final float[][] headPositions = {
            {10f, 40f, 5f},
            {25f, 165f, -10f},
            {0f, -10f, 15f},
            {0f, 10f, 65f}
    };

    private void spawnStands() {
        for (int i = 0; i < offsets.length; i++) {
            final GarbageEntity<ArmorStand> entity = new GarbageEntity<>(ArmorStand.class);
            this.stands.add(entity);
            int finalI = i;
            entity.spawn(location.clone().add(offsets[i][0], offsets[i][1], offsets[i][2]), me -> {
                me.setVisible(false);
                me.setMarker(true);
                me.setSilent(true);
                me.getEquipment().setHelmet(new ItemStack(Material.BONE));
                me.setHeadPose(new EulerAngle(Math.toRadians(headPositions[finalI][0]), Math.toRadians(headPositions[finalI][1]), Math.toRadians(headPositions[finalI][2])));
            });
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public void addBoneCharge() {
        ((TakerClass)ClassManager.TAKER.getTheClass()).addBoneCharge(this.player);
    }

    public void remove() {
        this.stands.forEach(GarbageEntity::kill);
        this.stands.clear();
    }
}
