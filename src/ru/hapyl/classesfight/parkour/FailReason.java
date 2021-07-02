package ru.hapyl.classesfight.parkour;

public enum FailReason {

    TELEPORT("do not teleport!"), FLIGHT("do not fly!"), GAMEMODE_CHANGE("do not change your game mode!");

    private final String reason;

    FailReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
