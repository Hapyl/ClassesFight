package ru.hapyl.classesfight.quest.eye;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.menu.ParkourMenu;
import ru.hapyl.classesfight.quest.DailyLoginQuestMenu;
import ru.hapyl.classesfight.quest.DailyLoginQuestReward;
import ru.hapyl.classesfight.quest.relic.RelicMenu;
import ru.hapyl.classesfight.quest.relic.RelicType;

public class TheEyeMenu extends PlayerGUI {

    public TheEyeMenu(Player player) {
        super(player, "The Eye", 3);
        this.setUpItems();
    }

    private void setUpItems() {
        // Relics
        this.setItem(11, ItemBuilder.playerHead(RelicType.AMETHYST.getTexture()).setName("&aRelic Hunt").setSmartLore("There are relics all over this world. Try and find them all to get unique reward!").addLore().addLore("&eClick to open relic hunt menu").toItemStack(), RelicMenu::new);

        // Parkour
        this.setItem(13, new ItemBuilder(Material.RABBIT_FOOT).setName("&aParkour Challenges").setSmartLore("Challenge yourself to complete parkour to be the number one!").addLore().addLore("&eClick to open parkour menu").toItemStack(), ParkourMenu::new);

        // Daily
        final DailyLoginQuestReward daily = ClassesFight.getSingleton().getDailyRewards();
        final long nextDaily = daily.nextDailyIn(getPlayer());

        this.setItem(15, new ItemBuilder(Material.CHEST).setName("&aDaily Rewards").setSmartLore("Login daily to claim your daily rewards!").addLore().addLore(nextDaily <= 0 ? "&6Reward available!" : "&cNext reward in " + daily.nextDailyInString(getPlayer())).addLore().addLore("&eClick to open rewards menu").toItemStack(), g -> new DailyLoginQuestMenu(g, this));
    }

}
