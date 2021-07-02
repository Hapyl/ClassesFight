package ru.hapyl.classesfight.test;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.core.util.JsonUtils;
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
