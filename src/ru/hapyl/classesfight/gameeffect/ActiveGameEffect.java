package ru.hapyl.classesfight.gameeffect;

public class ActiveGameEffect {

    private final GameEffectType type;
    private long ticksRemaining;

    public ActiveGameEffect(GameEffectType type, long ticksRemaining) {
        this.type = type;
        this.ticksRemaining = ticksRemaining;
    }

    public GameEffectType getType() {
        return type;
    }

    public long getTicksRemaining() {
        return ticksRemaining;
    }

    public GameEffect getEffect() {
        return this.type.getEffect();
    }

    public void setTicksRemaining(long ticksRemaining) {
        this.ticksRemaining = ticksRemaining;
    }

    public void addTicksRemaining(long ticks) {
        this.ticksRemaining += ticks;
    }

    public void tickDown() {
        this.ticksRemaining--;
    }


}
