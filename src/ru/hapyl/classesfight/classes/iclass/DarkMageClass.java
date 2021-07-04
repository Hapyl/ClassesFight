package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Reflect;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.ItemSlot;
import kz.hapyl.spigotutils.module.reflect.npc.NPCPose;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.extra.Spell;
import ru.hapyl.classesfight.classes.iclass.extra.SpellMode;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.*;

public class DarkMageClass extends IClass implements Listener {

	private final Map<Player, SpellMode> spells;
	private final int ultimateTime = 12 * 20;
	private final Material weaponMaterial = Material.WOODEN_HOE;

	public DarkMageClass() {
		super("Dark Mage", Material.CHARCOAL);
		this.setMagic(EnumInfo.HIGH);
		this.setRole(ClassRole.MELEE);
		this.setRating(ClassRating.NOT_YET);

		this.spells = new HashMap<>();

		this.setInfo("A mage that was cursed by &8&lDark &8&lMagic&7. But even it couldn't kill him...__" + Icons.ABILITY_DOT + "Cursed Ward &7A " + "wand, that is capable of casting the darkest spells! &e&lRIGHT &e&lCLICK &7to enter spell mode, then combine two more clicks (left " + "or right) to cast a spell. &bAvailable spells:____" + buildTinySpellsLore() + "____&7Hover over a wand in game to see detailed " + "descriptions.", "Wither Rider", "Transform to the wither for &b" + (GameUtils
				.roundTick(ultimateTime)) + "s&7. While transformed, &e&lCLICK &7to shoot wither skulls that deals massive damage. After wither " +
				"disappears, you perform plunging attack that deals damage in AoE upon hitting the ground.", 6);
		this.setUltimateSound(ENTITY_WITHER_SPAWN, 2.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZjYTYzNTY5ZTg3Mjg3MjJlY2M0ZDEyMDIwZTQyZjA4NjgzMGUzNGU4MmRiNTVjZjVjOGVjZDUxYzhjOGMyOSJ9fX0=");
		eq.setChestplate(102, 255, 255);
		eq.setLeggings(Material.IRON_LEGGINGS);
		eq.setBoots(153, 51, 51);

		eq.addItem(new CFItemBuilder(weaponMaterial).setName("&cCursed Wand")
				.setPureDamage(2.0)
				.addSmartLore("A magical item capable of casting dark spells...____" + buildSpellsLore())
				.applyDefaultSettings(false)
				.build());

	}

	@Override
	public void onStart(Player player) {
		// Indicators
		final PlayerInventory inventory = player.getInventory();

		int slot = 2;
		for (EnumAbility value : EnumAbility.values()) {
			inventory.setItem(slot, new ItemBuilder(value.material).setName(ChatColor.GREEN + (value.getName() + " Indicator"))
					.setSmartLore("This will indicate the cooldown of the ability!")
					.toItemStack());
			++slot;
		}

	}

	@Override
	public void onStopOnce() {
		this.spells.values().forEach(SpellMode::clearButtons);
		this.spells.clear();
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, ultimateTime);

		player.setAllowFlight(true);
		player.setFlying(true);

		final Wither wither = GarbageCollector.spawnEntity(player.getLocation(), Wither.class, me -> {
			me.setAI(false);
			me.setHealth(player.getHealth());
			me.setCustomName(player.getName());
			me.setCustomNameVisible(true);
			me.setGlowing(true);
			me.setInvulnerable(false);
		});

		updateWitherName(player, wither);
		GameUtils.hidePlayer(player);
		Reflect.hideEntity(wither, player);

		new GameTask() {
			private int tick = ultimateTime;

			@Override
			public void run() {

				if (wither.isDead()) {
					killWither(player, wither);
					this.cancel();
					return;
				}

				if (Spectator.isSpectator(player)) {
					wither.remove();
					this.cancel();
					return;
				}

				if (tick-- <= 0) {
					killWither(player, wither);
					plunge(player);
					this.cancel();
					return;
				}

				if (tick % 20 == 0) {
					updateWitherName(player, wither);
				}
				wither.teleport(player);

			}
		}.runTaskTimer(0, 1);

	}

	private void updateWitherName(Player player, Wither wither) {
		wither.setCustomName(Chat.format("&4&l☠ &c%s &8| &a&l%s ❤", player.getName(), BukkitUtils.decimalFormat(wither.getHealth())));
	}

