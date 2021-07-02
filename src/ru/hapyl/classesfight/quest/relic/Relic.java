package ru.hapyl.classesfight.quest.relic;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.feature.BlockLocation;

import java.util.Optional;

public class Relic {

    private final RelicType type;
    private final BlockLocation coordinates;
    private final GameMap mapZone;
    private final BlockFace face;

    private Integer id;

    public Relic(RelicType type, GameMap zone, BlockLocation location, BlockFace face) {
        if (zone == GameMap.NONE) {
            throw new IllegalArgumentException("Cannot use NONE as Relic zone!");
        }
        this.type = type;
        this.mapZone = zone;
        this.coordinates = location;
        this.face = face;
    }

    public Relic(RelicType type, GameMap zone, BlockFace face, int x, int y, int z) {
        this(type, zone, new BlockLocation(x, y, z), face);
    }

    public Relic(RelicType type, BlockFace face, int x, int y, int z) {
        this(type, GameMap.SPAWN, new BlockLocation(x, y, z), face);
    }

    public Relic(RelicType type, GameMap zone, int x, int y, int z) {
        this(type, zone, new BlockLocation(x, y, z), BlockFace.NORTH_WEST);

    }

    public Relic(RelicType type, BlockLocation coordinates) {
        this(type, GameMap.SPAWN, coordinates, BlockFace.NORTH_WEST);
    }

    public Relic(RelicType type, int x, int y, int z) {
        this(type, new BlockLocation(x, y, z));
    }

    public void setId(int id) {
        this.id = id;
    }

    public RelicType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public BlockLocation getCoordinates() {
        return coordinates;
    }

    public GameMap getMapZone() {
        return mapZone;
    }

    public void revoke(Player player) {
        if (!hasFound(player)) {
            return;
        }
        Database.getDatabase(player).getCollectibleEntry().removeFoundRelic(this);
    }

    public BlockFace getFace() {
        return face;
    }

    public void grant(Player player, boolean flag) {
        if (hasFound(player)) {
            return;
        }
        Database.getDatabase(player).getCollectibleEntry().addFoundRelic(this);
        if (flag) {
            Chat.sendMessage(player, "&d&l%s RELIC &aYou have found a relic!", this.getType().name().toUpperCase());
            PlayerLib.playSound(player, Sound.AMBIENT_CAVE, 2.0f);
        }
    }

    public boolean hasFound(Player player) {
        return Database.getDatabase(player).getCollectibleEntry().hasFoundRelic(this);
    }
}
