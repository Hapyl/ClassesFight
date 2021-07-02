package ru.hapyl.classesfight.rank;

public enum Rank {

    PLAYER(),
    VIP("&a&lVIP"),
    ADMIN();

    private final String prefix;

    Rank() {
        this("");
    }

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
