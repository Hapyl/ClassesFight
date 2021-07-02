package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.NPCPose;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.extra.BodyData;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.Map;

public class HealerClass extends IClass implements Listener {

	private final Map<Player, BodyData> body = Maps.newConcurrentMap();

	public HealerClass() {
		super("Healer", Material.APPLE);

		this.setHealth(EnumInfo.HIGH);
		this.setAttack(EnumInfo.LOW);
		this.setRole(ClassRole.MELEE);
		this.setRating(ClassRating.C);
		this.setInfo("Very healthy warrior with a bunch of healing! And a couple of &6Golden Apples &7just in case if he'll get hungry.", "Big Heal", "Instantly heals for &c10❤&7.", 6);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet(Material.GOLDEN_HELMET);
		eq.setChestplate(Material.GOLDEN_CHESTPLATE);
		eq.setLeggings(255, 163, 26);
		eq.setBoots(255, 184, 77);

		eq.addItem(new CFItemBuilder(Material.GOLDEN_SHOVEL).setName("&aSpoon").setPureDamage(2.0).applyDefaultSettings(false).build());
		eq.addItem(new CFItemBuilder(Material.GOLDEN_APPLE, "healer_gapple").setName("&aGolden Apple")
				.setAmount(2)
				.addClickEvent(this::eatApple)
				.withCooldown(20)
				.build());

	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		if (event.getDamage() >= DamageFeature.getHealth(player) && isUsingUltimate(player)) {
			createBody(player);
			event.setCancelled(true);
		}
	}

	private void createBody(Player player) {
		// Manage Player
		GameUtils.hidePlayer(player);
		player.setGameMode(GameMode.SPECTATOR);
		DamageFeature.setHealth(player, DamageFeature.getMaxHealth(player));

		// Manage NPC
		final HumanNPC npc = new HumanNPC(player.getLocation(), "", player.getName());
		npc.setPose(NPCPose.SLEEPING);
		npc.showAll();
		final BodyData data = new BodyData(player, npc);
		body.put(player, data);

		new GameTask() {
			private int reviveTime = 240;

			private void remove() {
				data.remove();
				body.remove(player);
			}

			@Override
			public void run() {
				if (data.getHealth() <= 0) {
					this.remove();
					DamageFeature.fakeDeath(player);
					this.cancel();
					return;
				}

				if (reviveTime-- <= 0) {
					this.remove();
					player.setGameMode(GameMode.SURVIVAL);
					GameUtils.showPlayer(player);
					player.teleport(npc.getLocation());
					Chat.sendTitle(player, "&2❣", "&aRevived!", 5, 20, 5);
					this.cancel();
					return;
				}

				final Location clonedLocation = npc.getLocation().clone();
				final Location location = clonedLocation.add(clonedLocation.getDirection().multiply(-2).setY(0.0d)).add(0.0d, 2.0d, 0.0);
				location.setYaw(player.getLocation().getYaw());
				location.setPitch(35f);
				player.teleport(location);
				Chat.sendTitle(player, "&c&lKNOCKED OUT", String.format("&eRevive in %ss...", BukkitUtils.roundTick(reviveTime)), 0, 10, 5);

			}
		}.runTaskTimer(0, 1);
	}

	@EventHandler()
	public void handleBodyDamage(PlayerInteractEvent ev) {
		if (ev.getHand() != EquipmentSlot.OFF_HAND && (ev.getAction() == Action.LEFT_CLICK_AIR || ev.getAction() == Action.LEFT_CLICK_BLOCK)) {
			final Player player = ev.getPlayer();
			final Location clickedLocation = ev.getClickedBlock() == null ? player.getLocation() : ev.getClickedBlock().getLocation();
			for (final Player body : body.keySet()) {
				if (body == player) {
					continue;
				}
				final BodyData data = this.body.get(body);
				if (data.getNpc().getLocation().distance(clickedLocation) <= 3.0d) {
					data.damageIfCan();
				}
			}
		}
	}

	private void eatApple(Player player) {
		final ItemStack main = player.getInventory().getItemInMainHand();
		if (main.getType() != Material.GOLDEN_APPLE) {
			player.sendMessage("Trying to use golden apple heal with " + main.getType() + "?");
			return;
		}
		main.setAmount(main.getAmount() - 1);
		OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_BURP, 0.95f);
		DamageFeature.addHealth(player, 3.0);
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, 10 * 20);
	}

}