package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

public class ZealotClass extends IClass implements Listener {

	private final int ULTIMATE_FORM_TIME = 400;
	private final int WHOOP_DELAY = 400;
	private final double pureDamage = 4.5d;

	private final ItemStack PLASMA_SHIELD = new CFItemBuilder(Material.ENDER_EYE).setName(ChatColor.BLUE + "Plasma Shield").build();
	private final ItemStack ABILITY_SWORD = new CFItemBuilder(Material.GOLDEN_SWORD).setName(ChatColor.DARK_AQUA + "Psionic Blade")
			.setPureDamage(pureDamage / 2d)
			.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 1.6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
			.applyDefaultSettings()
			.build();
	private final ItemStack NORMAL_SWORD = new CFItemBuilder(Material.DIAMOND_SWORD, "psinoc_blade").setName("&aPsionic Blade &6&lCLICK")
			.setPureDamage(pureDamage)
			.applyDefaultSettings()
			.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 1.6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
			.addClickEvent(this::useAbility)
			.build();

	public ZealotClass() {
		super("Zealot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMxNTMwZGI3NGJhYzg0YWQ5ZTMyMjI4MGM1NmM0ZTAxOTlmYmU4Nzk4ODNiNzZjOWNmM2ZkOGZmMTljZjAyNSJ9fX0=");

		this.setInfo("Fast warrior from another planet with a sword that makes him faster for &22 &7seconds, with &b7 &7seconds cooldown. Fun Fact: Zealots loves gold, don't give them gold.____&e○ Plasma Shield &7- Hold the Shield to negate all damage,__the shield recharges every &b10 &7seconds.__Max &b5 &7charges.", "Midas Touch", "Gain Midas power for &b" + ULTIMATE_FORM_TIME / 20 + " &7seconds and slight Speed boost. While affected, your hits applies &eGolden Curse&7. Hitting players affected by &eGolden Curse &7gives damager 1 coins for every hit.", 5);
		this.setRating(ClassRating.C);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMxNTMwZGI3NGJhYzg0YWQ5ZTMyMjI4MGM1NmM0ZTAxOTlmYmU4Nzk4ODNiNzZjOWNmM2ZkOGZmMTljZjAyNSJ9fX0=");
		eq.setChestplate(Material.GOLDEN_CHESTPLATE);
		eq.setLeggings(Material.GOLDEN_LEGGINGS);
		eq.setBoots(Material.GOLDEN_BOOTS);

		eq.addItem(NORMAL_SWORD);
		eq.addItem(PLASMA_SHIELD);

	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		if (isHoldingPlasmaShield(player)) {
			Chat.sendActionbar(player, "&aYour Plasma Shield just protected you from taking damage");
			removePlasmaShield(player);
			event.setCancelled(true);
		}
	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (isUsingUltimate(player)) {
			GameEffectManager.applyEffect(event.getWhoGotDamaged(), GameEffectType.GOLDEN_CURSE, 40);
		}
	}

	public boolean isHoldingPlasmaShield(Player player) {
		return player.getInventory().getItemInMainHand().isSimilar(PLASMA_SHIELD);
	}

	@Override
	public void onStart(Player player) {
		final ItemStack item = player.getInventory().getItem(1);
		if (item != null) {
			item.setAmount(5);
		}
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));

		new GameTask() {
			@Override
			public void run() {
				setUsingUltimate(player, false);
			}
		}.runTaskLater(400);

	}

	public void removePlasmaShield(Player player) {
		GameUtils.removeHeldItem(player, 1);
		addPlasmaShield(player);
	}

	public void addPlasmaShield(Player player) {

		new GameTask() {
			@Override
			public void run() {
				if (player.getInventory().containsAtLeast(PLASMA_SHIELD, 5)) {
					return;
				}
				player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 2, 1);
				player.getInventory().addItem(PLASMA_SHIELD);
			}
		}.runTaskLater(200);

	}

	private void useAbility(Player player) {

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 3, true));
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 2, 2);
		player.getInventory().setItem(0, ABILITY_SWORD);

		player.setCooldown(Material.DIAMOND_SWORD, WHOOP_DELAY);
		player.setCooldown(Material.GOLDEN_SWORD, WHOOP_DELAY);

		new GameTask() {
			@Override
			public void run() {
				player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 1, 1);
				player.getInventory().setItem(0, NORMAL_SWORD);
			}
		}.runTaskLater(WHOOP_DELAY);

	}

	@EventHandler
	public void handleGlowing(PlayerItemHeldEvent ev) {
		final Player player = ev.getPlayer();
		if (ClassManager.getClass(player) == ClassManager.ZEALOT) {
			if (player.getInventory().getItem(ev.getNewSlot()) != null && player.getInventory().getItem(ev.getNewSlot()).isSimilar(PLASMA_SHIELD)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200000, 1, true));
			}
			else {
				player.removePotionEffect(PotionEffectType.GLOWING);
			}
		}
	}

}
