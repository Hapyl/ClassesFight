package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.chat.Gradient;
import kz.hapyl.spigotutils.module.chat.gradient.Interpolators;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.extra.Grimmore;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.Map;

public class Librarian extends IClass implements Listener {

	private final Map<Player, Grimmore> data;
	private final ItemStack weaponNormal = new CFItemBuilder(Material.NETHERITE_SHOVEL, "lib_staff_0")
			.addClickEvent(this::useStaff)
			.setName("&aStaff")
			.applyDefaultSettings(false)
			.setPureDamage(5.0d)
			.build();
	private final ItemStack weaponInfused = new CFItemBuilder(Material.NETHERITE_SHOVEL, "lib_staff_1")
			.addClickEvent(this::useStaff)
			.setName("&aInfused Staff")
			.setPureDamage(7.5d)
			.glow()
			.applyDefaultSettings(false)
			.build();

	public Librarian() {
		super("Librarian of Void", Material.BOOK);

		this.data = new HashMap<>();
		this.setInfo("A librarian", "Void of Blindness", "Create massive void of blindness field. Everyone who dares steps inside, will be affected by paranoia and glow. Librarian also gets a damage and speed boost.", 8);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg4YjFjZDk1NzQ2NzJlOGUzMjYyZjIxMGMwZGRkYmMwODJlYTc1NjllOGU3MGYwYzA3YjRiZWU3NWUzMmY2MiJ9fX0=");
		eq.setChestplate(47, 32, 40);
		eq.setLeggings(Material.NETHERITE_LEGGINGS);
		eq.setBoots(84, 37, 62);

		eq.addItem(weaponNormal);

	}

	private void useStaff(Player player) {
		if (player.hasCooldown(Material.NETHERITE_SHOVEL)) {
			return;
		}

		final Player target = GameUtils.getTargetPlayer(player, 20.0d, 0.5d);
		if (target == null) {
			Chat.sendMessage(player, "&cNo target to teleport!");
			return;
		}

		final Location location = player.getLocation();
		final Location targetLocation = target.getLocation();
		final float yaw = targetLocation.getYaw();
		final float pitch = targetLocation.getPitch();
		BukkitUtils.mergePitchYaw(location, targetLocation);
		location.setYaw(yaw);
		location.setPitch(pitch);
		player.teleport(targetLocation);
		target.teleport(location);

		PlayerLib.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f);
		PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f);

		player.setCooldown(Material.NETHERITE_SHOVEL, 13 * 20);

	}

	@Override
	public void onStartOnce() {
		final int levelUpPer = 45 * 20;
		new GameTask() {
			private int tick = 3;

			@Override
			public void run() {

				// or infinite idk yet!
				if (tick-- <= 0) {
					this.cancel();
					return;
				}

				ClassManager.getPlayersInClass(ClassManager.LIBRARIAN).forEach(Librarian.this::levelUpGrimmore);

			}
		}.runTaskTimer(levelUpPer, levelUpPer);
	}

	@Override
	public void onStart(Player player) {
		data.put(player, new Grimmore(player));
		giveGrimmoreBook(player);
		GrimmoreBook.applyCooldown(player, 10 * 20);
	}

	@Override
	public void onStopOnce() {
		data.clear();
	}

	@Override
	public void onStop(Player player) {
	}

	public void giveGrimmoreBook(Player player) {
		player.getInventory().setItem(1, data.get(player).getBook().getStack());
	}

	public void useGrimmore(Player player) {
		if (player.hasCooldown(Material.BOOK)) {
			return;
		}
		this.data.get(player).usedNow();
		GrimmoreBook.applyCooldown(player, 10000);
		Abilities.BLACK_HOLE.addItemIfExists(player);
		Abilities.ENTITY_DARKNESS.addItemIfExists(player);
		Abilities.LIBRARIAN_SHIELD.addItemIfExists(player);
		Abilities.WEAPON_DARKNESS.addItemIfExists(player);
		player.updateInventory();
	}

	public void levelUpGrimmore(Player player) {
		final Grimmore data = this.data.get(player);
		if (data == null || data.getBook().isMaxed()) {
			return;
		}
		PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
		Chat.sendTitle(player, " ", grimoireLevelUpGradient, 5, 15, 5);
		//Chat.sendTitle(player, " ", "&aGrimoire Level Up!", 5, 15, 5);
		data.nextBook();
		giveGrimmoreBook(player);
	}

	private final String grimoireLevelUpGradient = new Gradient("Grimoire Level Up!").makeBold()
			.rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

	public double calculateDamage(Player player, double initDamage) {
		final int lvl = getBookLevel(player);
		return initDamage + (lvl * 2.0d);
	}

	public void removeSpellItems(Player player, Abilities ability) {
		int current = indexOf(ability);
		for (int i = 2; i <= 5; i++) {
			GameUtils.removeItem(player, i, current == i ? 1 : 64);
		}
	}

	public int getBookLevel(Player player) {
		return data.get(player).getUsedAtLvl();
	}

	private int indexOf(Abilities ability) {
		return switch (ability) {
			default -> 2;
			case ENTITY_DARKNESS -> 3;
			case LIBRARIAN_SHIELD -> 4;
			case WEAPON_DARKNESS -> 5;
		};
	}

	@EventHandler()
	public void handleClick(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		if (GameUtils.isValidPlayer(player, ClassManager.LIBRARIAN)) {
			if (player.getInventory().getHeldItemSlot() == 1) {
				if (GrimmoreBook.hasCooldown(player)) {
					Chat.sendMessage(player, "&cAbility on cooldown for %ss.", BukkitUtils.roundTick(player.getCooldown(Material.BOOK)));
					return;
				}
				useGrimmore(player);
			}
		}
	}

	@Override
	public void useUltimate(Player player) {
		final Location castLocation = player.getLocation().add(0.0d, 0.5d, 0.0d);
		this.setUsingUltimate(player, true, 12 * 20);
		new GameTask() {
			private int tick = 12 * 20;

			@Override
			public void run() {
				if ((tick -= 10) <= 0) {
					this.cancel();
					return;
				}

				PlayerLib.spawnParticle(castLocation, Particle.SQUID_INK, 50, 5, 0.5, 5, 0.05f);
				PlayerLib.spawnParticle(castLocation, Particle.SQUID_INK, 10, 5, 0.5, 5, 2.0f);
				GameUtils.getPlayerInRange(castLocation, 20).forEach(target -> {
					PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 40, 1);
					if (target == player) {
						PlayerLib.addEffect(target, PotionEffectType.SPEED, 20, 1);
						PlayerLib.addEffect(target, PotionEffectType.INCREASE_DAMAGE, 20, 1);
					}
					else {
						PlayerLib.addEffect(target, PotionEffectType.GLOWING, 20, 1);
					}
				});

			}
		}.runTaskTimer(0, 10);
	}

	public void infuseWeapon(Player player, boolean b) {
		if (Spectator.isSpectator(player)) {
			return;
		}
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(0, b ? weaponInfused : weaponNormal);
		inventory.setHeldItemSlot(0);
		PlayerLib.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.25f);
	}
}
