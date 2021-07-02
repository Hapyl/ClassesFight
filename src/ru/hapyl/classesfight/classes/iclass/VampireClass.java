package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.extra.VampireData;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

import java.util.HashMap;
import java.util.Map;

public class VampireClass extends IClass implements Listener {

	private final Map<Player, VampireData> playerData = new HashMap<>();
	private final Map<Entity, Player> fragileSpirit = new HashMap<>();

	private final ItemStack BLOOD = new CFItemBuilder(Material.REDSTONE, "va_blood")
			.setName("&aBlood &6&lCLICK")
			.addClickEvent(this::drinkBlood)
			.build();

	private final int BAT_KICK_CD = 40;
	private final int ULTIMATE_FORM_TIME = 200;
	private final int BLOOD_DRINK_COOLDOWN = 100;

	public VampireClass() {
		super("Vampire", Material.GHAST_TEAR);

		this.setDefense(EnumInfo.LOW);
		this.setRole(ClassRole.STRATEGIST);
		this.setRating(ClassRating.NOT_YET);

		// Reincarnated Vampire with connection to a Spirit Bat.____&e○ Transformation &e&lDOUBLE JUMP &7Instantly transform into a spirit Bat,
		// allowing you to fly but you can't deal damage.&e&lDOUBLE JUMP &7again to transform back into a human! If your spirit bat takes damage,
		// you will be forced to transform into a human and you won't be able to transform back for %ss.__&e○ Spiritual Energy &7While in Bat form,
		// your Spiritual Energy will constantly drain. If it reached 15 or less, you will be forced out of your Bat. Spiritual Energy regenerates
		// while in human form. __&e○ Vampirism &e&lPASSIVE &7Upon hitting an enemy, you will get a drop of blood, &e&lCLICK &7blood to drink to
		// heal and maybe receive some additional effects.

		this.setInfo("Reincarnated Vampire with connection to a Spirit Bat.____&e○ Transformation &e&lDOUBLE JUMP &7Instantly transform into a " +
				"spirit Bat, allowing you to fly but you can't deal damage. &e&lDOUBLE JUMP &7again to transform back into a human! If your Spirit " + "Bat takes damage, you will be forced to transform into a human and you won't be able to transform back for &b" + BukkitUtils
				.roundTick(BAT_KICK_CD) + "s&7.____&e○ Spiritual Energy &7While in Bat form, your Spiritual Energy will constantly drain. If it " +
				"reached 15 or less, you will be forced out of your Bat. Spiritual Energy regenerates while in human form. __&e○ Vampirism " + "&e" + "&lPASSIVE &7Upon hitting an enemy, you will get a drop of blood, &e&lRIGHT CLICK &7blood to heal and maybe receive some " + "additional effects.", "Ultimate Form", "Transform into a &c&lVery Angry Bat&7 for &b" + BukkitUtils
				.roundTick(ULTIMATE_FORM_TIME) + "s&7, which moves twice faster, doesn't drain energy, you can deal damage can't get kicked from it" + ".", 5);
		this.setUltimateSound(Sound.ENTITY_GHAST_HURT, 0.5f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ0NDc1NmUwYjRlY2U4ZDc0NjI5NmEzZDVlMjk3ZTE0MTVmNGJhMTc2NDdmZmUyMjgzODUzODNkMTYxYTkifX19");
		eq.setChestplate(Color.BLACK);
		eq.setLeggings(Color.BLACK);
		eq.setBoots(Color.BLACK);

		eq.addItem(new CFItemBuilder(Material.GHAST_TEAR).setName("&aFang")
				.setPureDamage(0.75d)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 999, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
				.build());
		Abilities.BATS_PACK.addItemIfExists(eq);

		//PatchNotes.CURRENT_BUILD.addPatch(ClassManager.VAMPIRE, "Fang damage reduced 1.0 -> 0.75");

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (isBat(player) && !isUsingUltimate(player)) {
			Chat.sendMessage(player, "&cCannot deal damage in bat form!");
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == EnumDamageCause.ENTITY_ATTACK && player != event.getWhoGotDamaged()) {
			this.addBlood(player);
		}

	}

	@Override
	public void onStartOnce() {
		// Fuel Controller
		new GameTask() {
			@Override
			public void run() {
				playerData.values().forEach(data -> {
					final Player player = data.getPlayer();
					final boolean isFlying = player.isFlying();
					final boolean checkFuel = isFlying && !isUsingUltimate(player);
					data.addFuel(isFlying ? -2 : 1);

					// Kick if out of fuel
					if (data.getFuel() <= 30 && checkFuel) {
						Chat.sendTitle(player, "", "&cLow Energy!", 0, 10, 5);
					}

					if (data.getFuel() <= 15 && checkFuel) {
						transformPlayer(player, Transform.HUMAN, true);
					}

					// Display Fuel
					Chat.sendActionbar(player, buildBars(data.getFuel()));
				});
			}
		}.runTaskTimer(10, 10);
	}

	private String buildBars(long fuel) {
		final StringBuilder builder = new StringBuilder();
		final long fuelPercent = fuel * 20 / 100;
		for (int i = 0; i < 20; i++) {
			builder.append(i > fuelPercent ? "&7" : "&4&l").append("✠");
		}
		return builder.append(" &e[&l").append(fuel).append("&e]").toString();
	}

	@Override
	public void onStart(Player player) {
		player.setFlying(false);
		player.setFlySpeed(0.05f);
		this.playerData.put(player, new VampireData(player));
	}

	@Override
	public void onPlayerRevealed(Player player) {
		player.setAllowFlight(true);
	}

	@Override
	public void onStop(Player player) {
		player.setAllowFlight(false);
	}

	@Override
	public void onStopOnce() {
		this.playerData.clear();
		this.fragileSpirit.clear();
	}

	@Override
	public void useUltimate(Player player) {
		transformPlayer(player, Transform.VERY_ANGRY_BAT, false);

		// Fx Red Border
		//new Border(player.getWorld()).setSize(1000).setWarningDistance(5000).applyChanges(player);
		this.setUsingUltimate(player, true, ULTIMATE_FORM_TIME);

		new GameTask() {
			@Override
			public void run() {
				//Border.reset(player);
				if (!Spectator.isSpectator(player)) {
					transformPlayer(player, Transform.BAT, false);
				}
			}
		}.runTaskLater(ULTIMATE_FORM_TIME);

	}

	@Override
	public void onDeath(Player player) {

	}

	public void transformPlayer(Player player, Transform into, boolean haveBeenKicked) {
		// don't allow transformation while ulting
		if (this.isUsingUltimate(player) || player.hasCooldown(Material.GHAST_TEAR)) {
			return;
		}

		final VampireData data = getData(player);

		if (data.getFuel() <= 15 && into != Transform.HUMAN) {
			Chat.sendMessage(player, "&c⦾ Not enough energy to transform!");
			return;
		}

		player.setFlySpeed((into == Transform.VERY_ANGRY_BAT) ? 0.1f : 0.05f);
		player.setCooldown(Material.GHAST_TEAR, 20);

		switch (into) {
			case VERY_ANGRY_BAT, BAT -> {
				player.setFlying(true);
				Chat.sendMessage(player, "&a⦾ Transforming into a %s.", Chat.capitalize(into.name()));

				GameUtils.hidePlayer(player);
				player.getInventory().setHeldItemSlot(0);

				data.spawnBat((into == Transform.VERY_ANGRY_BAT ? "&c&lAngry " : "&f") + player.getName());
				data.getSpiritGarbage().hide(player);
				fragileSpirit.put(data.getSpirit(), player);

				PlayerLib.playSound(player, Sound.ENTITY_BAT_AMBIENT, 0.75f);
				PlayerLib.spawnParticle(player.getLocation(), Particle.SPELL_MOB, 5, 0.2d, 0.2d, 0.2d, 0.0f);

			}
			case HUMAN -> {
				fragileSpirit.remove(data.getSpirit());
				data.removeBat();
				Chat.sendMessage(player, "&a⦾ Transforming into human.");
				PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.75f);

				if (haveBeenKicked) {
					player.setAllowFlight(false);
					Chat.sendMessage(player, "&c⦾ Cannot transform into a bat for another &e%ss&c!", BukkitUtils.roundTick(BAT_KICK_CD));

					new GameTask() {
						@Override
						public void run() {
							player.setAllowFlight(true);
							Chat.sendTitle(player, "", "&aYou can now fly!", 5, 15, 5);
						}
					}.runTaskLater(BAT_KICK_CD);

				}

				player.setFlying(false);
				GameUtils.showPlayer(player);

			}
		}

	}

