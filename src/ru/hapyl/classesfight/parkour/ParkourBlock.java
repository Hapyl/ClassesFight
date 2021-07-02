package ru.hapyl.classesfight.parkour;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.hapyl.classesfight.feature.BlockLocation;

public class ParkourBlock extends BlockLocation {

    private Material oldBlockMaterial;
    private final ParkourBlockType type;

    public ParkourBlock(ParkourBlockType type, int x, int y, int z) {
        this(type, x, y, z, 0.0f, 0.0f);
    }

    public ParkourBlock(ParkourBlockType type, int x, int y, int z, float yaw, float pitch) {
        super(x, y, z, yaw, pitch);
        this.type = type;
    }

    public boolean isCheckpoint() {
        return this.type == ParkourBlockType.CHECKPOINT;
    }

    public ParkourBlockType getType() {
        return type;
    }

    public void remove(World world) {
        getBlockAt(world).setType(oldBlockMaterial, false);
    }

    private Block getBlockAt(World world) {
        return world.getBlockAt(this.getX(), this.getY(), this.getZ());
    }

    public void spawn(World world) {
        final Block blockAt = getBlockAt(world);
        this.oldBlockMaterial = blockAt.getType();
        blockAt.setType(this.getType().getMaterial(), false);
    }

}
