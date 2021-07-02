package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public enum TamerPacks {

    ZOMBIE_HORDE(new TamerPack("Zombie Horde") {
        @Override
        public void spawn(Player player) {
            for (int i = 0; i < 3; i++) {
                final Location location = this.addRelativeOffset(player.getLocation(), i);
                this.createEntity(location, Zombie.class);
            }
        }
    }),

    SKELETON_GANG(new TamerPack("Skeleton Gang") {
        @Override
        public void spawn(Player player) {
            for (int i = 0; i < 3; i++) {
                this.createEntity(this.addRelativeOffset(player.getLocation(), i), Skeleton.class, me -> me.getEquipment().setItemInMainHand(new CFItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).setUnbreakable().build()));
            }
        }
    }),

    PIGMAN_RUSHED(new TamerPack("Pigman Rusher") {
        @Override
        public void spawn(Player player) {
            this.createEntity(player.getLocation(), PigZombie.class, me -> {
                me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5d);
                me.setAngry(true);
                me.setAnger(Short.MAX_VALUE);
            });
        }
    }),


    VEX(new TamerPack("Vex Brood Brothers") {
        @Override
        public void spawn(Player player) {
            for (int i = 0; i < 2; i++) {
                this.createEntity(addRelativeOffset(player.getLocation(), i), Vex.class);
            }
        }
    }),

    WOLVES(new TamerPack("Wolfies") {
        @Override
        public void spawn(Player player) {
            for (int i = 0; i < 4; i++) {
                this.createEntity(addRelativeOffset(player.getLocation(), i), Wolf.class, me -> {
                    me.setOwner(player);
                    me.setAngry(true);
                    me.setCollarColor(DyeColor.CYAN);
                    me.setAdult();
                });
            }
        }
    }),

    JOCKEY(new TamerPack("Skeleton Jockey") {
        @Override
        public void spawn(Player player) {

            final Location relative = addRelativeOffset(player.getLocation(), 0);

            final Entity skeleton = this.createEntity(relative, Skeleton.class, me -> {
                me.getEquipment().setItemInMainHand(new CFItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 2).build());
            });

            this.createEntity(relative, Spider.class, me -> {
                me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2d);
                me.addPassenger(skeleton);
            });

        }
    }),

    CHICKEN_RIDER(new TamerPack("Chicken Rider") {
        @Override
        public void spawn(Player player) {

            final Location relative = addRelativeOffset(player.getLocation(), 0);

            final Entity zombie = this.createEntity(relative, Zombie.class, me -> {
                me.getEquipment().setItemInMainHand(new CFItemBuilder(Material.NETHERITE_SWORD).setPureDamage(2.0d).build());
                me.setBaby();
            });

            this.createEntity(relative, Chicken.class, me -> me.addPassenger(zombie));

        }
    });

    private final TamerPack pack;

    TamerPacks(TamerPack tamerPack) {
        this.pack = tamerPack;
    }

    public TamerPack getPack() {
        return pack;
    }

    public static TamerPacks random() {
        return CollectionUtils.randomElement(values(), ZOMBIE_HORDE);
    }

}
