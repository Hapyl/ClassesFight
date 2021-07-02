package ru.hapyl.classesfight.quest.broadcast;

import com.google.common.collect.Lists;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.quest.DailyLoginQuestReward;

import java.util.List;

public class Broadcaster implements Runnable {

	private final List<Broadcast> broadcasts;
	private final DailyLoginQuestReward daily;
	private int pos;

	public Broadcaster(JavaPlugin plugin) {
		this.daily = ClassesFight.getSingleton().getDailyRewards();
		this.broadcasts = Lists.newArrayList();
		this.pos = 0;
		this.broadcasts.add(player -> daily.nextDailyIn(player) <= 0 ?
				"Hey, you haven't claimed your daily rewards! Come talk to The Eye when you have time." :
				"Looks like you have claimed your daily rewards today! Next reward is in " + daily.nextDailyInString(player) + ".");
	}

	@Override
	public void run() {
		if (true) {
			return;
		}
		final Broadcast next = broadcasts.get(pos);
		for (final Player online : Bukkit.getOnlinePlayers()) {
			sendBroadcastMessage(online.getPlayer(), next.getMessage(online));
		}
		pos = pos + 1 >= broadcasts.size() ? 0 : pos + 1;
	}

	public void sendBroadcastMessage(Player player, String message) {
		Chat.sendMessage(player, "");
		Chat.sendMessage(player, "&9&lBroadcast> &7" + message);
		Chat.sendMessage(player, "");
		PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_AMBIENT, 0.25f);
	}

}
