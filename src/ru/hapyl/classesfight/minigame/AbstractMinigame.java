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

package ru.hapyl.classesfight.minigame;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ChestInventory;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.minigame.rewards.MinigameReward;
import ru.hapyl.classesfight.minigame.rewards.Reward;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import javax.annotation.Nullable;

public abstract class AbstractMinigame {

	protected static final int MIN_SLOT = 10;
	protected static final int MAX_SLOT = 34; // including

	private static final ItemStack BLACK_BAR = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").build();

	private Player player;
	private IMinigame mini;
	private ChestInventory chest;
	private Reward reward;
	private boolean valid;
	private State state;
	protected int round;
	protected int hintsUsed;
	protected long timeLeft = 40;
	private boolean debug;
	private boolean giveRewardsAtClose;

	public AbstractMinigame(Player player, IMinigame reference) {
		this(player, reference, false, true);
	}

	public AbstractMinigame(Player player, IMinigame reference, boolean debug) {
		this(player, reference, debug, true);
	}

	public AbstractMinigame(Player player, IMinigame reference, boolean debug, boolean giveRewardsAtClose) {
		if (GameManager.current().isGameInProgress()) {
			Chat.sendMessage(player, "&cYou cannot play minigames while the game in progress!");
			return;
		}
		this.player = player;
		this.mini = reference;
		this.debug = debug;
		this.chest = new ChestInventory("★ " + this.mini.getName() + (debug ? " &4&lDEBUG" : ""), 5).setPredicate(!GameManager.current().isGameInProgress());
		MinigameManager.current().setAbstractMinigame(this);
		this.valid = true;
		this.reward = reference.getReward();
		this.chest.fillBorder(BLACK_BAR);
		this.player.closeInventory();
		this.state = State.SHOWING;
		this.giveRewardsAtClose = giveRewardsAtClose;
		this.chest.setCloseEvent((pl, ch) -> this.forceKick(true));
		for (int i = MIN_SLOT; i <= MAX_SLOT; i++) {
			int finalI = i;
			this.chest.setClickEvent(i, () -> onClick(finalI));
		}
		this.chest.openInventory(player);
		this.onEnter();
	}

	public Reward getReward() {
		return this.reward;
	}

	public void setGiveRewardsAtClose(boolean flag) {
		this.giveRewardsAtClose = flag;
	}

	public abstract void onClick(int slot);

	public abstract void onTick();

	public abstract void onEnter();

	public abstract void onExit();

	public boolean isValid() {
		return this.valid;
	}

	@Nullable
	protected ItemStack getItem(int slot) {
		return this.chest.getInventory().getItem(slot);
	}

	protected boolean checkMaterialMatch(int slot, Material material) {
		final ItemStack item = getItem(slot);
		if (item == null) {
			return false;
		}
		return !item.getType().isAir() && item.getType() == material;
	}

	protected void setItem(int slot, ItemStack itemStack) {
		this.chest.setItem(slot, itemStack);
	}

	protected void setClickEvent(int slot, Runnable run) {
		this.chest.setClickEvent(slot, run);
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getBoardSize() {
		return this.getValidSlots().length;
	}

	public int[] getValidSlots() {
		final int[] ints = new int[this.getChest().getSize() - 24];
		int pos = 0;
		for (int i = MIN_SLOT; i <= MAX_SLOT; ++i) {
			if (i % 9 != 0 && i % 9 != 8) {
				ints[pos] = i;
				++pos;
			}
		}
		return ints;
	}

	public void forceKick(String message, boolean grantRewards) {
		if (grantRewards) {
			this.sendMessage(message);
			OldPlayerLib.playSound(this.getPlayer(), Sound.ENTITY_CHICKEN_AMBIENT, 2.0f);
			if (this.getReward().hasAnyRewards() && this.giveRewardsAtClose) {
				this.getReward().grantAll(this.getPlayer(), this.round - 1, this);
			}
		}
		MinigameManager.current().removeAbstractMinigame(this);
	}

	public void forceKick(boolean grantRewards) {
		this.forceKick("The game has ended!", grantRewards);
	}

	public void fillBoard(ItemStack item) {
		this.getChest().fillInner(item);
	}

	public void fillBoard() {
		this.fillBoard(new ItemStack(Material.AIR));
	}

	public ChestInventory getChest() {
		return chest;
	}

	public void openInventory() {
		this.chest.openInventory(this.player);
	}

	public void sendMessage(String message, Object... replacement) {
		Chat.sendMessage(this.getPlayer(), Chat.format("&a&l%s &e%s", this.mini.getName().toUpperCase(), message), replacement);
	}

	public void clearTrash() {

	}

	public Player getPlayer() {
		return player;
	}

	public IMinigame getMini() {
		return mini;
	}

	protected final void setState(State state) {
		this.state = state;
	}

	protected final void setState(boolean bool) {
		this.state = bool ? State.PLAYING : State.SHOWING;
	}

	public State getState() {
		return state;
	}

	protected final void workWithTime() {
		if (this.debug) {
			return;
		}
		if (!state.asBoolean()) {
			return;
		}
		--timeLeft;
		if (timeLeft % 20 == 0) {
			this.updateTimerIcon();
		}
		if (timeLeft <= 0) {
			this.forceKick("Out of time!", true);
		}
	}

	protected final void updateRoundIcon() {
		this.getChest().setItem(4, new ItemBuilder(Material.JUKEBOX)
				.setName("&aRound &l" + this.round)
				.setAmount(this.round)
				.setSmartLore("This displays current round. More rounds you reach - more rewards you will get!")
				.toItemStack());
	}

	protected final void updateGlowStoneIcon(String name) {
		this.getChest().setItem(40, new ItemBuilder(Material.GLOWSTONE)
				.setName("&a" + name)
				.toItemStack());
	}

	protected final void updateTimerIcon() {
		this.setItem(40, new ItemBuilder(Material.CLOCK)
				.setName("&a" + this.timeLeft / 20)
				.setAmount((int)(this.timeLeft / 20))
				.setSmartLore("This is how much time you have left! You should probably play instead of reading this.")
				.toItemStack());
	}

	public void setDebug(boolean b) {
		this.debug = b;
		this.reward = new MinigameReward();
	}

	protected boolean isDebug() {
		return this.debug;
	}

	public void setReward(Reward mutableReward) {
		this.reward = mutableReward;
	}
}
