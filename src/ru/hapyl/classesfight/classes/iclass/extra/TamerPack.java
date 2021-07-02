package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.SoundLib;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

import java.util.HashSet;
import java.util.Set;

public abstract class TamerPack {

    private final double[][] relativeOffsets = {
            {-1.0d, 0.0d},
            {1.0d, 0.0d},
            {0.0d, -1.0d},
            {0.0d, 1.0d},
            {1.0d, 1.0d},
            {-1.0d, 1.0d},
            {1.0d, -1.0d},
            {-1.0d, -1.0d}
    };

    private final String name;
    private final Set<GarbageEntity<?>> entities;

    protected TamerPack(String name) {
        this.name = name;
        this.entities = new HashSet<>();
    }

    public abstract void spawn(Player player);

    public final void recall() {
        entities.forEach(entity -> {
            PlayerLib.spawnParticle(entity.getEntity().getLocation().add(0, 1, 0), Particle.EXPLOSION_NORMAL, 10, 0.5d, 0.5d, 0.5d, 0.2f);
            SoundLib.play(entity.getEntity().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.2f);
            entity.kill();
        });
    }

    public String getName() {
        return name;
    }

    public Set<GarbageEntity<?>> getEntities() {
        return entities;
    }

    public boolean isInPack(Entity entity) {
        for (GarbageEntity<?> garbageEntity : this.entities) {
            if (garbageEntity.getEntity() == entity) {
                return true;
            }
        }
        return false;
    }

    protected final <T extends Entity> Entity createEntity(Location location, Class<T> spawnClass) {
        return this.createEntity(location, spawnClass, null);
    }

    protected final <T extends Entity> Entity createEntity(Location location, Class<T> spawnClass, Consumer<T> consumer) {
        final GarbageEntity<T> entity = new GarbageEntity<>(spawnClass);
        entity.spawn(location, me -> {

            if (!(me instanceof LivingEntity)) {
                throw new IllegalArgumentException(spawnClass.getSimpleName() + " is not a LivingEntity!");
            }
            LivingEntity livingMe = (LivingEntity)me;

            livingMe.setMaxHealth(30d);
            livingMe.setHealth(30d);
            livingMe.getEquipment().setHelmet(new CFItemBuilder(Material.LEATHER_HELMET).setUnbreakable().setLeatherArmorColor(Color.fromRGB(509659)).build());
            livingMe.addScoreboardTag("TAMER_ENTITY");

            if (consumer != null) {
                consumer.accept(me);
            }

        });
        this.entities.add(entity);
        return entity.getEntity();
    }

    public void updateName(Player player) {
        this.entities.forEach(entity -> {
            final LivingEntity ent = (LivingEntity)entity.getEntity();
            ent.setCustomName(Chat.format("&a%s's %s &8| &c%s❤&8", player.getName(), Chat.capitalize(ent.getType()), BukkitUtils.decimalFormat(ent.getHealth())));
            ent.setCustomNameVisible(true);
        });
    }

    protected final Location addRelativeOffset(Location location, int offset) {
        final double[] doubles = relativeOffsets[offset >= relativeOffsets.length ? 0 : offset];
        return location.clone().add(doubles[0], 0.0d, doubles[1]);
    }

}
