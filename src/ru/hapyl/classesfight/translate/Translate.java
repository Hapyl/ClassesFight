package ru.hapyl.classesfight.translate;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public enum Translate {

    // Common
    NO_PERMISSION("&cYou don't have enough permissions!"),

    TEST_MESSAGE("This is a test locale message for you, {}!"),

    // Scoreboards
    SCORE_GAME("Game"),
    SCORE_MODE("Mode"),
    SCORE_GAME_STATE("State"),
    SCORE_PLAYERS("Players"),
    SCORE_YOU("You, {}"),
    SCORE_PLAYER_CLASS("Class"),
    SCORE_PLAYER_COINS("Coins"),
    SCORE_PLAYER_RUBIES("Rubies");

    private final String key;
    private final String def;

    Translate(String def) {
        // NO_PERMISSION
        // no-permission
        this.key = this.name().toLowerCase().replace("_", "-");
        this.def = def;
    }

    public String getString(Locale locale) {
        return TranslateManager.current().getLocale(locale).getByKeyOr(this.key, this.def);
    }

    public String getString() {
        return this.def;
    }

    public void sendMessage(Player player, Object... replacements) {
        final String format = Chat.bformat(getString(), replacements);
        Chat.sendMessage(player, format);
    }

}
