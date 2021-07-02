package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import kz.hapyl.spigotutils.module.math.IntInt;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Booleans;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourManager;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.utils.OldPlayerLib;
import ru.hapyl.classesfight.utils.RomanNumber;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class ParkourChallengeMenu extends PlayerGUI {

    private final Parkour parkour;
    private final StatsContainer stats = ClassesFight.getSingleton().getStatsContainer();

    public ParkourChallengeMenu(Player player, Parkour parkour, GUI from) {
        super(player, 3, parkour.getName() + " Challenges");
        this.parkour = parkour;
        this.setArrowBack(from);
        this.updateMenu();
    }

    private void updateMenu() {
        final Map<Long, Reward> challengeRewards = parkour.getCompletionRewards();
        if (challengeRewards.isEmpty()) {
            this.setItem(13, new ItemBuilder(Material.CAULDRON).setName("&cNo Challenges").setSmartLore("This parkour doesn't have any challenges yet!").build());
            return;
        }

        final SmartComponent smart = this.newSmartComponent();
        final IntInt integer = new IntInt(1);

        challengeRewards.keySet().stream().sorted(Collections.reverseOrder()).forEach(((time) -> {

            final Reward reward = challengeRewards.get(time);

            if (reward == null) {
                throw new NullPointerException("Null reward on NotNull?");
            }

            final String databasePath = "pa_ch." + parkour.getName().toLowerCase(Locale.ROOT).replace(" ", "-") + time;
            final boolean hasCompleted = Booleans.isTrue(this.getPlayer(), databasePath);
            final long completionTime = stats.getParkourStat(parkour, getPlayer(), StatsContainer.ParkourStat.TIME);

            final ItemBuilder builder =
                    new ItemBuilder(Material.DIAMOND)
                            .setAmount(integer.get())
                            .setName("&aTime Challenge " + RomanNumber.toRoman(integer.getAndIncrement()))
                            .predicate(hasCompleted, ItemBuilder::glow)
                            .setSmartLore("Complete parkour in set time limit to claim these rewards:");

            builder.addLore();

            final long rewardCoins = reward.coinsReward();
            final long rewardRubies = reward.rubiesReward();
            final RareReward rewardRare = reward.getRareReward();

            if (rewardCoins > 0) {
                builder.addLore(" &e%s Coins", rewardCoins);
            }

            if (rewardRubies > 0) {
                builder.addLore(" &e%s Rubies", rewardRubies);
            }

            if (rewardRare != null) {
                builder.addLore(" &e" + rewardRare.getName());
            }

            builder.addLore();
            builder.addLore("&bCompletion Goal");
            builder.addLore(" &6" + millisIntoTime(time));
            builder.addLore();
            builder.addLore("&bYour Time");
            builder.addLore(" &6" + (completionTime == Long.MAX_VALUE ? "Not yet finished" : millisIntoTime(completionTime)));
            builder.addLore();

            if (completionTime <= time) {
                if (hasCompleted) {
                    builder.addLore("&cRewards already claimed!");
                    smart.add(builder.build());
                }
                else {
                    builder.addLore("&aClick to claim rewards!");
                    smart.add(builder.build(), pl -> {
                        Booleans.setValue(pl, databasePath, true);
                        Database.getCoins(pl).addCoins(rewardCoins);
                        Database.getRubies(pl).addRubies(rewardRubies);
                        if (rewardRare != null) {
                            rewardRare.grantReward(pl);
                        }
                        ParkourManager.parkourMessage(pl, "Challenge rewards claimed!");
                        OldPlayerLib.playSound(getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                        this.updateMenu();
                    });
                }
            }
            else {
                builder.addLore("&cNot eligible to claim rewards!");
                smart.add(builder.build());
            }

        }));

        smart.fillItems(this);
        this.openInventory();

    }

    private String millisIntoTime(long millis) {
        return new SimpleDateFormat("mm:ss.SS").format(millis);
    }

}