	private void killWither(Player player, Wither wither) {
		player.setFlying(false);
		player.setAllowFlight(false);
		GameUtils.showPlayer(player);
		PlayerLib.playSound(player.getLocation(), ENTITY_WITHER_DEATH, 1.0f);
		wither.remove();
	}

	@EventHandler()
	public void handleButtons(PlayerInteractEvent ev) {
		final Action action = ev.getAction();
		final Player player = ev.getPlayer();

		if (!GameManager.current().isGameInProgress() || !GameManager.current()
				.arePlayersRevealed() || action == Action.PHYSICAL || player.getInventory()
				.getItemInMainHand()
				.getType() != weaponMaterial || ev.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		final boolean isLeftClick = (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);

		if (isUsingUltimate(player)) {
			if (player.hasCooldown(weaponMaterial)) {
				return;
			}
			PlayerLib.playSound(player.getLocation(), ENTITY_WITHER_SHOOT, 1.0f);
			final WitherSkull skull = player.launchProjectile(WitherSkull.class, player.getLocation().getDirection().multiply(3.0d));
			skull.setCharged(true);
			skull.setYield(0.0f);
			skull.setShooter(player);
			player.setCooldown(weaponMaterial, 20);
			return;
		}

		final SpellMode spellMode = this.spells.get(player);

		if (isLeftClick && (spellMode == null || spellMode.isTimedOut())) {
			return;
		}

		// check for spell
		if (!isLeftClick) {
			if (spellMode == null) {
				this.spells.put(player, new SpellMode().titleCurrent(player));
				return;
			}
			else if (spellMode.isTimedOut()) {
				this.spells.put(player, new SpellMode().titleCurrent(player));
				return;
			}
		}

		final SpellMode.SpellButton[] response = spellMode.addAndCheck(isLeftClick ? SpellMode.SpellButton.LEFT : SpellMode.SpellButton.RIGHT);
		spellMode.titleCurrent(player);

		// if has response than can use spell
		if (response != null) {
			performSpell(player, response[0], response[1]);
			this.spells.remove(player);
		}

	}

	@EventHandler()
	public void handleWitherSkullHit(ProjectileHitEvent ev) {
		if (!(ev.getEntity() instanceof final WitherSkull skull) || !(ev.getEntity().getShooter() instanceof Player)) {
			return;
		}

		final Player player = (Player)skull.getShooter();
		final Location location = skull.getLocation();

		GameUtils.getPlayerInRange(location, 3.0d).forEach(target -> DamageFeature.damage(target, player, 4.0d, EnumDamageCause.WITHER_SKULLED));

	}

	private void performSpell(Player player, SpellMode.SpellButton first, SpellMode.SpellButton second) {

		if (GameEffectManager.playerHasEffect(player, GameEffectType.STUN)) {
			return;
		}

		EnumAbility.checkAndPerform(player, first, second);

	}

	private String buildTinySpellsLore() {
		return "&e&lRR &6➠ &eBlinding Curse__&e&lRL &6➠ &eSlowing Aura__&e&lLL &6➠ &eHealing Circle__&e&lLR &6➠ &eShadow Clone";
	}

	private String buildSpellsLore() {
		return String.format("&e&lRR &6➠ &eBlinding Curse &7Applies blinding curse to target player. &b(%ss cd)____" + "&e&lRL &6➠ &eSlowing Aura " + "&7Creates a slowness pull whenever you look that slows whoever steps into it. &b(%ss cd)____" + "&e&lLL &6➠ &eHealing Circle " + "&7Creates a healing circle at your location that heals all players periodically. &b(%ss cd)____" + "&e&lLR &6➠ &eShadow Clone " + "&7Creates a shadow clone of you at your current location and completely hides you. After a brief delay, clone explodes, stunning " + "nearby enemies. &b(%ss cd)", GameUtils
						.roundTick(EnumAbility.BLINDING_CURSE.getCd()), GameUtils.roundTick(EnumAbility.SLOWING_AURA.getCd()),
				GameUtils.roundTick(EnumAbility.HEALING_CIRCLE
						.getCd()), GameUtils.roundTick(EnumAbility.SHADOW_CLONE.getCd()));
	}

	private void plunge(Player player) {
		GameEffectManager.applyEffect(player, GameEffectType.FALL_DAMAGE_RESISTANCE, 1000, true);
		player.setVelocity(new Vector(0.0d, -0.5d, 0.0d));
		new GameTask() {

			private int maxAirTicks = 5 * 20;

			@Override
			public void run() {
				if (maxAirTicks-- <= 0 || player.isOnGround()) {
					this.cancel();
					EnumEffect.GROUND_PUNCH.display(player);
					GameUtils.getPlayerInRange(player.getLocation(), 4).forEach(target -> {
						if (target == player) {
							return;
						}
						DamageFeature.damage(target, player, 5.0d, EnumDamageCause.ENTITY_ATTACK);
					});
				}

			}
		}.runTaskTimer(0, 1);
	}

	public enum EnumAbility {

		BLINDING_CURSE("Blinding Curse", Material.INK_SAC, 100, new Spell() {

			@Override
			public void execute(Player player) {
				if (getAbility().hasCooldown(player)) {
					spellFailure(player, getAbility(), "On Cooldown!");
					return;
				}

				final Player target = GameUtils.getTargetPlayer(player, 35, 0.5d);

				if (target == null) {
					spellFailure(player, getAbility(), "No valid target!");
					return;
				}

				spellSuccess(player, getAbility(), getAbility().getCd());

				// effects
				target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 10, true));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 1, true));

				Chat.sendMessage(target, "&c%s has cursed you with the Dark Magic!", player.getName());
				Chat.sendMessage(player, "&aYou have cursed %s with Dark Magic!", target.getName());

				final Location location = player.getLocation();
				PlayerLib.playSound(location, Sound.ENTITY_SQUID_SQUIRT, 1.8f);
				GeometryLib.drawLine(location.add(0, 1, 0), target.getLocation()
						.add(0, 1, 0), 0.5, new GeometryLib.TinyParticle(Particle.SQUID_INK, 1, 0, 0, 0, 0));
				PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, .3, .3, .3, 3);
			}

			@Override
			public boolean test(SpellMode.SpellButton first, SpellMode.SpellButton second) {
				return first == SpellMode.SpellButton.RIGHT && second == SpellMode.SpellButton.RIGHT;
			}

			@Override
			public EnumAbility getAbility() {
				return BLINDING_CURSE;
			}
		}),

		SLOWING_AURA("Slowing Aura", Material.BONE_MEAL, 200, new Spell() {
			@Override
			public void execute(Player player) {
				final Block targetBlock = player.getTargetBlockExact(20);

				if (targetBlock == null) {
					spellFailure(player, getAbility(), "No valid block in sight!");
					return;
				}

				spellSuccess(player, getAbility(), getAbility().getCd());
				final Location location = targetBlock.getRelative(BlockFace.UP).getLocation();

				new GameTask() {
					private int tick = 10;

					@Override
					public void run() {

						if (tick-- <= 0) {
							this.cancel();
							return;
						}

						double radius = 4.0d;
						Geometry.drawCircle(location, radius, Quality.LOW, new Draw(Particle.SPELL) {
							@Override
							public void draw(Location location) {
								final World world = location.getWorld();
								if (world != null) {
									world.spawnParticle(this.getParticle(), location.getX(), location.getY(), location.getZ(), 1, 0, 0, 0, /*?*/
											null);
								}
							}
						});

						PlayerLib.playSound(location, BLOCK_HONEY_BLOCK_SLIDE, 0.0f);
						GameUtils.getPlayerInRange(location, radius)
								.forEach(entity -> entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3)));

					}
				}.runTaskTimer(0, 5);
			}

			@Override
			public boolean test(SpellMode.SpellButton first, SpellMode.SpellButton second) {
				return first == SpellMode.SpellButton.RIGHT && second == SpellMode.SpellButton.LEFT;
			}

			@Override
			public EnumAbility getAbility() {
				return SLOWING_AURA;
			}
		}),

		HEALING_CIRCLE("Healing Circle", Material.APPLE, 600, new Spell() {
			@Override
			public void execute(Player player) {

				final double radius = 2.5d;
				final Location location = player.getLocation();
				spellSuccess(player, getAbility(), getAbility().getCd());

				final int delay = 1;

				new GameTask() {

					private int tick = 200;
					private double t1 = 0;

					@Override
					public void run() {

						double x = radius * Math.sin(t1);
						double z = radius * Math.cos(t1);

						location.add(x, 0, z);
						PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 2, 0.01, 0, 0.01, 0);
						location.subtract(x, 0, z);

						if (t1 >= 36) {
							t1 = 0;
						}
						else {
							t1 += 0.1;
						}

						if ((tick % 20) == 0) {
							GameUtils.getPlayerInRange(location, radius).forEach(target -> {
								DamageFeature.addHealth(target, 2.0d);
								PlayerLib.playSound(target, BLOCK_GRASS_HIT, 1.0f);
							});
							PlayerLib.spawnParticle(location, Particle.HEART, 5, 1, 0.2, 1, 0.01f);
						}

						if ((tick -= delay) <= 0) {
							this.cancel();
						}

					}
				}.runTaskTimer(0, delay);
			}

			@Override
			public boolean test(SpellMode.SpellButton first, SpellMode.SpellButton second) {
				return first == SpellMode.SpellButton.LEFT && second == SpellMode.SpellButton.LEFT;
			}

			@Override
			public EnumAbility getAbility() {
				return HEALING_CIRCLE;
			}
		}),
		SHADOW_CLONE("Shadow Clone", Material.NETHERITE_SCRAP, 300, new Spell() {
			@Override
			public void execute(Player player) {

				final HumanNPC npc = new HumanNPC(player.getLocation(), "", player.getName());
				spellSuccess(player, getAbility(), getAbility().getCd());
				GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, 3 * 20);
				npc.showAll();
				final PlayerInventory inventory = player.getInventory();
				npc.setItem(ItemSlot.HEAD, inventory.getHelmet());
				npc.setItem(ItemSlot.CHEST, inventory.getChestplate());
				npc.setItem(ItemSlot.LEGS, inventory.getLeggings());
				npc.setItem(ItemSlot.FEET, inventory.getBoots());
				npc.setItem(ItemSlot.MAINHAND, inventory.getItemInMainHand());

				if (player.isSneaking()) {
					npc.setPose(NPCPose.CROUCHING);
				}

				new GameTask() {
					@Override
					public void run() {
						final Location location = npc.getLocation();

						if (!ClassManager.DARK_MAGE.getTheClass().isUsingUltimate(player)) {
							GameUtils.showPlayer(player);
						}
						npc.remove();

						PlayerLib.spawnParticle(location, Particle.SQUID_INK, 10, 0.1, 0.5, 0.1, 0.05f);
						PlayerLib.playSound(location, ENTITY_SQUID_SQUIRT, 0.25f);
						GameUtils.getPlayerInRange(location, 3.0d).forEach(target -> {
							DamageFeature.damage(target, player, 3.0d, EnumDamageCause.ENTITY_ATTACK);
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2));
						});

					}
				}.runTaskLater(3 * 20);
			}

			@Override
			public boolean test(SpellMode.SpellButton first, SpellMode.SpellButton second) {
				return first == SpellMode.SpellButton.LEFT && second == SpellMode.SpellButton.RIGHT;
			}

			@Override
			public EnumAbility getAbility() {
				return SHADOW_CLONE;
			}
		});

		private final String display;
		private final Material material;
		private final int cd;
		private final Spell spell;

		EnumAbility(String name, Material material, int cd, Spell spell) {
			this.display = name;
			this.material = material;
			this.cd = cd;
			this.spell = spell;
		}

		private static void spellFailure(Player player, EnumAbility ability, String reason) {
			Chat.sendActionbar(player, "&cCouldn't use " + ability.getName() + "! " + ChatColor.YELLOW + reason, 5, 30, 5);
			PlayerLib.playSound(player, ENTITY_VILLAGER_NO, 1.0f);
		}

		private static void spellSuccess(Player player, EnumAbility ability, int cd) {
			Chat.sendActionbar(player, "&aCasted " + ability.getName() + "!");
			ability.setCooldown(player, cd);
			PlayerLib.playSound(player, ENTITY_WITCH_THROW, 0.4f);
			PlayerLib.playSound(player, ENTITY_ARROW_HIT_PLAYER, 0.7f);
		}

		public static void checkAndPerform(Player player, SpellMode.SpellButton first, SpellMode.SpellButton second) {
			for (final EnumAbility value : values()) {
				if (value.spell.test(first, second)) {
					if (value.hasCooldown(player)) {
						spellFailure(player, value, "On Cooldown!");
						return;
					}
					value.spell.execute(player);
				}
			}
		}

		public int getCd() {
			return cd;
		}

		public String getName() {
			return display;
		}

		public void setCooldown(Player player, int cd) {
			player.setCooldown(this.material, cd);
		}

		public boolean hasCooldown(Player player) {
			return player.hasCooldown(this.material);
		}

	}

}
