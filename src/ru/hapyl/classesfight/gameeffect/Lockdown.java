package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashMap;
import java.util.Map;

public class Lockdown implements GameEffect {

	private final Map<Player, OldData> oldData = new HashMap<>();

	@Override
	public void onEffectStart(Player player, Object... extra) {

		// if effect didn't end and we're still overriding than apply old data
		if (oldData.containsKey(player)) {
			applyOldData(player);
		}

		oldData.put(player, new OldData(player.isFlying(), player.getAllowFlight(), player.getFlySpeed()));

		// prevent kick
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.0f);

		ClassManager.getClass(player).getTheClass().onLockdown(player);

		//if (ClassManager.getClass(player) == ClassManager.VAMPIRE) {
		//    ((VampireClass)ClassManager.VAMPIRE.getTheClass()).transformPlayer(player, VampireClass.Transform.HUMAN, true);
		//}

		// This slow is just for fx, since it's looks cooler and indicated whenever you affected
		OldPlayerLib.addPotionEffect(player, PotionEffectType.SLOW, 165, 100);
		OldPlayerLib.addPotionEffect(player, PotionEffectType.WEAKNESS, 165, 100);
		OldPlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.75f);

		// Changed from 8 slot to 7 because of japan relics
		player.getInventory().setHeldItemSlot(7);
	}

	@Override
	public void onEffectStop(Player player) {

		applyOldData(player);

		//if (ClassManager.getClass(player) == ClassManager.VAMPIRE) {
		//	player.setAllowFlight(true);
		//}

		OldPlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2);
	}

	private void applyOldData(Player player) {
		// Should contain!
		if (oldData.containsKey(player)) {
			final OldData data = this.oldData.remove(player);
			player.setFlying(data.isWasFlying());
			player.setAllowFlight(data.isWasAllowedToFly());
			player.setFlySpeed(data.getFlightSpeed());
		}
	}

	@Override
	public String getEffectName() {
		return "Lockdown";
	}

	@Override
	public long getTaskDelay() {
		return 50;
	}

	@Override
	public void affectTask(Player player) {
		player.getWorld().spawnParticle(Particle.BARRIER, player.getLocation().clone().add(0, 2, 0), 1, 0, 0, 0, 0);
	}

	private static class OldData {

		private final boolean wasFlying;
		private final boolean wasAllowedToFly;
		private final float flightSpeed;

		public OldData(boolean wasFlying, boolean wasAllowedToFly, float flightSpeed) {
			this.wasFlying = wasFlying;
			this.wasAllowedToFly = wasAllowedToFly;
			this.flightSpeed = flightSpeed;
		}

		public float getFlightSpeed() {
			return flightSpeed;
		}

		public boolean isWasFlying() {
			return wasFlying;
		}

		public boolean isWasAllowedToFly() {
			return wasAllowedToFly;
		}
	}

}
