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

package ru.hapyl.classesfight.test;

import org.apache.commons.lang.math.NumberUtils;
import ru.hapyl.classesfight.utils.Icons;

public enum EnumStringFormatter {

    NUMBER(new StringFormatterRule() {
        @Override
        public boolean validate(String c) {
            return NumberUtils.isNumber(c);
        }

        @Override
        public String replace(String c) {
            return "&b" + c;
        }
    }),

    ABILITY_NAME(new StringFormatterRule() {
        @Override
        public boolean validate(String str) {
            return isBetween(str, "{", "}");
        }

        @Override
        public String replace(String str) {
            return Icons.ABILITY_DOT + str;
        }
    }),

    UNDERLINE(new StringFormatterRule() {
        @Override
        public boolean validate(String str) {
            return isBetween(str, "_", "_");
        }

        @Override
        public String replace(String str) {
            return "&n" + str;
        }
    });

    private final StringFormatterRule rule;

    EnumStringFormatter(StringFormatterRule rule) {
        this.rule = rule;
    }

    public String checkAndFormat(String str, String extraPrefix, String extraSuffix) {
        return extraPrefix + (this.rule.validate(str) ? this.rule.replace(str) : str) + extraSuffix;
    }

    public String checkAndFormat(String str) {
        return this.checkAndFormat("", str, "&7");
    }

    private static boolean isBetween(String str, String prefix, String suffix) {
        return str.startsWith(prefix) && str.endsWith(suffix);
    }

}
