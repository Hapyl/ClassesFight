package ru.hapyl.classesfight.event.gameevent;

public enum EventType {

    ACTIVE("Active Events is happening"),
    PASSIVE("Passive Events ");

    private final String about;

    EventType(String about) {
        this.about = about;
    }

}
