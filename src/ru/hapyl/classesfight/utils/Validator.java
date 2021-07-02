package ru.hapyl.classesfight.utils;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class Validator {

    /**
     * Helpful validator for spigot.
     * Made by hapyl
     */

    public static boolean isInt(Object obj) {
        return getInt(obj) != -999;
    }

    public static int getInt(Object obj) {
        try {
            return Integer.parseInt((String) obj);
        } catch (NumberFormatException ignored) {
            return -999;
        }
    }

    public static boolean isDouble(Object value) {
        return getDouble(value) != 999D;
    }

    public static double getDouble(Object value) {
        try {
            return Double.parseDouble((String) value);
        } catch (NumberFormatException ignored) {
            return -999D;
        }
    }

    @Deprecated(/* Hard to use because of {final} */)
    public static <T extends Enum<T>> void invokeIfEnumOf(Class<T> enumClass, String value, Consumer<? super T> ifTrue, Consumer<? super String> ifFalse) {
        try {
            ifTrue.accept(Enum.valueOf(enumClass, value.toUpperCase()));
        } catch (Exception e) {
            ifFalse.accept(value);
        }
    }

    @Nullable
    public static <T extends Enum<T>> T getEnumValue(Class<T> enumClass, String value) {
        return getEnumValueOrDefault(enumClass, value, null);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnumValueOrDefault(Class<T> enumClass, String value, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void invokePlayerIfTrue(Player player, boolean condition, Consumer<? super Player> ifTrue, Consumer<? super Player> ifFalse) {
        if (condition) ifTrue.accept(player);
        else ifFalse.accept(player);
    }

}
