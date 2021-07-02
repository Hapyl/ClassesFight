package ru.hapyl.classesfight.feature;

import kz.hapyl.spigotutils.module.util.CollectionUtils;
import kz.hapyl.spigotutils.module.util.ThreadRandom;

import java.util.Locale;
import java.util.Random;

public class Code {

    private final transient String code;

    public Code(String code) {
        if (code.length() != 12) {
            throw new IllegalArgumentException("code length must be 12 chars long");
        }
        if (!code.matches("[aA-zZ0-9]")) {
            throw new IllegalArgumentException("code does not match provided pattern");
        }
        this.code = code.toUpperCase(Locale.ROOT);
    }

    public String getCode() {
        return code;
    }

    public static Code randomCode() {
        StringBuilder str = new StringBuilder();
        while (str.length() != 12) {
            str.append(randomChar());
        }
        return new Code(str.toString());
    }

    private static String randomChar() {
        final String str = "ABCDEFGHIJKLMNOPQRSTUVWZYZ0123456789";
        return "" + str.toCharArray()[new Random().nextInt(str.length())];
    }

}
