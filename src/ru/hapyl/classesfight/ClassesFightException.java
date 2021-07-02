package ru.hapyl.classesfight;


import kz.hapyl.spigotutils.module.chat.Chat;

public class ClassesFightException extends RuntimeException {

    public ClassesFightException() {
        super();
    }

    public ClassesFightException(String var0) {
        super(var0);
        Chat.broadcast("&4An error occurred in ClassesFight! " + var0);
    }

}
