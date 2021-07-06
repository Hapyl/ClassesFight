/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.*;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * This class used to create custom ClassesFight classes.
 *
 * @author hapyl
 * @version 1.0
 */

public abstract class IClass {

	private final Set<Player> usingUltimate = new HashSet<>();

	private final String className;
	private final ClassEquipment classEquipment;
	private String devName = "null";

	private ItemStack item;
	private EnumInfo health;
	@Deprecated private EnumInfo attack;
	@Deprecated private EnumInfo defense;
	@Deprecated private EnumInfo magic;
	private ClassInfo info;
	private ClassUltimate ultimate;
	private ClassRole role;
	private ClassRating rating;
	private ClassStatus status;

	private ClassDifficulty difficulty = ClassDifficulty.NONE;
	private int lvlRequired = 0;

	/**
	 * Создает объект класса.
	 *
	 * @param className - Название класса.
	 * @param mat       - Предмет, который будет показыватся в меню.
	 * @param health    - Статистика здоровья.
	 * @param attack    - Статистика атаки.
	 * @param defense   - Статистика жащиты.
	 * @param magic     - Статистика магии.
	 * @param info      - Информация о классе.
	 * @param ultimate  - Информация о ульте.
	 */

	public IClass(String className, Material mat, EnumInfo health, EnumInfo attack, EnumInfo defense, EnumInfo magic, ClassRole role, ClassInfo info, ClassUltimate ultimate) {
		this(className, new ItemStack(mat), health, attack, defense, magic, role, info, ultimate);
	}

	public IClass(String className, Material mat, EnumInfo health, EnumInfo attack, EnumInfo defense, EnumInfo magic, ClassInfo info,
			ClassUltimate ultimate) {
		this(className, new ItemStack(mat), health, attack, defense, magic, null, info, ultimate);
	}

	/**
	 * @param headTexture - Заменяет предмет на голову игрока с данной текстурой.
	 */
	public IClass(String className, String headTexture, EnumInfo health, EnumInfo attack, EnumInfo defense, EnumInfo magic, ClassRole role,
			ClassInfo info, ClassUltimate ultimate) {
		this(className, new CFItemBuilder(Material.PLAYER_HEAD).setHeadTexture(headTexture).build(), health, attack, defense, magic, role, info,
				ultimate);
	}

	public IClass(String className, String headTexture, EnumInfo health, EnumInfo attack, EnumInfo defense, EnumInfo magic, ClassInfo info,
			ClassUltimate ultimate) {
		this(className, new CFItemBuilder(Material.PLAYER_HEAD).setHeadTexture(headTexture).build(), health, attack, defense, magic, null, info,
				ultimate);
	}

	public IClass(String className, ItemStack menuIcon, EnumInfo health, EnumInfo attack, EnumInfo defense, EnumInfo magic, ClassRole role,
			ClassInfo info, ClassUltimate ultimate) {
		this.className = className;
		this.item = new CFItemBuilder(menuIcon).applyDefaultSettings(false).build();
		this.health = health;
		this.attack = attack;
		this.defense = defense;
		this.magic = magic;
		this.role = role;
		this.info = info;
		this.ultimate = ultimate;
		this.classEquipment = new ClassEquipment();
		this.status = ClassStatus.NONE;
	}

	public void setStatus(ClassStatus status) {
		this.status = status;
	}

	public ClassStatus getStatus() {
		return status;
	}

	public ClassEquipment getClassEquipment() {
		return classEquipment.equipAtStart();
	}

	public ClassEquipment getClassEquipment(boolean autoEquip) {
		return this.classEquipment.equipAtStart();
	}

	public IClass(String className, Material stack) {
		this(className, stack, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.NONE, ClassRole.MELEE, new ClassInfo("", "", "", ""),
				new ClassUltimate("Unnamed Ultimate", Integer.MAX_VALUE));
	}

