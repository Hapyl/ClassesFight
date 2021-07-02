package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MageClass extends IClass {

	private final ItemStack ITEM_HEART_OF_WYVERN = new CFItemBuilder(Material.FERMENTED_SPIDER_EYE, "wyvern").addClickEvent(this::useWyvern)
			.setName("&c&lHeart of Wyvern &e&lRIGHT CLICK")
			.build();
	private final ItemStack ITEM_DRAGON_SKIN = new CFItemBuilder(Material.PHANTOM_MEMBRANE, "dragon_skin").addClickEvent(this::useDragon)
			.setName("&c&lDragon's Skin &e&lRIGHT CLICK")
			.build();
	private final ItemStack SOUL_EATER = new CFItemBuilder(Material.IRON_HOE, "soul_eater").setName("&eSoul Eater")
			.setPureDamage(7.0)
			.applyDefaultSettings(false)
			.addAttribute(Attribute.GENERIC_ATTACK_SPEED, -3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
			.addClickEvent(this::shootSoul)
			.build();
	private final ItemStack ENDER_PEARLS = new CFItemBuilder(Material.ENDER_PEARL).setName("&aEnder Pearl")
			.setAmount(2)
			.applyDefaultSettings(false)
			.build();

	public final Map<UUID, Integer> soulsEaten = new HashMap<>();

	private final int DRAGON_SKIN_LENGTH = 400;
	private final int WYVERN_HEART_LENGTH = 500;

	private final double WYVERN_HEALING = 10.0d;

	public MageClass() {
		super("Mage", Material.PHANTOM_MEMBRANE);

		this.setRole(ClassRole.RANGE);
		this.setMagic(EnumInfo.HIGH);
		this.setRating(ClassRating.C);
		this.setLvlRequired(4);

		this.setInfo("Amateur Necromancer with a &eSoul Eater &7that can absorb souls upon &emelee damage &7and unleash them with &e&lRIGHT &e&lCLICK&7. Which makes him both Melee and Range warrior!",
				"Magical Trainings", "Retrieve two ancient spells and use one of them to your advantage!____&e○ Heart of Wyvern &7heals you__for &c" + (WYVERN_HEALING / 2) + "&c❤&7, makes you fast but weak for &b" + (WYVERN_HEART_LENGTH / 20) + "s&7.____&e○ Dragon's Skin &7makes you incredible strong but slow for &b" + (DRAGON_SKIN_LENGTH / 20) + "s&7.____Only one of the spells can be used at the same time; You will not gain ultimate points until the effect is over.",
				// "Grants you a choice between &eHeart of Wyvern &7that makes heals you for &c" + (WYVERN_HEALING / 2) + "❤ &7and makes you fast but weak for &b" + (WYVERN_HEART_LENGTH / 20) + "s&7 or &eDragon's Skin &7that makes you incredible strong but slow for &b" + (DRAGON_SKIN_LENGTH / 20) + "s&7."
				4);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQxZTZlNGJjZDI2NjdiYjI4NGZiMGRkZTM2MTg5NDg0MGVhNzgyZWZiZmI3MTdmNjI0NGUwNmI5NTFjMmIzZiJ9fX0=");
		eq.setChestplate(56, 34, 70);
		eq.setLeggings(29, 29, 33);
		eq.setBoots(56, 34, 70);

		eq.addItem(SOUL_EATER);
		eq.addItem(ENDER_PEARLS);

		// c?

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (player == event.getPlayer() || event.getCause() == EnumDamageCause.SOUL_WHISPER || event.getDamage() < 0.0d) {
			return;
		}

		if (this.getSouls(player) <= 50) {
			this.addSouls(player, 1);
		}
		else {
			Chat.sendMessage(player, "&cYou have reached maximum souls!");
			OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_BURP, 0.75f);
		}
	}

	@Override
	public void onStopOnce() {
		soulsEaten.clear();
	}

	@Override
	public void onStart(Player player) {
	}

	private void shootSoul(Player player) {
		if (getSouls(player) > 0) {
			if (!player.hasCooldown(Material.IRON_HOE)) {
				removeSouls(player, 1);
				player.setCooldown(Material.IRON_HOE, 5);
				OldPlayerLib.playSound(player, Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
				GameUtils.rayTraceLine(player, 50, 0.5, 1.5, EnumDamageCause.SOUL_WHISPER, this::spawnParticles, this::hitEnemy);
			}
		}
		else {
			Chat.sendActionbar(player, "&cNo more souls available");
			OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_BURP, 1.75f);
		}
	}

	private void spawnParticles(Location location) {
		location.getWorld().spawnParticle(Particle.SOUL, location, 1, 0.1, 0, 0.1, 0.035);
	}

	private void hitEnemy(Player player) {
		final Location location = player.getLocation();
		location.getWorld().spawnParticle(Particle.SOUL, location, 8, 0, 0, 0, 0.10);
		location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 10, 0, 0, 0, 0.25);
	}

	public int getSouls(Player player) {
		return soulsEaten.getOrDefault(player.getUniqueId(), 0);
	}

	public void fixSoulEater(Player player) {
		final PlayerInventory inventory = player.getInventory();
		for (int i = 1; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			if (item != null && item.getType() == Material.IRON_HOE) {
				item.setAmount(0);
			}
		}
		final ItemStack item = inventory.getItem(0);
		if (item != null) {
			item.setAmount(Math.max(1, getSouls(player)));
		}
	}

	public void setSouls(Player player, int integer) {
		soulsEaten.put(player.getUniqueId(), integer);
		// update players soul count
		final ItemStack item = player.getInventory().getItem(0);
		if (item != null && item.getType() == Material.IRON_HOE) {
			item.setAmount(Math.max(1, getSouls(player)));
		}
	}

	public void addSouls(Player player, int integer) {
		setSouls(player, getSouls(player) + integer);
	}

	public void removeSouls(Player player, int integer) {
		setSouls(player, getSouls(player) - integer);
	}

	@Override
	public void useUltimate(Player player) {
		final PlayerInventory inv = player.getInventory();
		// prevent farming points
		this.setUsingUltimate(player, true);
		removeItems(player);
		inv.addItem(ITEM_HEART_OF_WYVERN);
		inv.addItem(ITEM_DRAGON_SKIN);
	}

	private void removeItems(Player player) {
		final PlayerInventory inventory = player.getInventory();
		inventory.remove(Material.FERMENTED_SPIDER_EYE);
		inventory.remove(Material.PHANTOM_MEMBRANE);
	}

	private void useWyvern(Player player) {

		this.setUsingUltimate(player, true, WYVERN_HEART_LENGTH);

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, WYVERN_HEART_LENGTH, 3, true));
		GameEffectManager.applyEffect(player, GameEffectType.WEAKNESS, WYVERN_HEART_LENGTH, true);
		DamageFeature.addHealth(player, WYVERN_HEALING);

		player.getWorld().spawnParticle(Particle.HEART, player.getLocation().clone().add(0, 1, 0), 7, 0.3, 0.2, 0.3, 1);
		player.sendMessage(ChatColor.GREEN + "Used Heart of Wyvern!");
		removeItems(player);

	}

	private void useDragon(Player player) {

		this.setUsingUltimate(player, true, DRAGON_SKIN_LENGTH);

		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, DRAGON_SKIN_LENGTH, 2, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, DRAGON_SKIN_LENGTH, 5, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, DRAGON_SKIN_LENGTH, 250, true));
		player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation().clone().add(0, 1, 0), 40, 0.1, 0.1, 0.1, 1);
		player.sendMessage(ChatColor.GREEN + "Used Dragon's Skin!");
		removeItems(player);

	}

}
