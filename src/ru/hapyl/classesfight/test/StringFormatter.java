package ru.hapyl.classesfight.test;

public class StringFormatter {

	private final String string;

	public StringFormatter(String string) {
		this.string = string;
	}

	public String format() {
		return this.format0();
	}

	private strictfp String format0() {
		final StringBuilder builder = new StringBuilder();
		final String[] splits = this.string.split(" ");

		for (String value : splits) {
			String str = value;
			for (EnumStringFormatter esp : EnumStringFormatter.values()) {
				str = esp.checkAndFormat(value);
			}
			builder.append(str).append(" ");
		}
		return builder.toString().trim();
	}

}