	public IClass(String className, String texture) {
		this(className, ItemBuilder.playerHead(texture).toItemStack(), EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.NONE,
				ClassRole.MELEE, new ClassInfo("", "", ""), new ClassUltimate("", 69420));
	}

	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
	}

	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		// Processes
	}

	public void onDeath(final Player player) {

	}

	public void onLockdown(final Player player) {

	}

	/**
	 * Нужно только для InvalidClass Не использовать!
	 *
	 * @param className - Название класса.
	 */
	public IClass(String className) {
		this(className, Material.BEDROCK, EnumInfo.NONE, EnumInfo.NONE, EnumInfo.NONE, EnumInfo.NONE, null, new ClassInfo("", "", "", ""),
				new ClassUltimate("", Integer.MAX_VALUE));
	}

	/**
	 * Выполняется для игрока во время старта игры. Используется чтобы дать игрока броню и тд.
	 *
	 * @param player - Player
	 */
	public void onStart(final Player player) {

	}

	/**
	 * Выполняется для игрока во время показа игроков. Используется для выдачи абилок, чтобы их нельзя было использовать до показа игроков.
	 *
	 * @param player - Player
	 */
	public void onPlayerRevealed(final Player player) {
	}

	/**
	 * Использует ульту игрока.
	 *
	 * @param player - Player
	 */
	public abstract void useUltimate(final Player player);

	/**
	 * Выполняется один раз во время показа игроков. (Мнгновенно если в debugMode)
	 */
	public void onStartOnce() {
	}

	/**
	 * Выполняется один раз во время конца игры. Отлично для очистки Листов, Сетов, Мап и тд.
	 */
	public void onStopOnce() {
	}

	/**
	 * Выполняется для игрока во время конца игры.
	 *
	 * @param player - Player
	 */
	public void onStop(final Player player) {

	}

	/**
	 * Перезагружает абилити игрока. (Пока не реализованно)
	 *
	 * @param player - Player
	 */
	public void restartAbility(final Player player) {
	}

	/**
	 * Выполняется если 'predicateUltimate' возвращает false.
	 *
	 * @return Сообщение, который будет показано при ошибке.
	 */
	public String predicateMessage() {
		return "";
	}

	/**
	 * Проверяет boolean для игрока, если false то 'predicateMessage' будет показано.
	 * <p>
	 * Это используется в ульте, когда игрок использует ульты, первым делом проверяется этот метод, если он возвращает false, то ошибка будет показано
	 * игроку, и ульта на используется.
	 *
	 * @param player - Player
	 * @return boolean
	 */
	public boolean predicateUltimate(final Player player) {
		return true;
	}

	// setters - getters

	public ClassRating getRating() {
		return rating;
	}

	public IClass setRating(ClassRating rating) {
		this.rating = rating;
		return this;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public void setHealth(EnumInfo health) {
		this.health = health;
	}

	public void setAttack(EnumInfo attack) {
		this.attack = attack;
	}

	public void setDefense(EnumInfo defense) {
		this.defense = defense;
	}

	public void setMagic(EnumInfo magic) {
		this.magic = magic;
	}

	public void setInfo(ClassInfo info) {
		this.info = info;
	}

	private void setInfo(String lore, String ultName, String ultLore) {
		this.info = new ClassInfo(lore, "", ultName, ultLore);
	}

	public void setInfo(String lore, String aboutKit, String ultName, String ultLore, int ultPoints) {
		this.setInfo(new ClassInfo(lore, aboutKit, ultName, ultLore));
		this.setUltimate(ultName, ultPoints);
	}

	public void setInfo(String lore, String ultName, String ultLore, int ultPoints) {
		this.setInfo(lore, ultName, ultLore);
		this.setUltimate(ultName, ultPoints);
	}

	public void setUltimateSound(Sound sound, float pitch) {
		this.getUltimate().setSound(sound);
		this.getUltimate().setPitch(pitch);
	}

	public void setUltimate(ClassUltimate ultimate) {
		this.ultimate = ultimate;
	}

	public void setUltimate(String name, int ult) {
		this.ultimate = new ClassUltimate(name, ult);
	}

	public void setRole(ClassRole role) {
		this.role = role;
	}

	public boolean hasLevelRequirement() {
		return this.getLvlRequired() != 0;
	}

	public IClass setLvlRequired(int lvl) {
		this.lvlRequired = lvl;
		return this;
	}

	public int getLvlRequired() {
		return this.lvlRequired;
	}

	public IClass setDifficulty(ClassDifficulty difficulty) {
		this.difficulty = difficulty;
		return this;
	}

	public ClassDifficulty getDifficulty() {
		return this.difficulty;
	}

	public final ClassRole getRole() {
		return this.role;
	}

	public final String getClassName() {
		return this.className;
	}

	public final EnumInfo getHealth() {
		return this.health;
	}

	public final EnumInfo getAttack() {
		return attack;
	}

	public final EnumInfo getDefense() {
		return defense;
	}

	public final EnumInfo getMagic() {
		return magic;
	}

	public final ItemStack getItem() {
		return item;
	}

	public final ClassInfo getInfo() {
		return info;
	}

	public final ClassUltimate getUltimate() {
		return this.ultimate;
	}

	public void setUsingUltimate(Player player, boolean flag) {
		this.setUsingUltimate(player, flag, 0);
	}

	public void setUsingUltimate(Player player, boolean flag, int reverseAfter) {
		if (flag) {
			usingUltimate.add(player);
		}
		else {
			usingUltimate.remove(player);
		}
		// ticks
		if (reverseAfter > 0) {
			new GameTask() {
				@Override
				public void run() {
					setUsingUltimate(player, !flag, 0);
				}
			}.runTaskLater(reverseAfter);
		}
	}

	public boolean isUsingUltimate(Player player) {
		return this.usingUltimate.contains(player);
	}

	public void clearUltimate() {
		this.usingUltimate.clear();
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public final boolean isUnlocked(Player player) {
		return Experience.getInstance().getLvl(player) >= this.getLvlRequired();
	}

	public enum ClassRole {

		MELEE("Melees are professionals of dealing damage up close."),
		RANGE("Ranges are dead-eye shooters that can hold distance to strike."),
		RANGE_MELEE("These are specialist in both melee and range combat!"),
		STRATEGIST("Strikes at the perfect moment for them."),
		ASSASSIN("Assassins are masters of murder, with fast cuts and getaways.");

		private final String info;

		ClassRole(String info) {
			this.info = info;
		}

		public String getName() {
			return Chat.capitalize(this.name());
		}

		public String getInfo() {
			return info;
		}
	}

	public enum ClassDifficulty {

		NONE((byte)0, "please set class difficulty!!!"),
		VERY_EASY((byte)1, "Great class for new players."),
		EASY((byte)2, "Easy to use, new players will easily use it."),
		MEDIUM((byte)3, ""),
		HARD((byte)4, ""),
		EXPERT((byte)5, "");

		private final byte index;
		private final String info;

		ClassDifficulty(byte index, String info) {
			this.index = index;
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		public byte getIndex() {
			return index;
		}
	}

}
