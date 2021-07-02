package ru.hapyl.classesfight.quest.relic;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import kz.hapyl.spigotutils.module.math.IntInt;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameMap;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RelicHunt {

    private final Map<Integer, Relic> byId;
    private int totalZones;

    private final Map<RelicType, Integer> perTypeRelics;

    public RelicHunt(JavaPlugin plugin) {
        this.byId = new HashMap<>();
        this.perTypeRelics = new HashMap<>();
        this.loadRelics();
    }

    private void loadRelics() {
        // Lobby
        registerRelic(new Relic(RelicType.AMETHYST, 27, 17, 8));
        registerRelic(new Relic(RelicType.AMETHYST, 32, 17, 0));
        registerRelic(new Relic(RelicType.EMERALD, BlockFace.SOUTH_WEST, -20, 23, 21));
        registerRelic(new Relic(RelicType.AMETHYST, 11, 18, -27));
        registerRelic(new Relic(RelicType.AMETHYST, 7, 17, 23));

        // Arena
        registerRelic(new Relic(RelicType.SAPPHIRE, GameMap.ARENA, -124, 9, -94));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.ARENA, -82, 7, -142));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.ARENA, -132, 19, -98));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.ARENA, -128, 17, -117));

        // Japan
        registerRelic(new Relic(RelicType.GOLDEN, GameMap.JAPAN, 106, 23, -149));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.JAPAN, 96, 3, -130));
        registerRelic(new Relic(RelicType.BEDROCK, GameMap.JAPAN, 94, 11, -129));

        // Greenhouse
        registerRelic(new Relic(RelicType.EMERALD, GameMap.GREENHOUSE, 6, 5, 45));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.GREENHOUSE, 5, 14, 65));

        // Railway
        registerRelic(new Relic(RelicType.SAPPHIRE, GameMap.RAILWAY, 115, 9, 13));
        registerRelic(new Relic(RelicType.SAPPHIRE, GameMap.RAILWAY, 113, 11, -11));
        registerRelic(new Relic(RelicType.EMERALD, GameMap.RAILWAY, 68, 12, 29));

        this.spawnRelics();
        this.totalZones = this.countZonesWithRelics();
    }

    public void registerRelic(Relic relic) {
        final int nextId = this.byId.size();
        relic.setId(nextId);
        this.byId.put(nextId, relic);
        CollectionUtils.addMapValue(this.perTypeRelics, relic.getType(), 1);
    }

    public int getRelicAmount(RelicType type) {
        return this.perTypeRelics.getOrDefault(type, 0);
    }

    public int getFoundRelicAmount(RelicType type, Player player) {
        AtomicInteger i = new AtomicInteger(0);
        byId.forEach((id, relic) -> {
            if (relic.getType() == type && relic.hasFound(player)) {
                i.incrementAndGet();
            }
        });
        return i.get();
    }

    public int getFoundRelics(Player player) {
        final IntInt i = new IntInt(0);
        byId.forEach((id, relic) -> {
            if (relic.hasFound(player)) {
                i.increment();
            }
        });
        return i.get();
    }

    public Set<Relic> getByZone(GameMap map) {
        return byId.values().stream().filter(relic -> relic.getMapZone() == map).collect(Collectors.toSet());
  /*      final Set<Relic> hash = new HashSet<>();
        byId.forEach((id, relic) -> {
            if (relic.getMapZone() == map) {
                hash.add(relic);
            }
        });
        return hash;*/
    }

    private int countZonesWithRelics() {
        return getZones().size();
    }

    public List<GameMap> getZones() {
        final List<GameMap> zones = new ArrayList<>();
        byId.forEach((id, relic) -> CollectionUtils.addIfAbsent(zones, relic.getMapZone()));
        return zones;
    }

    @Nullable
    public Relic getById(int id) {
        return byId.getOrDefault(id, null);
    }

    public int getTotalZones() {
        return totalZones;
    }

    public int getTotalZonesScaled() {
        return (int)Math.ceil((float)this.getTotalZones() / 5);
    }

    @Nullable
    public Relic getByBlock(Block block) {
        if (!(block.getState() instanceof Skull)) {
            return null;
        }
        final Skull skull = (Skull)block.getState();
        final Integer integer = skull.getPersistentDataContainer().get(NamespacedKey.minecraft("relic_id"), PersistentDataType.INTEGER);
        if (integer == null) {
            return null;
        }
        return this.getById(integer);
    }

    public Map<Integer, Relic> getRelics() {
        return byId;
    }

    public Set<Relic> getFoundIn(Player player, GameMap zone) {
        final Set<Relic> relics = new HashSet<>();
        getRelics().forEach((id, relic) -> {
            if (relic.getMapZone() == zone && relic.hasFound(player)) {
                relics.add(relic);
            }
        });
        return relics;
    }

    public void spawnRelics() {
        this.byId.forEach((id, relic) -> {
            final Location spawnLocation = relic.getCoordinates().toLocation();
            final Block block = spawnLocation.getBlock();
            block.setType(Material.PLAYER_HEAD, false);

            // Apply rotation
            if (block.getBlockData() instanceof Rotatable) {
                final Rotatable rotation = (Rotatable)block.getBlockData();
                rotation.setRotation(relic.getFace());
                block.setBlockData(rotation);
            }

            // Apply texture and ID
            if (block.getState() instanceof Skull) {
                final Skull skull = (Skull)block.getState();
                skull.getPersistentDataContainer().set(NamespacedKey.minecraft("relic_id"), PersistentDataType.INTEGER, id);
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", relic.getType().getTexture()));
                try {
                    Field f = skull.getClass().getDeclaredField("profile");
                    f.setAccessible(true);
                    f.set(skull, profile);
                    f.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                skull.update(false, false);
            }
        });
    }

    public static RelicHunt current() {
        return ClassesFight.getSingleton().getRelicHunt();
    }


}
