package ru.hapyl.classesfight.minigame.games;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.experience.reward.RarePackageReward;
import ru.hapyl.classesfight.minigame.AbstractMinigame;
import ru.hapyl.classesfight.minigame.IMinigame;
import ru.hapyl.classesfight.minigame.State;
import ru.hapyl.classesfight.minigame.games.superpair.Pair;
import ru.hapyl.classesfight.minigame.games.superpair.PairType;
import ru.hapyl.classesfight.minigame.rewards.MutableReward;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Superpairs extends IMinigame {

    public Superpairs() {
        super("Superpairs",
                500,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RjOTg1YTdhNjhjNTc0ZjY4M2MwYjg1OTUyMWZlYjNmYzNkMmZmYTA1ZmEwOWRiMGJhZTQ0YjhhYzI5YjM4NSJ9fX0=",
                "Find &bpairs &7of items on the grid to unlock them.");
        this.setPrefixLore("Rewards__&a Superpairs rewards purely based on__&a what pares you will find!");
    }

    final RarePackageReward SMALL_REWARD = new RarePackageReward("Small", Material.CHEST)
            .addCoins(100)
            .addExp(50)
            .addRuby(1);

    final RarePackageReward MEDIUM_REWARD = new RarePackageReward("Medium", Material.CHEST, 2)
            .addCoins(500)
            .addExp(150)
            .addRuby(3);

    final RarePackageReward LARGE_REWARD = new RarePackageReward("Large", Material.CHEST_MINECART)
            .addCoins(1000)
            .addExp(300)
            .addRuby(5);

    final RarePackageReward EXTRA_LARGE_REWARD = new RarePackageReward("Big", Material.CHEST_MINECART, 2)
            .addCoins(5000)
            .addExp(500)
            .addRuby(7);

    final RarePackageReward INSANE_REWARD = new RarePackageReward("Insane", Material.ENDER_CHEST)
            .addCoins(10000)
            .addExp(1000)
            .addRuby(10);

    /**
     * 3 extra items (more clicks, free find, ???)
     */

    @Override
    public void newInstance(Player player, boolean flag) {
        new AbstractMinigame(player, this, flag, false) {

            private int clicksLeft;
            private boolean click;
            private Set<Pair> pairs;
            private Set<Pair> foundPairs;
            private int lastClick;

            private boolean nextClickRevealPair;

            @Override
            public void onClick(int slot) {

                final Pair pair = getBySlot(slot);

                if (slot == -1) {
                    return;
                }

                if (pair == null) {
                    return;
                }

                if (!getState().asBoolean()) {
                    return;
                }

                if (clicksLeft <= 0) {
                    setState(State.SHOWING);
                    forceKick("You run out of clicks!", true);
                    return;
                }

                if (hasFoundPair(slot) || isRevealed(slot)) {
                    return;
                }

                // *--* Can Proceed Click *--* //

                // Power Up Impl
                if (nextClickRevealPair) {

                    if (pair.getSecondPair() == -1) {
                        validatePowerUp(pair, slot);
                        return;
                    }

                    reveal(pair);
                    click = true;
                    lastClick = -999;
                    nextClickRevealPair = false;

                    return;
                }

                // Power Up check
                if (pair.getSecondPair() == -1) {
                    this.validatePowerUp(pair, slot);
                    return;
                }

                --clicksLeft;

                // Reveal first or second?
                if (click) {
                    revealAt(slot);
                }
                else {
                    revealAndCheck(slot);
                }

                // Set clicks count or cauldron?
                this.updateClickIcon(clicksLeft <= 0);
                // Invert click
                click = !click;
                // reset last click if back to 0, else assign to current
                lastClick = click ? -999 : slot;

            }

            private void validatePowerUp(Pair pair, int slot) {
                if (pair.getPowerUp() == null) {
                    return;
                }

                switch (pair.getPowerUp()) {
                    case REVEAL_NEXT_CLICK: {
                        this.setItem(slot, new ItemBuilder(Material.DIAMOND)
                                .glow()
                                .setName("&6&lPower-Up!")
                                .setSmartLore("Your next click will reveal both pairs!")
                                .toItemStack());
                        fillEmptyPairsSpots(Material.LIME_STAINED_GLASS_PANE, "&a&lClick to reveal pair!");
                        this.playPowerUpSound();
                        nextClickRevealPair = true;
                        break;
                    }
                    case ADDITIONAL_CLICKS: {
                        int clicksToAdd = ThreadLocalRandom.current().nextInt(2, 5);
                        this.clicksLeft += clicksToAdd;
                        this.setItem(slot, new ItemBuilder(Material.FEATHER)
                                .glow()
                                .setName("&6&lInstant Power-Up!")
                                .addLore("&7+%s Clicks", clicksToAdd)
                                .toItemStack());
                        this.playPowerUpSound();
                        this.updateClickIcon();
                        break;
                    }

                    case ADDITIONAL_TIME: {
                        int secondsToAdd = ThreadLocalRandom.current().nextInt(3, 8);
                        this.timeLeft += (secondsToAdd * 20);
                        this.setItem(slot, new ItemBuilder(Material.CLOCK)
                                .glow()
                                .setName("&6&lInstant Power-Up!")
                                .addLore("&7+%s Seconds", secondsToAdd)
                                .toItemStack());
                        this.playPowerUpSound();
                        this.updateTimerIcon();
                        break;
                    }

                    default: {
                        break;
                    }

                }
            }

            private void revealAndCheck(int slot) {
                final Pair pair = this.getBySlot(slot);
                if (pair == null) {
                    return;
                }
                this.revealAt(slot);
                fillEmptyPairsSpots("&c");
                if (pair.contains(slot) && pair.contains(this.lastClick)) {
                    this.reveal(pair);
                }
                else {
                    OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_BURP, 1.35f);
                    setState(State.SHOWING);
                    final int savedLastClick = lastClick;
                    new GameTask() {
                        @Override
                        public void run() {
                            if (isValid()) {
                                hide("&aClick the first pair!", slot, savedLastClick);
                                setState(State.PLAYING);
                            }
                            else {
                                this.cancel();
                            }
                        }
                    }.runTaskLater(20);
                }
            }

            private void reveal(Pair pair) {
                // found pair
                this.foundPairs.add(pair);
                OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                this.setItem(pair.getFirstPair(), pair.getItemOrCreate());
                this.setItem(pair.getSecondPair(), pair.getItemOrCreate());
                fillEmptyPairsSpots("&aClick the first pair!");
            }

            private void generate() {

                this.lastClick = -999;
                this.clicksLeft = (21 - 3) / 2;
                this.timeLeft = (30 * 20);
                this.pairs = new HashSet<>();
                this.foundPairs = new HashSet<>();

                /**
                 * @implNote Needs MORE rewards
                 */

                for (int i = 0; i < (21 - 3) / 2; ++i) {
                    final float chance = ThreadLocalRandom.current().nextFloat();
                    final Pair pair = new Pair(randomSlots());

                    // 45% coins
                    if (checkChance(chance, 0.0f, 0.45f)) {
                        this.pairs.add(pair.addReward(PairType.COINS.random(), 0, 0));
                    }
                    // 25% exp
                    else if (checkChance(chance, 0.45f, 0.7f)) {
                        this.pairs.add(pair.addReward(0, PairType.RUBIES.random(), 0));

                    }
                    // 15% ruby
                    else if (checkChance(chance, 0.7f, 0.85f)) {
                        this.pairs.add(pair.addReward(0, 0, PairType.EXP.random()));
                    }
                    // else special, for now 69, 69, 69 to indicate it's special
                    else {

                        final float nextFloat = ThreadRandom.nextFloat();

                        // 50% small
                        if (checkChance(nextFloat, 0.0f, 0.5f)) {
                            this.pairs.add(pair.addRareReward(SMALL_REWARD));
                        }
                        // 35% medium
                        else if (checkChance(nextFloat, 0.5f, 0.75f)) {
                            this.pairs.add(pair.addRareReward(MEDIUM_REWARD));
                        }
                        // 20% large
                        else if (checkChance(nextFloat, 0.75f, 0.95f)) {
                            this.pairs.add(pair.addRareReward(LARGE_REWARD));
                        }

                        // 0.05% extra large
                        else if (checkChance(nextFloat, 0.95f, 0.995f)) {
                            this.pairs.add(pair.addRareReward(EXTRA_LARGE_REWARD));
                        }

                        // <0.05% in fucking sane large!
                        else {
                            this.pairs.add(pair.addRareReward(INSANE_REWARD));
                        }

                    }
                }

                // temp
                if (isDebug()) {
                    final ItemBuilder builder = new ItemBuilder(Material.PAPER).setName("&aCheat Sheet");
                    int i = 0;
                    for (Pair pair : this.pairs) {
                        builder.addLore("&7%s. %s - %s", ++i, pair.getFirstPair(), pair.getSecondPair());
                    }
                    this.setItem(44, builder.toItemStack());
                }

                this.fillEmptyPairsSpots("Click the first pair!");
                this.click = true;
                setState(State.PLAYING);
                this.updateTimerIcon();
                this.updateClickIcon();

                final List<Integer> freeSlots = this.findFreeSlots();

                while (!freeSlots.isEmpty()) {
                    if (this.pairs.size() == 21) {
                        break;
                    }
                    final int slot = freeSlots.remove(ThreadLocalRandom.current().nextInt(freeSlots.size()));
                    this.pairs.add(new Pair(slot)
                            .setPowerUp(Pair.PowerUpType.values()[ThreadLocalRandom.current().nextInt(Pair.PowerUpType.values().length)]));
                }

            }

            private void revealAt(int slot) {
                final Pair pair = getBySlot(slot);
                if (pair == null) {
                    return;
                }

                OldPlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, (float)Math.max(0, Math.min(2.0f, 0.0f + (0.15f * ThreadLocalRandom.current().nextDouble()))));
                if (pair.contains(slot)) {
                    this.setItem(slot, getPairItem(pair));
                    this.fillEmptyPairsSpots("&eClick the second pair!");
                }

            }

            private boolean isRevealed(int slot) {
                return !isValidItem(this.getItem(slot));
            }

            private boolean hasFoundPair(int slot) {
                if (this.foundPairs.isEmpty()) {
                    return false;
                }
                for (Pair foundPair : this.foundPairs) {
                    if (foundPair.contains(slot)) {
                        return true;
                    }
                }
                return false;
            }

            @Nullable
            private Pair getBySlot(int slot) {
                for (Pair pair : this.pairs) {
                    if (pair.contains(slot)) {
                        return pair;
                    }
                }
                return null;
            }

            private void updateClickIcon(boolean b) {
                if (b) {
                    this.setItem(4, new ItemBuilder(Material.CAULDRON)
                            .setName("&cOut of Clicks!")
                            .setSmartLore("That's it, Game Over!")
                            .toItemStack());
                }
                else {
                    this.updateClickIcon();
                }
            }

            private void updateClickIcon() {
                this.setItem(4, new ItemBuilder(Material.FEATHER)
                        .setName("&aClicks Left &l" + this.clicksLeft)
                        .setAmount(Math.max(this.clicksLeft, 0))
                        .setSmartLore("This is how many click left!").toItemStack());
            }

            private void hide(Material material, String name, int... slots) {
                for (int slot : slots) {
                    final ItemStack theItem = new ItemBuilder(material).setName("&a" + name).toItemStack();
                    if (isDebug()) {
                        theItem.setAmount(slot);
                    }
                    this.setItem(slot, theItem);
                }
            }

            private void hide(String name, int... slots) {
                this.hide(Material.GRAY_STAINED_GLASS_PANE, name, slots);
            }

            private boolean isValidItem(ItemStack item) {
                if (item == null || item.getType().isAir()) {
                    return true;
                }
                switch (item.getType()) {
                    case GRAY_STAINED_GLASS_PANE:
                    case LIME_STAINED_GLASS_PANE:
                        return true;
                }
                return false;
            }

            private void fillEmptyPairsSpots(Material material, String name) {
                for (int validSlot : this.getValidSlots()) {
                    final ItemStack item = this.getItem(validSlot);
                    if (isValidItem(item)) {
                        this.hide(material, name, validSlot);
                    }
                }
            }

            private void fillEmptyPairsSpots(String name) {
                this.fillEmptyPairsSpots(Material.GRAY_STAINED_GLASS_PANE, name);
            }

            @Override
            public void onTick() {
                this.workWithTime();
            }

            private void playPowerUpSound() {
                OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
                OldPlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.75f);
            }

            private List<Integer> findFreeSlots() {
                final List<Integer> slots = CollectionUtils.intArrayToList(this.getValidSlots());
                for (Pair pair : this.pairs) {
                    slots.removeIf(pair::contains);
                }
                return slots;
            }

            private ItemStack getPairItem(Pair pair) {
                return pair.getItemOrCreate();
            }

            private int[] randomSlots() {
                final int first = randomSlot(-1);
                final int second = randomSlot(first);
                return new int[]{first, second};
            }

            private boolean checkChance(float chance, float a, float b) {
                return chance >= a && chance < b;
            }

            private int randomSlot(int first) {
                try {
                    final int random = this.getValidSlots()[ThreadLocalRandom.current().nextInt(0, this.getBoardSize())];
                    return isTakenSlot(first, random) ? randomSlot(first) : random;
                } catch (StackOverflowError error) {
                    Chat.sendMessage(this.getPlayer(), "&cAn error occurred while playing a minigame, your rewards were saved and granted.");
                    this.forceKick(true);
                }
                return 0;
            }

            private boolean isTakenSlot(int first, int slot) {
                if (first != -1 && (first == slot)) {
                    return true;
                }
                if (this.pairs.isEmpty()) {
                    return false;
                }
                for (Pair pair : this.pairs) {
                    if (pair.getFirstPair() == slot || pair.getSecondPair() == slot) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onEnter() {
                this.generate();
                this.setReward(new MutableReward());
            }

            @Override
            public void onExit() {
                final MutableReward reward = (MutableReward)this.getReward();
                for (Pair pair : this.foundPairs) {
                    reward.addCoins(pair.getCoins());
                    reward.addRubies(pair.getRubies());
                    reward.addExp(pair.getExp());
                    reward.addRareReward(pair.getRare());
                }
                reward.grantAll(player, -1, this);
            }
        };
    }
}
