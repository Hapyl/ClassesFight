package ru.hapyl.classesfight.minigame.games;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.RubyEntry;
import ru.hapyl.classesfight.experience.reward.RewardStorage;
import ru.hapyl.classesfight.minigame.AbstractMinigame;
import ru.hapyl.classesfight.minigame.IMinigame;
import ru.hapyl.classesfight.minigame.State;
import ru.hapyl.classesfight.minigame.rewards.ExtraReward;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.concurrent.ThreadLocalRandom;

public class Ultrasequencer extends IMinigame {

    public Ultrasequencer() {
        super("Ultrasequencer",
                250,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUyOGJlNmQ0MjYzZGFkY2U2MTNhN2JiYzA0MjNhYTc2YTM5Yzc0ODQ2NzAyZmE4NWMyNzg3YTdmNTNjNCJ9fX0=",
                "&a1. &7Number(s) appear for 2 seconds.__&e2. &7They all disappear!__&c3. &7Click them in order from memory.",
                false);
        this.setReward(super
                .chainPut(1, new ExtraReward(10, 0, 1))
                .putReward(2, new ExtraReward(20, 0, 2))
                .putReward(3, new ExtraReward(30, 0, 5))
                .putReward(4, new ExtraReward(40, 0, 10))
                .putReward(5, new ExtraReward(50, 0, 15))
                .putReward(6, new ExtraReward(75, 0, 15))
                .putReward(7, new ExtraReward(100, 0, 20))
                .putReward(8, new ExtraReward(125, 0, 25))
                .putReward(9, new ExtraReward(150, 0, 30))
                .putReward(10, new ExtraReward(200, 1, 40))
                .putReward(11, new ExtraReward(250, 0, 50))
                .putReward(12, new ExtraReward(350, 0, 70))
                .putReward(13, new ExtraReward(500, 0, 100))
                .putReward(14, new ExtraReward(750, 0, 130))
                .putReward(15, new ExtraReward(1000, 2, 190))
                .putReward(16, new ExtraReward(2000, 3, 260))
                .putReward(21, new ExtraReward(5000, 10, 1000, RewardStorage.COMPLETE_ULTRASEQUENCER))
        );
    }

    private final Material[] VALID_MATERIALS = {
            Material.WHITE_DYE, Material.ORANGE_DYE, Material.MAGENTA_DYE,
            Material.LIGHT_BLUE_DYE, Material.YELLOW_DYE, Material.LIME_DYE,
            Material.PINK_DYE, Material.GRAY_DYE, Material.LIGHT_GRAY_DYE,
            Material.CYAN_DYE, Material.PURPLE_DYE, Material.BLUE_DYE,
            Material.BROWN_DYE, Material.GREEN_DYE, Material.RED_DYE, Material.BLACK_DYE};

    private final ItemStack ICON_HIDDEN_BAR = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("&f").toItemStack();
    private final ItemStack ICON_CLICK_TO_REVEAL = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("&a?").toItemStack();

    private final int TICKS_PER_ROUND_TO_GUESS = 20;

