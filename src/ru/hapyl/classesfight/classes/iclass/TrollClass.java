package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.AbilitiesCooldown;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class TrollClass extends IClass implements Listener {

    private final List<Location> blocks = new ArrayList<>();

    public TrollClass() {
        super("Troll", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYyNmMwMTljOGI0MWM3YjI0OWFlOWJiNjc2MGM0ZTY5ODAwNTFjZjBkNjg5NWNiM2U2ODQ2ZDgxMjQ1YWQxMSJ9fX0=");

        this.setAttack(EnumInfo.LOW);
        this.setDefense(EnumInfo.LOW);
        this.setRole(ClassRole.STRATEGIST);
        this.setRating(ClassRating.S);
        this.setInfo("Not a good fighter but definitely a good troll!____&e○ Repulsor &e&lSNEAK &7- Launches all nearby__enemies in 5 blocks radius up.____&e○ Spin &7- Turns all enemies on 180 degrees.", "Spider's Nest", "Spawns a batch of cobweb at your position that only visible for your opponents.", 3);
        this.setUltimateSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f);

        final ClassEquipment eq = this.getClassEquipment(true);

        eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYyNmMwMTljOGI0MWM3YjI0OWFlOWJiNjc2MGM0ZTY5ODAwNTFjZjBkNjg5NWNiM2U2ODQ2ZDgxMjQ1YWQxMSJ9fX0=");

        eq.setChestplate(255, 204, 84);
        eq.setLeggings(255, 204, 84);
        eq.setBoots(255, 204, 84);

        eq.addItem(new CFItemBuilder(Material.STICK).setName("&eStickonator").addEnchant(Enchantment.KNOCKBACK, 1).setPureDamage(1.5).build());
        eq.addItem(new CFItemBuilder(Material.NAUTILUS_SHELL, "troll_spin").setName("&aSpin &e&lRIGHT CLICK").withCooldown(300).addClickEvent(this::useSpin).build());

    }

    @Override
    public void onStart(Player player) {
        player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SNEAK" + ChatColor.YELLOW + " to use your Repulsor ability!");
    }

    @Override
    public void useUltimate(Player player) {
        useCobweb(player);
        GameManager.current().forEachBut(who -> Chat.sendMessage(who, "&aAh... Sticky! &e&lPUNCH &athe cobweb to remove it!"), player);
    }

    @Override
    public void onStopOnce() {
        blocks.forEach(loc -> loc.getBlock().getState().update(false, false));
        blocks.clear();
    }

    @EventHandler
    public void handleRepulsor(PlayerToggleSneakEvent ev) {

        final GameManager manager = GameManager.current();
        if (!manager.isGameInProgress()) {
            return;
        }

        Player player = ev.getPlayer();
        if (ClassManager.getClass(player).equals(ClassManager.TROLL)) {
            if (Spectator.isSpectator(player)) {
                return;
            }
            if (!manager.arePlayersRevealed()) {
                player.sendMessage(ChatColor.RED + "You cannot use that while players is not revealed!");
                return;
            }

            if (!Cooldown.isOnCooldown(player.getUniqueId(), "troll_repulsor")) {

                for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
                    if (ent instanceof Player && ent != player && !(Spectator.isSpectator((Player)ent))) {
                        useRepulsor((Player)ent);
                    }
                }

                AbilitiesCooldown.add(player, "Repulsor", "troll_repulsor", 200, g -> Chat.sendTitle(g, "", "&aRepulsor is ready!", 5, 15, 5));
            }

        }
    }

    private void useRepulsor(Player player) {
        player.sendMessage(ChatColor.GREEN + "Whoosh!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.MASTER, 5, 1.8f);
        player.setVelocity(new Vector(0, 1, 0));
    }

    private void useCobweb(Player player) {

        Location loc = player.getLocation();
        Location fake = new Location(loc.getWorld(), loc.getX() - 2, loc.getY(), loc.getZ() - 2);

        for (int i = 0; i < 5; i++) {
            blocks.add(fake.clone().add(i, 0, 0));
            sendChange(player, fake.clone().add(i, 0, 0));
            for (int j = 0; j < 5; j++) {
                blocks.add(fake.clone().add(i, 0, j));
                sendChange(player, fake.clone().add(i, 0, j));
            }
        }

    }

    private void useSpin(Player player) {
        GameManager.current().forEachBut(g -> {
            if (Spectator.isSpectator(g)) {
                return;
            }
            Location loc = g.getLocation();
            loc.setYaw(loc.getYaw() + 180);
            g.teleport(loc);
            g.playSound(g.getLocation(), Sound.ENTITY_BLAZE_HURT, SoundCategory.MASTER, 20, 2);
        }, player);
    }

    private void sendChange(Player player, Location fake) {
        GameManager.current().forEachBut(who -> {
            if (!fake.getBlock().getType().isSolid()) {
                who.sendBlockChange(fake, Material.COBWEB.createBlockData());
            }
        }, player);
    }

}
