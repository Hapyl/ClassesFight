package ru.hapyl.classesfight.parkour.parkour;

import org.bukkit.Material;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.parkour.Parkour;
import ru.hapyl.classesfight.parkour.ParkourBlock;
import ru.hapyl.classesfight.parkour.ParkourBlockType;

import java.util.ArrayList;
import java.util.List;

public class LobbyParkour extends Parkour {

    private static final List<ParkourBlock> checkPoints = new ArrayList<>();

    private static final ParkourBlock START_LOCATION = new ParkourBlock(ParkourBlockType.START, 4, 14, -3, 180, 0);
    private static final ParkourBlock FINISH_LOCATION = new ParkourBlock(ParkourBlockType.FINISH, 22, 16, 10);

    static {
        checkPoints.add(lazyCheckpoint(7, 18, 2, 37, 2));
        checkPoints.add(lazyCheckpoint(8, 17, -23, 135, 0));
        checkPoints.add(lazyCheckpoint(-14, 18, -21, 55, 23));
        checkPoints.add(lazyCheckpoint(-27, 16, 8, -20, 0));
        checkPoints.add(lazyCheckpoint(-11, 24, 11, -135, 0));
        checkPoints.add(lazyCheckpoint(18, 19, 18, -153, 26));
    }

    public LobbyParkour(String name) {
        super(name, START_LOCATION, FINISH_LOCATION);
        this.setCheckpoints(checkPoints);
        this.setCoinsReward(2500);
        this.setQuitPlace(new BlockLocation(4, 13, -0, 180, 0));
        this.setLeadersLocation(new BlockLocation(25, 15, 11));

        this.setIcon(Material.NETHER_STAR);

        this.setCompletionRewards(30 * 1000, new Reward() {
            @Override
            public long coinsReward() {
                return 15000;
            }

            @Override
            public long rubiesReward() {
                return 5;
            }
        });

        this.setCompletionRewards(45 * 1000, () -> 10000);
        this.setCompletionRewards(60 * 1000, () -> 2500);

    }

}
