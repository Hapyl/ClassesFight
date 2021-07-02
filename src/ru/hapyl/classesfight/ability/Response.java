package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class Response {

    public static final Response OK = new Response(null, ResponseStatus.OK);
    public static final Response ERROR = new Response(null, ResponseStatus.ERROR);

    private final String about;
    private final ResponseStatus status;

    public Response(String about, ResponseStatus status) {
        this.about = about;
        this.status = status;
    }

    public static Response error(String why) {
        return new Response(why, ResponseStatus.ERROR);
    }

    public boolean isOk() {
        return this.status == ResponseStatus.OK;
    }

    public boolean isError() {
        return this.status == ResponseStatus.ERROR;
    }

    public void sendMessageIfError(Player player) {
        if (this.isError()) {
            if (this.getReason() == null) {
                return;
            }
            Chat.sendMessage(player, "&cCannot use this! " + this.getReason());
        }
    }

    public String getReason() {
        return about;
    }

    public enum ResponseStatus {
        OK, ERROR
    }

}
