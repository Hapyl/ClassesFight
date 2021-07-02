package ru.hapyl.classesfight.minigame.games;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.minigame.AbstractMinigame;
import ru.hapyl.classesfight.minigame.IMinigame;
import ru.hapyl.classesfight.minigame.State;
import ru.hapyl.classesfight.minigame.rewards.ExtraReward;
import ru.hapyl.classesfight.minigame.rewards.MinigameReward;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Chronomatron extends IMinigame {

    private final ChronoButton BLACK = new ChronoButton("&8Black", Material.BLACK_STAINED_GLASS, Material.BLACK_TERRACOTTA, 0.4f, 19, 28);
    private final ChronoButton RED = new ChronoButton("&cRed", Material.RED_STAINED_GLASS, Material.RED_TERRACOTTA, 0.6f, 11, 20);
    private final ChronoButton ORANGE = new ChronoButton("&6Orange", Material.ORANGE_STAINED_GLASS, Material.ORANGE_TERRACOTTA, 0.8f, 21, 30);
    private final ChronoButton YELLOW = new ChronoButton("&eYellow", Material.YELLOW_STAINED_GLASS, Material.YELLOW_TERRACOTTA, 1.0f, 13, 22);
    private final ChronoButton GREEN = new ChronoButton("&2Green", Material.GREEN_STAINED_GLASS, Material.GREEN_TERRACOTTA, 1.2f, 23, 32);
    private final ChronoButton LIME = new ChronoButton("&aLime", Material.LIME_STAINED_GLASS, Material.LIME_TERRACOTTA, 1.4f, 15, 24);
    private final ChronoButton BLUE = new ChronoButton("&9Blue", Material.BLUE_STAINED_GLASS, Material.BLUE_TERRACOTTA, 1.6f, 25, 34);

    private final Set<ChronoButton> BUTTONS = new HashSet<>();
    private final ItemStack ICON_BACKGROUND = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName("&f").toItemStack();

    public Chronomatron() {
        super("Chronomatron",
                1000,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRlOTFhNmI1NjVlZjM4NmJjZTU3ZTg3YWVjZWMxODBiOWYyZGU4OWY3NmIxNDY1OGE5MTg0ZWYxMTQ4ZTcifX19",
                "&aRepeat &7the musical &dpattern &7to form the longest chain of notes.");
        CollectionUtils.addAll(BUTTONS, BLACK, RED, ORANGE, YELLOW, GREEN, LIME, BLUE);
        this.setReward(new MinigameReward()
                .putReward(1, new ExtraReward(50))
                .putReward(2, new ExtraReward(100))
        );
    }

    // constants
    private final int MAX_ALLOWED_ROUNDS = 21;
    private final int PER_NOTE_TICKS = 15;

    @Nullable
    private ChronoButton getBySlot(int slot) {
        for (ChronoButton button : BUTTONS) {
            if (button.hasSlot(slot)) {
                return button;
            }
        }
        return null;
    }

    private ChronoButton randomButton() {
        return CollectionUtils.randomElement(BUTTONS, RED);
    }

    @Override
    public void newInstance(Player player, boolean debug) {
        new AbstractMinigame(player, this, debug) {

            private List<ChronoButton> toPress;
            private int currentClick;

            @Override
            public void onClick(int slot) {
                if (!getState().asBoolean()) {
                    return;
                }
                final ChronoButton button = getBySlot(slot);
                if (button != null) {

                    if (!isValidClick(slot)) {
                        this.forceKick("You clicked the wrong button!", true);
                        return;
                    }

                    final ChronoButton next = toPress.get(currentClick);
                    clickButton(next);

                    if (currentClick == toPress.size() - 1) {
                        this.dingNext();
                        OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                        return;
                    }

                    ++currentClick;

                }
            }

            private boolean isValidClick(int slot) {
                if (currentClick >= toPress.size()) {
                    return false;
                }
                return toPress.get(currentClick).hasSlot(slot);
            }

            private void clickButton(ChronoButton button) {

                button.playSound(this.getPlayer());
                for (int slot : button.getSlots()) {
                    this.setItem(slot, button.toItemPressed());
                }

                new GameTask() {
                    @Override
                    public void run() {

                        if (!isValid()) {
                            return;
                        }

                        for (int slot : button.getSlots()) {
                            setItem(slot, button.toItem());
                        }
                    }
                }.runTaskLater(PER_NOTE_TICKS - (PER_NOTE_TICKS / 3));
            }

            private void dingNext() {

                setState(State.SHOWING);
                ++this.round;
                this.currentClick = 0;

                if (this.round > MAX_ALLOWED_ROUNDS) {
                    // have to add rounds limit, might remove later and add multiplier on rewards
                    this.forceKick(true);
                    return;
                }

                this.timeLeft = 40 + (round * 20);
                this.updateRoundIcon();
                this.updateGlowStoneIcon("Remember the sequence!");
                this.toPress.add(randomButton());
                this.debugPaper();

                new GameTask() {

                    private int count = 0;

                    @Override
                    public void run() {

                        if (!isValid()) {
                            this.cancel();
                            return;
                        }

                        if (count >= toPress.size()) {
                            allowPlayerToPlay();
                            this.cancel();
                            return;
                        }

                        final ChronoButton next = toPress.get(count);
                        clickButton(next);

                        // Have to add here
                        ++count;

                    }
                }.runTaskTimer(PER_NOTE_TICKS, PER_NOTE_TICKS);
            }

            @Override
            public void onTick() {
                this.workWithTime();
            }

            private void allowPlayerToPlay() {
                this.setState(State.PLAYING);
                this.updateTimerIcon();
            }

            private void debugPaper() {
                if (!this.isDebug()) {
                    return;
                }
                final ItemBuilder builder = new ItemBuilder(Material.PAPER).setName("&aCheat Sheet");
                for (ChronoButton press : toPress) {
                    builder.addSmartLore(press.getName());
                }
                this.setItem(44, builder.toItemStack());
            }

            @Override
            public void onEnter() {
                this.toPress = new ArrayList<>();
                this.fillButtons();
            }

            private void fillButtons() {
                this.getChest().fillInner(ICON_BACKGROUND);
                for (ChronoButton button : BUTTONS) {
                    for (int slot : button.getSlots()) {
                        this.setItem(slot, button.toItem());
                    }
                }
                dingNext();
            }

            @Override
            public void onExit() {

            }
        };
    }

}