	@NOTNULL
	public VampireData getData(Player player) {
		return this.playerData.getOrDefault(player, new VampireData(player));
	}

	public enum Transform {
		BAT,
		VERY_ANGRY_BAT,
		HUMAN
	}

	public void addBlood(Player player) {
		if (isBat(player)) {
			return;
		}

		final VampireData data = getData(player);
		data.addBlood(1);

		if (data.getBlood() >= 10) {
			Chat.sendTitle(player, "", "&4☮ &c&lBlood is full!", 0, 10, 5);
		}

		player.getInventory().setItem(4, BLOOD);
		final ItemStack item = player.getInventory().getItem(4);
		if (item != null) {
			item.setAmount(data.getBlood());
		}
		player.updateInventory();

	}

	private void drinkBlood(Player player) {
		final ItemStack item = player.getInventory().getItemInMainHand();

		if (item.getType() != Material.REDSTONE || player.hasCooldown(Material.REDSTONE)) {
			return;
		}

		final VampireData data = getData(player);
		final int blood = data.getBlood();
		final double healingAmount = blood / 2d;

		if (blood > 0) {
			Chat.sendMessage(player, "&4☮ &cYou drank %s blood!", blood);
			item.setAmount(0);
			data.setBlood(0);
			player.setCooldown(Material.REDSTONE, BLOOD_DRINK_COOLDOWN);

			// Fx
			PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8f);

			// Heal
			DamageFeature.addHealth(player, healingAmount);

			// Move slot to 0
			player.getInventory().setHeldItemSlot(0);

			Chat.sendMessage(player, " &c&l+ %s ❤", healingAmount);

			if (blood >= 5) {
				Chat.sendMessage(player, " &b&l+ Speed II &7(5s)");
			}

			if (blood >= 10) {
				Chat.sendMessage(player, " &4&l+ Strength I &7(5s)");
			}

		}

	}

	@EventHandler
	public void handleBatDamage(EntityDamageEvent ev) {
		final Entity entity = ev.getEntity();
		if (entity instanceof Player) {
			final Player player = (Player)entity;
			if (isVampire(player) && isBat(player)) {
				if (isUsingUltimate(player)) {
					return;
				}
				transformPlayer(player, Transform.HUMAN, true);
				return;
			}
		}

		if (entity instanceof Bat) {
			if (fragileSpirit.containsKey(entity)) {
				final Player player = fragileSpirit.get(entity);
				if (isUsingUltimate(player)) {
					return;
				}
				transformPlayer(player, Transform.HUMAN, true);
				if (ev instanceof EntityDamageByEntityEvent) {
					final Entity damager = ((EntityDamageByEntityEvent)ev).getDamager();
					if (damager instanceof Player && Spectator.isSpectator((Player)damager)) {
						return;
					}
					Chat.sendMessage(damager, "&aYou kicked %s from their bat form!", player.getName());
				}
			}
		}
	}

	@EventHandler
	public void handleMoveEvent(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		if (GameManager.current().isGameInProgress() && isVampire(player) && isBat(player)) {
			final Entity bat = getData(player).getSpirit();
			final Location location = player.getLocation().clone();
			bat.teleport(location.add(0.0d, 0.5d, 0.0d));
		}
	}

	@EventHandler
	public void handleFlight(PlayerToggleFlightEvent ev) {
		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}
		final Player player = ev.getPlayer();
		//if (ClassManager.getClass(player) == ClassManager.VAMPIRE)
		{
			if (Spectator.isSpectator(player)) {
				return;
			}
			if (player.hasCooldown(Material.GHAST_TEAR)) {
				ev.setCancelled(true);
				return;
			}
			transformPlayer(player, !player.isFlying() ? Transform.BAT : Transform.HUMAN, false);
		}
	}

	public boolean isBat(Player player) {
		return getData(player).getSpirit() != null && player.isFlying();
	}

	public boolean isVampire(Player player) {
		return false;
		//return ClassManager.getClass(player) == ClassManager.VAMPIRE;
	}

}
