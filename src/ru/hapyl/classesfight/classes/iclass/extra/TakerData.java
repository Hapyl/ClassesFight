package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

import java.util.ArrayList;
import java.util.List;

public class TakerData {

	private final int maxBones = 4;

	private final Player player;
	private final List<GarbageEntity<ArmorStand>> entities;
	private int boneCharges;

	public TakerData(Player player) {
		this.player = player;
		this.entities = new ArrayList<>();
		this.boneCharges = 0;
		new GameTask() {

			private float theta = 0.0f;
			private float yaw = 0.0f;

			@Override
			public void run() {

				final float offset = (float)((Math.PI * 2) / Math.max(entities.size(), 1));

				if (entities.isEmpty()) {
					return;
				}

				if (Spectator.isSpectator(player)) {
					resetBones();
					this.cancel();
					return;
				}

				final Location location = player.getLocation();
				location.setYaw(yaw += 3.0f);
				location.setPitch(0.0f);

				int pos = 1;
				for (GarbageEntity<ArmorStand> garbageEntity : entities) {
					final Entity entity = garbageEntity.getEntity();

					final double x = Math.sin(theta + (offset * pos));
					final double z = Math.cos(theta + (offset * pos));
					final double y = -0.25d;

					location.add(x, y, z);
					location.setYaw(location.getYaw() + 1.0f);
					entity.teleport(location);
					location.subtract(x, y, z);
					++pos;
				}

				theta += 0.1f;
				if (theta > (Math.PI * 2)) {
					theta = 0;
				}

			}
		}.runTaskTimer(0, 1);
	}

	public void removeBone() {
		if (!this.entities.isEmpty()) {
			--this.boneCharges;
			final GarbageEntity<ArmorStand> ge = this.entities.remove(this.entities.size() - 1);
			poof(ge);
		}
	}

	private void poof(GarbageEntity<ArmorStand> ge) {
		PlayerLib.spawnParticle(ge.getEntity().getLocation().add(0.0d, 1.25d, 0.0d), Particle.EXPLOSION_NORMAL, 5, 0.2d, 0.2d, 0.2d, 0.015f);
		ge.kill();
	}

	public int getMaxBones() {
		return maxBones;
	}

	public void addBone() {
		if (this.entities.size() <= maxBones) {
			final GarbageEntity<ArmorStand> gar = new GarbageEntity<>(ArmorStand.class);
			this.entities.add(gar);
			gar.spawn(player.getLocation(), me -> {
				me.setInvisible(true);
				me.setMarker(true);
				me.setSilent(true);
				me.getEquipment().setHelmet(new ItemStack(Material.BONE));
			});
			++this.boneCharges;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public int getBoneCharges() {
		return boneCharges;
	}

	public void setBoneCharges(int boneCharges) {
		this.boneCharges = boneCharges;
	}

	public void resetBones() {
		this.boneCharges = 0;
		for (GarbageEntity<ArmorStand> entity : this.entities) {
			poof(entity);
		}
		this.entities.clear();
	}
}
