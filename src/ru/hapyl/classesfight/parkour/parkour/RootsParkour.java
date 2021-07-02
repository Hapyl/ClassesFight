package ru.hapyl.classesfight.parkour.parkour;

import org.bukkit.Material;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourBlock;
import ru.hapyl.classesfight.parkour.ParkourBlockType;

public class RootsParkour extends Parkour {

    public RootsParkour(String name) {
        super(name, new ParkourBlock(ParkourBlockType.START, 26, 16, -9), new ParkourBlock(ParkourBlockType.FINISH, 21, 16, -19));
        this.addCheckpoint(lazyCheckpoint(26, 16, -12, 90, 0));
        this.addCheckpoint(lazyCheckpoint(22, 16, -13, 90, 0));
        this.addCheckpoint(lazyCheckpoint(18, 16, -13, 140, 5));
        this.setQuitPlace(new BlockLocation(26, 16, -7, 180, 0));
        this.setLeadersLocation(new BlockLocation(28, 15, -4));
        this.setCoinsReward(1500);

        this.setIcon(Material.DEAD_BUSH);

        this.setCompletionRewards(15 * 1000, () -> 500);
        this.setCompletionRewards(10 * 1000, () -> 1000);
        this.setCompletionRewards(5 * 1000, () -> 5000);
        this.setCompletionRewards(4 * 1000, new Reward() {
            @Override
            public long coinsReward() {
                return 13500;
            }

            @Override
            public long rubiesReward() {
                return 10;
            }
        });

    }

}
