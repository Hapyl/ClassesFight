package ru.hapyl.classesfight.minigame;

public enum State {

    PLAYING, SHOWING;

    public boolean asBoolean() {
        return this == PLAYING;
    }

}
