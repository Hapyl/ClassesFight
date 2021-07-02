package ru.hapyl.classesfight.parkour;

public enum ParkourDifficulty {

    EASY("&aEasy"),
    NORMAL("&2Normal"),
    HARD("&eHard"),
    VERY_HARD("&cVery Hard"),
    IMPOSSIBLE("&4Impossible");

    private final String name;

    ParkourDifficulty(String display) {
        this.name = display;
    }

    public String getName() {
        return name;
    }
}
