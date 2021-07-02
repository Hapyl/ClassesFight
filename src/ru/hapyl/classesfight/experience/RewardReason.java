package ru.hapyl.classesfight.experience;

public enum RewardReason {

    MINUTE_PLAYED(5, "Minute Played"),
    GAME_WON(50, "Winner"),
    KILL(10, "Kill");

    private final long expAmount;
    private final String reason;

    RewardReason(long expAmount) {
        this(expAmount, "");
    }

    RewardReason(long expAmount, String reason) {
        this.expAmount = expAmount;
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public long getExpAmount() {
        return this.expAmount;
    }
}
