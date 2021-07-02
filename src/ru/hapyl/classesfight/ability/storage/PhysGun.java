package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.Map;

public class PhysGun extends Ability {

	private final Map<Player, LivingEntity> captured;

	public PhysGun() {
		super("Physgun", "no description yet");
		this.captured = new HashMap<>();
		this.setItem(Material.DIAMOND_HORSE_ARMOR);
	}

	@Override
	public void onStop() {
		captured.clear();
	}

	@Override
	public Response useAbility(Player player) {

		// Throw
		if (captured.containsKey(player)) {
			final LivingEntity entity = this.captured.get(player);
			final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2.0d));

			this.captured.remove(player);

			entity.setVelocity(player.getLocation().getDirection().multiply(2.5d));
			PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.2, 0.05, 0.2, 0.02f);
			PlayerLib.playSound(location, Sound.ITEM_CROSSBOW_SHOOT, 0.5f);

			return Response.OK;
		}

		// Get the target entity
		final LivingEntity target = (LivingEntity)GameUtils.getTargetEntity(player, 3.0d, 0.5d, entity -> {
			if (entity instanceof LivingEntity) {
				if (entity instanceof Player) {
					return !Spectator.isSpectator((Player)entity);
				}
				return true;
			}
			return false;
		});

		if (target == null) {
			return Response.error("No valid target!");
		}

		this.captured.put(player, target);
		new GameTask() {

			@Override
			public void run() {

				if (!CFItemBuilder.compareItemsIDs(player.getInventory().getItemInMainHand(), getItem().getItem())) {
					dismountEntity(player, target);
					this.cancel();
					return;
				}

				// cancel if no more or other entity
				if ((captured.get(player) == null) || (captured.get(player) != target)) {
					dismountEntity(player, target);
					this.cancel();
					return;
				}

				final Location playerLocation = player.getLocation();
				final Location location = target.getLocation();
				Location finalLocation = playerLocation.add(0.0d, 1.0d, 0.0d).add(playerLocation.getDirection().multiply(2.0d));

				finalLocation.setYaw(location.getYaw());
				finalLocation.setPitch(location.getPitch());

				if (!finalLocation.getBlock().getType().isAir() || !finalLocation.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
					finalLocation = playerLocation;
				}

				if (target instanceof Player) {
					GameEffectManager.applyEffect((Player)target, GameEffectType.RESISTANCE, 20, true);
					Chat.sendActionbar((Player)target, "&f&lCaptured by &a%s&f&l!", player.getName());
				}
				else {
					target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 5));
				}

				target.teleport(finalLocation);
				Chat.sendActionbar(player, "&f&lCarrying &a%s", target.getName());

			}
		}.runTaskTimer(0, 1);
		return Response.OK;
	}

	private void dismountEntity(Player holder, Entity entity) {
		final Location location = entity.getLocation();
		final Block block = location.getBlock();

		if (!block.getType().isAir() || !block.getRelative(BlockFace.UP).getType().isAir()) {
			Chat.sendMessage(holder, "&a%s was teleported to your since they would suffocate.", entity.getName());
			entity.teleport(holder);
		}
		boolean solid = false;
		// check for solid ground
		for (double y = 0; y <= location.getY(); ++y) {
			if (!location.clone().subtract(0.0d, y, 0.0d).getBlock().getType().isAir()) {
				solid = true;
				break;
			}
		}

		if (!solid) {
			Chat.sendMessage(holder, "&a%s was teleported to your since they would fall into void.", entity.getName());
			entity.teleport(holder);
		}

		captured.remove(holder);

	}

}