    @Override
    public void newInstance(Player player, boolean debug) {
        // init either here or in onEnter()
        final RubyEntry rubies = Database.getDatabase(player).getRubyEntry();
        new AbstractMinigame(player, this, debug) {

            // 4 seconds + 1 second every round
            private int[] slots;
            private int currentClick = 0;
            private boolean hintUsed;

            private void generateNext() {

                ++this.round;
                this.setState(State.SHOWING);
                this.hintUsed = false;

                this.updateRoundIcon();
                this.updateHintButton();

                this.updateGlowStoneIcon("Remember the sequence!");

                if (this.round > this.getBoardSize()) {
                    // that's it, you won
                    this.forceKick(true);
                    return;
                }

                final int boardSizePlusOne = this.getBoardSize() + 1;
                this.slots = new int[Math.min(this.round, boardSizePlusOne)];
                this.fillBoard(ICON_HIDDEN_BAR);

                for (int i = 0; i < this.round; i++) {
                    // max slot + 1?
                    this.slots[i] = randomSlot();
                }

                // i == item count
                for (int i = 0; i < this.slots.length; i++) {
                    this.getChest().setItem(this.slots[i], getItemOf(i));
                }

                new GameTask() {

                    // give more time at the first round
                    final int maxTimes = round == 1 ? 3 : round;
                    int currentTime = 0;

                    @Override
                    public void run() {

                        if (!isValid()) {
                            this.cancel();
                            return;
                        }

                        if (currentTime >= maxTimes) {
                            this.cancel();
                            hideAndAllowPlayerToPlayer();
                            return;
                        }

                        PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, Math.min(0.5f + (0.05f * currentTime), 2.0f));
                        ++currentTime;

                    }
                }.runTaskTimer(15, 15);

            }

            private void updateHintButton() {
                this.setItem(42, new ItemBuilder(Material.DEAD_BUSH)
                        .setName("&aHint")
                        .addLore("&8Reveals next click")
                        .addLore("&8+1 Timer Second")
                        .addLore("")
                        .addLore("Cost")
                        .addLore("&a 1 Ruby")
                        .addLore()
                        .addLore("&cNote: Using hints will reduce rewards.")
                        .addLore("&cYou can only use one hint per round!")
                        .addLore()
                        .addLore(this.hintUsed ? "&c&lAlready used!" : (rubies.getRubies() > 0 ? "&eClick to reveal!" : "&cNot enough rubies!"))
                        .toItemStack());

                this.setClickEvent(42, () -> {
                    if (this.hintUsed) {
                        Chat.sendMessage(player, "&cYou have already used a hint!");
                        GameUtils.villagerNo(player);
                        return;
                    }
                    if (getState().asBoolean()) {
                        if (rubies.hasRubies(1)) {
                            this.revealNextClick();
                            this.timeLeft += 20;
                            rubies.removeRubies(1);
                            this.updateHintButton();
                        }
                        else {
                            Chat.sendMessage(player, "&cNot enough rubies!");
                            GameUtils.villagerNo(player);
                        }
                        return;
                    }
                    Chat.sendMessage(player, "&cHints aren't available in generating state.");
                    GameUtils.villagerNo(player);
                });
            }


            private void revealNextClick() {
                ++this.hintsUsed;
                this.hintUsed = true;
                this.timeLeft += 20;
                this.getChest().setItem(this.slots[this.currentClick], new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setName("&aHinted Slot!")
                        .addSmartLore("Click me!")
                        .toItemStack());
                PlayerLib.playSound(this.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
            }

            @Override
            public void onClick(int slot) {
                if (getState().asBoolean()) {
                    if (this.slots.length >= this.currentClick
                            && this.slots[this.currentClick] == slot) {
                        revealAt(slot, this.currentClick);
                        ++this.currentClick;
                        if (this.currentClick >= this.slots.length) {
                            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                            generateNext();
                        }
                    }
                    else {
                        final ItemStack item = this.getChest().getInventory().getItem(slot);
                        // don't kick if clicked at already revealed one
                        if (item != null) {
                            if (item.getType() == Material.WHITE_STAINED_GLASS_PANE) {
                                forceKick(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTick() {
                this.workWithTime();
            }

            @Override
            public void onEnter() {
                this.generateNext();
            }

            @Override
            public void onExit() {

            }

            private void hideAndAllowPlayerToPlayer() {
                this.getChest().fillInner(ICON_CLICK_TO_REVEAL);
                this.currentClick = 0;
                this.timeLeft = 80 + (this.round * TICKS_PER_ROUND_TO_GUESS);
                this.updateTimerIcon();
                this.setState(State.PLAYING);
            }

            private ItemStack getItemOf(int index) {
                Material current = index > (VALID_MATERIALS.length - 1) ? VALID_MATERIALS[index - (VALID_MATERIALS.length - 1)] : VALID_MATERIALS[index];
                final int validIndex = Math.max(1, Math.min(index + 1, 64));
                return new ItemBuilder(current).setAmount(validIndex).setAmount(validIndex).setName("&a" + validIndex).toItemStack();
            }

            private void revealAt(int slot, int index) {
                PlayerLib.playSound(this.getPlayer(), Sound.ENTITY_ITEM_PICKUP, Math.min(0.5f + (0.05f * this.currentClick), 2.0f));
                this.getChest().setItem(slot, getItemOf(index));
            }

            private int randomSlot() {
                try {
                    final int random = this.getValidSlots()[ThreadLocalRandom.current().nextInt(0, this.getBoardSize())];
                    return inArray(random) ? randomSlot() : random;
                } catch (StackOverflowError ignored0) {
                    Chat.sendMessage(this.getPlayer(), "&cAn error occurred while playing a minigame, your rewards were saved and granted.");
                    this.forceKick(true);
                }
                return 0;
            }

            private boolean inArray(int slot) {
                for (int i : this.slots) {
                    if (i == slot) {
                        return true;
                    }
                }
                return false;
            }

        };

    }
}
