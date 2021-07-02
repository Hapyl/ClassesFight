package ru.hapyl.classesfight.cosmetics.death;

import kz.hapyl.spigotutils.module.chat.Chat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

import java.util.Random;

public class LastMessage extends AbstractEffect {

    public LastMessage() {
        super("Last Message", "", 1000, Material.OAK_SIGN, ShopItemRarity.EPIC);
        this.setLore("Spawns a hologram with last words of yours. Don't say anything rude, please.__" + getString());
    }

    private final String[] WORDS = new String[]{"Shit.", "Oh come on now!", "I was lagging!", "Hacker.", "You just got lucky.", "Well, I tried..."};

    private String getString() {
        String str = "__&7Available words:";
        for (String word : WORDS) {
            str = str.concat("__&b - " + word);
        }
        return str;
    }

    @Override
    public void display(Player player) {
        spawn(player.getLocation().add(0, .25, 0), ChatColor.YELLOW + player.getName() + "'s Last Words were");
        spawn(player.getLocation(), ChatColor.AQUA + ChatColor.BOLD.toString() + WORDS[new Random().nextInt(WORDS.length)]);
    }

    private void spawn(Location location, String display) {
        new GarbageEntity<>(ArmorStand.class).spawn(location, me -> {
            me.setVisible(false);
            me.setSmall(true);
            me.setMarker(true);
            me.setInvulnerable(true);
            me.setCustomName(Chat.format(display));
            me.setCustomNameVisible(true);
        });
    }

}
