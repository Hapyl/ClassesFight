package ru.hapyl.classesfight.feature;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;

public class RedeemableCode {

    private final String name;
    private final Code code;
    private final long validUntil;

    public RedeemableCode(String name, Code code, long validUntil) {
        this.name = name;
        this.code = code;
        this.validUntil = -1L;
    }

    public Code getCode() {
        return code;
    }

    public void redeem(Player player) {

    }

    public boolean haveRedeemed(Player player) {
        return Database.getDatabase(player).getBooleanEntry().getBoolean("code." + this.code.getCode(), false);
    }



}
