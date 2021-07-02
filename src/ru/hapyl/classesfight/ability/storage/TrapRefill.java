package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.TrapRefillData;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.entity.Entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrapRefill extends Ability implements Listener {

	private final Map<Player, TrapRefillData> data;
	private final Map<Entity, Player> loyalEntity;

	private final ItemStack ITEM_HEAD_TEXTURE = ItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM2MjQzOGZmNGVjZjhmNGEyY2FhMTI3NzU2MWM5NTEzYzlhOTg2ZGJlMzhhODBiOWJhZmNiZmVkOGIyYTljOCJ9fX0=")
			.toItemStack();

	public TrapRefill() {
		super("Toolbox", "Place a portable toolbox on the battlefield! &e&lSNEAK &7near to create new traps. But be careful, anyone can break it and you will not be able to replace your traps!");
		this.setItem(Material.CHEST, "PLACE");
		this.setRemoveItemOnUse(true);
		this.data = new ConcurrentHashMap<>();
		this.loyalEntity = new ConcurrentHashMap<>();
	}

	@Override
	public void onDeath(Player player) {
		final TrapRefillData data = this.data.get(player);
		final ArmorStand entity = data.getEntity();
		data.stopTask();
		entity.remove();
		this.data.remove(player);
		this.loyalEntity.remove(entity);
	}

	@Override
	public void onStop() {
		this.data.clear();
		this.loyalEntity.clear();
	}

	@Override
	public Response useAbility(Player player) {
		final Block block = player.getTargetBlockExact(5);
		if (block == null) {
			return Response.error("No valid block in sight!");
		}

		final Block blockUp = block.getRelative(BlockFace.UP);
		if (!blockUp.getType().isAir()) {
			return Response.error("Cannot fit!");
		}

		final Location location = blockUp.getLocation().add(0.5d, 0.0d, 0.5d);
		final Entity entity = Entities.ARMOR_STAND.spawn(location.subtract(0.0d, 1.4d, 0.0d), me -> {
			me.setMaxHealth(1000);
			me.setHealth(1000);
			me.setInvisible(true);
			me.setMarker(false);
			me.setSilent(true);
			me.setGravity(false);
			me.getEquipment().setHelmet(ITEM_HEAD_TEXTURE);
			GameUtils.lockArmorStandSlots(me);
		});

		this.data.put(player, new TrapRefillData(player, (ArmorStand)entity));
		this.loyalEntity.put(entity, player);

		return Response.OK;
	}

	@EventHandler()
	public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent ev) {
		if (!GameManager.current().isGameInProgress()) {
			return;
		}

		if (!(ev.getEntity() instanceof final ArmorStand stand)) {
			return;
		}

		if (!this.loyalEntity.containsKey(stand)) {
			return;
		}

		ev.setDamage(0.0d);
		ev.setCancelled(true);
		final Player owner = this.loyalEntity.get(stand);
		final Entity damager = ev.getDamager();
		if (damager == owner) {
			Chat.sendMessage(owner, "&cCannot remove your own toolbox!");
		}
		else {
			this.loyalEntity.remove(stand);
			this.data.remove(owner);
			stand.remove();
			Chat.sendTitle(owner, "", "&cYour Toolbox has been broken!", 10, 30, 10);
			damager.sendMessage(Chat.format("&aYou broke %s's Toolbox!", owner.getName()));
			PlayerLib.playSound(owner, Sound.ITEM_SHIELD_BREAK, 0.75f);
		}

	}

	private double distance = 0.75f;

	@EventHandler()
	public void handleSneakEvent(PlayerToggleSneakEvent ev) {
		final Player player = ev.getPlayer();
		if (GameUtils.isValidPlayer(player, ClassManager.TECHNO)) {
			final TrapRefillData data = this.data.get(player);
			if (data == null) {
				return;
			}
			final boolean isSneaking = player.isSneaking();
			if (isSneaking) {
				return;
			}
			player.getNearbyEntities(data.getDistance(), data.getDistance(), data.getDistance())
					.stream()
					.filter(entity -> entity instanceof ArmorStand && data.getEntity() == entity)
					.findFirst()
					.ifPresent(entity -> data.startTask());
		}
	}

}
