/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.feature;

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
